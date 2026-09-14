package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.ModeSetting;

public class TimeChangerMod extends Mod {
    public final ModeSetting time = new ModeSetting("Time", this, "Day", "Day", "Night");

    public TimeChangerMod() {
        super("Time Changer", "Change time.", "\uF185");
    }

    public int getTimeInt() {
        if (time.is("Day")) return 0;
        else return 24000;
    }
}
