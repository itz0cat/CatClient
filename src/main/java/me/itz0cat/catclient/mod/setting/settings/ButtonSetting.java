package me.itz0cat.catclient.mod.setting.settings;

import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.Setting;

public class ButtonSetting extends Setting {
    public Runnable runnable;
    public boolean sameLine;

    public ButtonSetting(String name, Mod parent, Runnable runnable) {
        this.name = name;
        this.parent = parent;
        this.runnable = runnable;
        this.sameLine = false;

        if (parent != null) parent.addSettings(this);
    }

    public ButtonSetting(String name, Mod parent, Runnable runnable, boolean sameLine) {
        this.name = name;
        this.parent = parent;
        this.runnable = runnable;
        this.sameLine = sameLine;

        if (parent != null) parent.addSettings(this);
    }

    public void click() {
        if (runnable != null) {
            runnable.run();
        }
    }
}
