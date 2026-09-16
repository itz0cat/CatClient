package me.itz0cat.catclient.mod.setting.settings;

import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.Setting;

import java.util.Arrays;

public class ModeSetting extends Setting {
	public int index;
	public String[] modes;

	public ModeSetting(String name, Mod parent, String defaultMode, String... modes) {
		this.name = name;
		this.parent = parent;
		this.modes = modes;
		this.index = Arrays.stream(this.modes).toList().indexOf(defaultMode);
		if (parent != null) parent.addSettings(this);
	}

	public String getMode() {
		return this.modes[this.index];
	}

	public void setMode(String mode) {
		int i = Arrays.stream(this.modes).toList().indexOf(mode);
		if (i >= 0) {
			this.index = i;
		}
	}

	public boolean is(String mode) {
		return (this.index == Arrays.stream(this.modes).toList().indexOf(mode));
	}

	public void cycle() {
		if (this.index < this.modes.length - 1) {
			this.index++;
		} else {
			this.index = 0;
		}
	}
}