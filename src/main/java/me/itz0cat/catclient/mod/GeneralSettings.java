package me.itz0cat.catclient.mod;

import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.KeybindSetting;
import org.lwjgl.glfw.GLFW;

public class GeneralSettings extends Mod {
    public final ColorSetting mainColor = new ColorSetting("Main Color", this, new JColor(0.00f, 0.78f, 1.00f), false);
    public final KeybindSetting openMenu = new KeybindSetting("Open Menu", GLFW.GLFW_KEY_RIGHT_SHIFT, this);
    public final BooleanSetting fullbright = new BooleanSetting("Fullbright", this, true);
    public final BooleanSetting minimalViewBob = new BooleanSetting("Minimal View Bob", this, false);
    public final BooleanSetting showOwnNametag = new BooleanSetting("Show Own Nametag", this, false);
    public final BooleanSetting showCosmetics = new BooleanSetting("Show Cosmetics", this, false);
    public final BooleanSetting showClientBadges = new BooleanSetting("Show Client Badges", this, true);
    public final BooleanSetting lowShield = new BooleanSetting("Low Shield", this, false);
    public final BooleanSetting lowFire = new BooleanSetting("Low Fire", this, false);
    public final BooleanSetting numericalPing = new BooleanSetting("Numerical Ping", this, false);
    public final BooleanSetting smallPing = new BooleanSetting("Small Ping", this, false);
    public final BooleanSetting msPing = new BooleanSetting("Ping MS text", this, false);
    public final BooleanSetting hourFormat = new BooleanSetting("24 Hour Format", this, true);

    public final BooleanSetting showInChat = new BooleanSetting("Show Mods in Chat", this, true);
    public final BooleanSetting showInInventory = new BooleanSetting("Show Mods in Inventory", this, true);

    public final BooleanSetting blurChat = new BooleanSetting("Chat", this, false);
    public final BooleanSetting blurInventory = new BooleanSetting("Inventory", this, false);

    public final BooleanSetting darkenInventory = new BooleanSetting("Inventory", this, true);

    public final BooleanSetting unlimitedChatHistory = new BooleanSetting("Unlimited Chat History", this, false);
    public final BooleanSetting stackChatMessages = new BooleanSetting("Stack Chat Messages", this, false);
    public final BooleanSetting enableDiscordRPC = new BooleanSetting("Enable Discord RPC", this, true);
    public final BooleanSetting showAddress = new BooleanSetting("Show Server Address", this, true);

    public GeneralSettings() {
        super("General Settings", "General client settings.", "\uF085");
        category = Category.SETTINGS;
    }
}