package com.andela.gbv.demo.utils;

import com.andela.gbv.demo.models.menu.MenuDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MenuLoader {

    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());

    public Map<String, MenuDefinition> loadAll() {
        Map<String, MenuDefinition> result = new HashMap<>();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:menus/menu.*.yml");
            for (Resource r : resources) {
                String filename = r.getFilename(); // menu.en.yml
                String lang = filename.substring(5, filename.length() - 4);
                try (InputStream in = r.getInputStream()) {
                    MenuDefinition def = yaml.readValue(in, MenuDefinition.class);
                    validate(def, lang);
                    result.put(lang, def);
                    log.info("Loaded menu for language '{}' with {} nodes",
                            lang, def.getMenus().size());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load menus", e);
        }
        return result;
    }

    private void validate(MenuDefinition def, String lang) {
        Map<String, MenuDefinition.MenuNode> nodes = def.getMenus();
        nodes.forEach((id, node) -> {
            if (node.getType() == MenuDefinition.MenuType.MENU && node.getOptions() != null) {
                for (MenuDefinition.MenuOption opt : node.getOptions()) {
                    if (!nodes.containsKey(opt.getNext())) {
                        throw new IllegalStateException(
                                "[" + lang + "] Node '" + id + "' -> missing node '" + opt.getNext() + "'");
                    }
                }
            }
            if (node.getNext() != null && !nodes.containsKey(node.getNext())) {
                throw new IllegalStateException(
                        "[" + lang + "] Node '" + id + "' next -> missing node '" + node.getNext() + "'");
            }
        });
    }

}
