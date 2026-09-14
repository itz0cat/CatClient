package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.OverlayReloadListener;
import me.itz0cat.catclient.api.event.events.TickEvent;
import me.itz0cat.catclient.api.event.orbit.EventHandler;
import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;

public class HitColorMod extends Mod {
    public final ColorSetting hitColor = new ColorSetting("Hit Color", this, new JColor(0.7f, 0f, 0f, 0.75f), true);
    public HitColorMod() {
        super("Hit Color", "Change the hit color.", "\uF53F");
    }
}
