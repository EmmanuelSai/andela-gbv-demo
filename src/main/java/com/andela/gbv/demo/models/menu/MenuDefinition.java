package com.andela.gbv.demo.models.menu;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class MenuDefinition {
    private int version;
    private Settings settings;
    private Map<String, MenuNode> menus;

    @Data
    public static class Settings {
        private String backKey = "0";
        private String backLabel = "Back";
        private String homeKey = "00";
        private String homeLabel = "Main menu";
        private String exitKey = "EXIT";
        private String exitLabel = "Clear chat";
        private String invalidInputMessage = "Invalid choice. Please try again.";
        private int sessionTimeoutMinutes = 30;
    }

    @Data
    public static class MenuNode {
        private MenuType type;
        private String text;
        private List<MenuOption> options;
        private String source; // DYNAMIC_MENU
        private String itemLabel; // DYNAMIC_MENU
        private String contextKey; // DYNAMIC_MENU
        private String contextLabelKey; // DYNAMIC_MENU
        private String action; // INPUT / ACTION
        private String contextFlag; // ACTION: risk flag name
        private String contextLocation; // ACTION: referral location
        private Validation validation; // INPUT
        private String next; // ACTION auto-navigate
    }

    @Data
    public static class MenuOption {
        private String label;
        private String next;
    }

    @Data
    public static class Validation {
        private String pattern;
        private String errorMessage;
    }

    public enum MenuType {
        MENU, DYNAMIC_MENU, INPUT, ACTION, END
    }
}