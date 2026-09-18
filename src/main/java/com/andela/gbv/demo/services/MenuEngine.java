package com.andela.gbv.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.andela.gbv.demo.models.UserSession;
import com.andela.gbv.demo.models.menu.DynamicItem;
import com.andela.gbv.demo.models.menu.DynamicMenuSource;
import com.andela.gbv.demo.models.menu.MenuDefinition;
import com.andela.gbv.demo.services.handler.MenuActionHandler;
import com.andela.gbv.demo.utils.MenuLoader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MenuEngine {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");

    private final Map<String, MenuDefinition> definitions;
    private final Map<String, MenuActionHandler> handlers;
    private final Map<String, DynamicMenuSource> sources;
    private final SessionManager sessionManager;
    private final TwilioService twilioService;

    public MenuEngine(MenuLoader loader,
            List<MenuActionHandler> handlerList,
            List<DynamicMenuSource> sourceList,
            SessionManager sessionManager,
            TwilioService twilioService) {
        this.definitions = loader.loadAll();
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(MenuActionHandler::getName, h -> h));
        this.sources = sourceList.stream()
                .collect(Collectors.toMap(DynamicMenuSource::getName, s -> s));
        this.sessionManager = sessionManager;
        this.twilioService = twilioService;
        log.info("Menu engine ready: {} languages, {} handlers, {} dynamic sources",
                definitions.size(), handlers.size(), sources.size());
    }

    public void handleInput(String userNumber, String input) {
        UserSession session = sessionManager.getOrCreate(userNumber);
        MenuDefinition def = resolveDefinition(session);
        MenuDefinition.Settings settings = def.getSettings();

        String trimmed = input == null ? "" : input.trim();

        // 1. Global quick-exit (works at any time)
        if (settings.getExitKey().equalsIgnoreCase(trimmed)) {
            quickExit(userNumber, settings);
            return;
        }

        // 2. First-ever message -> render main menu, don't try to parse it
        if (!session.isMenuRendered()) {
            session.setMenuRendered(true);
            render(userNumber, "main", session, def);
            return;
        }

        // 3. Global home
        if (settings.getHomeKey().equals(trimmed)) {
            render(userNumber, "main", session, def);
            return;
        }

        // 4. Global back
        if (settings.getBackKey().equals(trimmed) && session.getHistory().size() > 1) {
            session.getHistory().pop(); // remove current
            String parent = session.getHistory().pop();
            render(userNumber, parent, session, def);
            return;
        }

        // 5. Dispatch based on current node type
        MenuDefinition.MenuNode node = def.getMenus().get(session.getCurrentNodeId());
        if (node == null) {
            render(userNumber, "main", session, def);
            return;
        }

        switch (node.getType()) {
            case MENU -> handleStaticChoice(userNumber, trimmed, node, session, def);
            case DYNAMIC_MENU -> handleDynamicChoice(userNumber, trimmed, node, session, def);
            case INPUT -> handleInputNode(userNumber, trimmed, node, session, def);
            case ACTION, END -> render(userNumber, node.getNext(), session, def);
        }
    }

    private MenuDefinition resolveDefinition(UserSession session) {
        MenuDefinition def = definitions.get(session.getLanguage());
        if (def == null) {
            log.warn("No menu for language '{}', falling back to 'en'", session.getLanguage());
            def = definitions.get("en");
        }
        return def;
    }

    private void quickExit(String userNumber, MenuDefinition.Settings settings) {
        sessionManager.clear(userNumber);
        twilioService.sendWhatsAppMessage(userNumber,
                "Your appointment reminder: Tuesday 3pm. Reply STOP to unsubscribe.");
        log.info("Quick exit triggered for {}", userNumber);
    }

    private void render(String userNumber, String nodeId, UserSession session, MenuDefinition def) {
        MenuDefinition.MenuNode node = def.getMenus().get(nodeId);
        if (node == null)
            throw new IllegalStateException("Unknown node: " + nodeId);

        switch (node.getType()) {
            case MENU -> renderStaticMenu(userNumber, nodeId, node, session, def);
            case DYNAMIC_MENU -> renderDynamicMenu(userNumber, nodeId, node, session, def);
            case INPUT -> renderInput(userNumber, nodeId, node, session, def);
            case ACTION -> executeAction(userNumber, nodeId, node, session, def);
            case END -> {
                /* nothing */ }
        }
    }

    private void renderStaticMenu(String userNumber, String nodeId,
            MenuDefinition.MenuNode node,
            UserSession session, MenuDefinition def) {
        StringBuilder sb = new StringBuilder(interpolate(node.getText(), session)).append("\n");
        List<MenuDefinition.MenuOption> options = node.getOptions();
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(". ").append(options.get(i).getLabel()).append("\n");
        }
        appendNavHints(sb, def.getSettings());
        twilioService.sendWhatsAppMessage(userNumber, sb.toString());
        pushHistory(session, nodeId);
        sessionManager.save(session);
    }

    private void renderDynamicMenu(String userNumber, String nodeId,
            MenuDefinition.MenuNode node,
            UserSession session, MenuDefinition def) {
        DynamicMenuSource source = sources.get(node.getSource());
        if (source == null)
            throw new IllegalStateException("Unknown source: " + node.getSource());

        List<DynamicItem> items = source.resolve(userNumber, session.getContext());
        if (items.isEmpty()) {
            twilioService.sendWhatsAppMessage(userNumber, "No options available right now.");
            render(userNumber, "main", session, def);
            return;
        }

        List<String> keys = items.stream().map(DynamicItem::key).toList();
        List<String> labels = items.stream().map(DynamicItem::label).toList();
        session.getContext().put("_dynKeys_" + nodeId, keys);
        session.getContext().put("_dynLabels_" + nodeId, labels);

        StringBuilder sb = new StringBuilder(interpolate(node.getText(), session)).append("\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append(i + 1).append(". ").append(items.get(i).label()).append("\n");
        }
        appendNavHints(sb, def.getSettings());
        twilioService.sendWhatsAppMessage(userNumber, sb.toString());
        pushHistory(session, nodeId);
        sessionManager.save(session);
    }

    private void renderInput(String userNumber, String nodeId,
            MenuDefinition.MenuNode node,
            UserSession session, MenuDefinition def) {
        twilioService.sendWhatsAppMessage(userNumber, interpolate(node.getText(), session));
        pushHistory(session, nodeId);
        sessionManager.save(session);
    }

    private void executeAction(String userNumber, String nodeId,
            MenuDefinition.MenuNode node,
            UserSession session, MenuDefinition def) {
        MenuActionHandler handler = handlers.get(node.getAction());
        if (handler == null)
            throw new IllegalStateException("Unknown action: " + node.getAction());

        // Pass contextual hints and the session itself so handlers can read/update
        // state
        if (node.getContextFlag() != null) {
            session.getContext().put("_riskFlag", node.getContextFlag());
        }
        if (node.getContextLocation() != null) {
            session.getContext().put("_location", node.getContextLocation());
        }
        session.getContext().put("_session", session);

        handler.handle(userNumber, null, session.getContext());
        sessionManager.save(session);

        if (node.getNext() != null) {
            render(userNumber, node.getNext(), session, def);
        }
    }

    private void handleStaticChoice(String userNumber, String input,
            MenuDefinition.MenuNode node,
            UserSession session, MenuDefinition def) {
        int choice = parseChoice(input, node.getOptions().size());
        if (choice < 0) {
            twilioService.sendWhatsAppMessage(userNumber, def.getSettings().getInvalidInputMessage());
            return;
        }
        render(userNumber, node.getOptions().get(choice).getNext(), session, def);
    }

    @SuppressWarnings("unchecked")
    private void handleDynamicChoice(String userNumber, String input,
            MenuDefinition.MenuNode node,
            UserSession session, MenuDefinition def) {
        String nodeId = session.getCurrentNodeId();
        List<String> keys = (List<String>) session.getContext().get("_dynKeys_" + nodeId);
        List<String> labels = (List<String>) session.getContext().get("_dynLabels_" + nodeId);
        if (keys == null) {
            render(userNumber, "main", session, def);
            return;
        }

        int choice = parseChoice(input, keys.size());
        if (choice < 0) {
            twilioService.sendWhatsAppMessage(userNumber, def.getSettings().getInvalidInputMessage());
            return;
        }
        if (node.getContextKey() != null) {
            session.getContext().put(node.getContextKey(), keys.get(choice));
        }
        if (node.getContextLabelKey() != null) {
            session.getContext().put(node.getContextLabelKey(), labels.get(choice));
        }
        render(userNumber, node.getNext(), session, def);
    }

    private void handleInputNode(String userNumber, String input,
            MenuDefinition.MenuNode node,
            UserSession session, MenuDefinition def) {
        if (node.getValidation() != null && node.getValidation().getPattern() != null) {
            if (!input.matches(node.getValidation().getPattern())) {
                twilioService.sendWhatsAppMessage(userNumber, node.getValidation().getErrorMessage());
                return;
            }
        }
        MenuActionHandler handler = handlers.get(node.getAction());
        if (handler == null)
            throw new IllegalStateException("Unknown action: " + node.getAction());

        session.getContext().put("_session", session);
        handler.handle(userNumber, input, session.getContext());
        sessionManager.save(session);

        if (node.getNext() != null)
            render(userNumber, node.getNext(), session, def);
    }

    private String interpolate(String template, UserSession session) {
        if (template == null)
            return "";
        Matcher m = PLACEHOLDER.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            Object val = session.getContext().getOrDefault(m.group(1), "");
            m.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(val)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private void appendNavHints(StringBuilder sb, MenuDefinition.Settings s) {
        sb.append("\n").append(s.getBackKey()).append(". ").append(s.getBackLabel()).append("\n");
        sb.append(s.getHomeKey()).append(". ").append(s.getHomeLabel());
    }

    private void pushHistory(UserSession session, String nodeId) {
        session.getHistory().push(nodeId);
        session.setCurrentNodeId(nodeId);
        if (session.getHistory().size() > 10) {
            Deque<String> trimmed = new ArrayDeque<>();
            Iterator<String> it = session.getHistory().iterator();
            for (int i = 0; i < 10 && it.hasNext(); i++)
                trimmed.push(it.next());
            session.setHistory(trimmed);
        }
    }

    private int parseChoice(String input, int max) {
        try {
            int n = Integer.parseInt(input.trim());
            return (n >= 1 && n <= max) ? n - 1 : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
