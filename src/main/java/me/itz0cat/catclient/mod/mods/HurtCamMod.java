package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;

public class HurtCamMod extends Mod {
    public final BooleanSetting disableHurtcam = new BooleanSetting("Disable hurtcam", this, false);
    public final BooleanSetting oldHurtcam = new BooleanSetting("Old Hurtcam", this, false);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0, 2, 0.1);
    public HurtCamMod() {
        super("Hurt Cam", "Change the hurtcam.", "\uF030");
    }
}
