package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.mod.Category;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;

public class CrosshairMod extends Mod {
    public final ColorSetting color = new ColorSetting("Color", this, new JColor(1f, 1f, 1f, 1f), true);
    public final ColorSetting targetColor = new ColorSetting("Target Color", this, new JColor(1f, 0f, 0f, 1f), true);

    public boolean[][] crosshair = new boolean[][]{
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, true, true, false, true, false, true, true, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false}
    };

    public CrosshairMod() {
        super("Crosshair", "Change the crosshair.", "\uF05B");
        this.category = Category.RENDER;
    }
}
