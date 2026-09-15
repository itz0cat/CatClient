package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.event.events.TickEvent;
import me.itz0cat.catclient.api.event.orbit.EventHandler;
import me.itz0cat.catclient.api.font.JColor;

import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class HitboxMod extends Mod {
    public final ColorSetting boxColor = new ColorSetting("Box Color", this, new JColor(1f,1f,1f), true);

    public HitboxMod() {
        super("Hitbox", "Change the hitbox.", "\uF0C8");
    }
}
