package me.itz0cat.catclient.mod.setting.settings;

import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.Setting;

public class BooleanSetting extends Setting {
	public boolean enabled;

	public BooleanSetting(String name, Mod parent, boolean enabled) {
		this.name = name;
		this.parent = parent;
		this.enabled = enabled;
		if (parent != null) parent.addSettings(this);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void toggle() {
		this.enabled = !this.enabled;
	}
}
