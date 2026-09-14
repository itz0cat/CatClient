package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;

public class NametagsMod extends Mod {
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, false);
    public final NumberSetting opacity = new NumberSetting("Opacity", this, 0.25, 0, 1, 0.01);

    public NametagsMod() {
        super("Nametags", "Change nametags.", "\uF02C");
    }
}
