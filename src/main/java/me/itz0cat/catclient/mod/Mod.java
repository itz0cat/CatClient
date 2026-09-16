package me.itz0cat.catclient.mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.hud.HudPosition;
import me.itz0cat.catclient.mod.setting.Setting;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

public abstract class Mod {
    protected MinecraftClient mc = MinecraftClient.getInstance();

    public String name, description, icon;
    public Category category = Category.HUD;
    public List<Setting> settings = new ArrayList<>();
    private boolean enabled;
    private boolean showOptions;
    public boolean isFocused = false;
    public HudPosition updatedPos = new HudPosition(0, 0);
    public HudPosition position = new HudPosition();

    public Mod(String name, String description, @Nullable String... icon) {
        if (icon != null && icon.length > 0) this.icon = icon[0];
        this.name = name;
        this.description = description;
        this.category = assignCategory(name);

        enabled = false;
        showOptions = false;
    }

    private Category assignCategory(String modName) {
        if (modName == null) return Category.HUD;
        return switch (modName) {
            case "Aim Assist", "Hitbox", "Reach Display" -> Category.COMBAT;
            case "Toggle Sprint", "Toggle Sneak" -> Category.MOVEMENT;
            case "Block Overlay", "Fullbright", "Freelook", "Hit Color", "Hurt Cam", "Nametags", "Time Changer", "Zoom", "Scoreboard" -> Category.RENDER;
            case "General Settings" -> Category.SETTINGS;
            default -> Category.HUD;
        };
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void toggle() {
        if (isEnabled()) {
            disable();
        } else {
            enable();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            CatClient.EVENTBUS.subscribe(this);
        } else {
            CatClient.EVENTBUS.unsubscribe(this);
        }
    }

    public boolean showOptions() {
        return showOptions;
    }

    public void toggleShowOptions() {
        this.showOptions = !this.showOptions;
    }

    public void enable() {
        onEnable();
        setEnabled(true);
    }

    public void disable() {
        onDisable();
        setEnabled(false);
    }

    public void onEnable() {}

    public void onDisable() {}

    public boolean nullCheck() {
        return mc.player != null && mc.world != null;
    }

    public void cleanStrings() {
        this.setName(null);
        this.setDescription(null);

        for (Setting setting : settings) {
            setting.name = null;
        }
    }
}
