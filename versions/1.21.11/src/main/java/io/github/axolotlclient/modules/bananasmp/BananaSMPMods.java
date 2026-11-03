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

package io.github.axolotlclient.modules.bananasmp;

import java.net.URI;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.options.GenericOption;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BananaSMPMods extends AbstractModule {

	private static final BananaSMPMods INSTANCE = new BananaSMPMods();
	private static final String DISCORD_URL = "https://dsc.gg/itz0cat";

	private final OptionCategory category = OptionCategory.create("bananasmp");

	public static BananaSMPMods getInstance() {
		return INSTANCE;
	}

	@Override
	public void init() {
		category.add(new GenericOption("bananasmp.bone_drop", "bananasmp.action", () -> openDiscordDialog("Bone Drop")));
		category.add(new GenericOption("bananasmp.auto_shop", "bananasmp.action", () -> openDiscordDialog("Auto Shop")));
		category.add(new GenericOption("bananasmp.chat_game", "bananasmp.action", () -> openDiscordDialog("Chat Game")));
		category.add(new GenericOption("bananasmp.coinflip", "bananasmp.action", () -> openDiscordDialog("Coinflip")));

		AxolotlClientConfigCommon.instance().addCategory(category);
	}

	private void openDiscordDialog(String featureName) {
		Screen previous = Minecraft.getInstance().screen;
		Minecraft.getInstance().setScreen(new ConfirmScreen(
			confirmed -> {
				if (confirmed) {
					try {
						Util.getPlatform().openUri(URI.create(DISCORD_URL));
					} catch (Exception ignored) {
					}
				}
				Minecraft.getInstance().setScreen(previous);
			},
			Component.literal(featureName + " - BananaSMP"),
			Component.literal("Join Discord server for the addon for BananaSMP!\n\n" + DISCORD_URL),
			Component.translatable("bananasmp.dialog.discord"),
			Component.translatable("gui.back")
		));
	}
}
