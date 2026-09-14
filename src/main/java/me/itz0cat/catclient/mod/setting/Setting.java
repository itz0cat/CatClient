package me.itz0cat.catclient.mod.setting;

import me.itz0cat.catclient.mod.Mod;

public abstract class Setting {
	public String name;
	public Mod parent;

	public String getName() {
		return name;
	}
}