/*
 * Copyright © 2026 itz0cat & CatClient Contributors
 *
 * This file is part of CatClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.cheat;

public final class PrestigeBridge {
	public static void set(String option, boolean on) {
		try {
			Class<?> p = Class.forName("juxxl.prestige.Prestige");
			Object inst = p.getField("INSTANCE").get(null);
			if (inst == null) return;
			Object mgr = p.getField("moduleManager").get(inst);
			Class<?> mod = Class.forName("juxxl.prestige.module.Module");
			for (Object m : (java.util.List<?>) mgr.getClass().getMethod("getModules").invoke(mgr)) {
				String n = mod.getMethod("getName").invoke(m).toString();
				if (n.replace(" ", "").equalsIgnoreCase(option) || (option.equalsIgnoreCase("prestige") && n.equalsIgnoreCase("clickgui"))) {
					mod.getMethod("setEnabled", boolean.class).invoke(m, on);
					return;
				}
			}
		} catch (Throwable ignored) {}
	}
}
