package me.itz0cat.catclient.mod;

public enum Category {
    ALL("All"),
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    HUD("HUD"),
    SETTINGS("Settings");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return displayName;
    }
}
