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

package io.github.axolotlclient.modules.resourcepack;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.api.Constants;
import io.github.axolotlclient.util.NetworkUtil;
import io.github.axolotlclient.util.ThreadExecuter;
import io.github.axolotlclient.util.notifications.Notifications;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class CatResourcePackManager {

	public static final List<String> PACK_NAMES = List.of(
		"Unique Dark - Lite - 1.20.2-26.x.zip",
		"geofont-mc-v2.1.3.zip",
		"! §3§6Small Held Items v2.1.zip"
	);

	private static boolean initialized = false;

	public static void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		CompletableFuture.runAsync(CatResourcePackManager::checkAndLoadPacks, ThreadExecuter.service());
	}

	private static void checkAndLoadPacks() {
		try {
			Path gameDir = FabricLoader.getInstance().getGameDir();
			Path resourcePacksDir = gameDir.resolve("resourcepacks");
			if (!Files.isDirectory(resourcePacksDir)) {
				Files.createDirectories(resourcePacksDir);
			}

			HttpClient client = NetworkUtil.createHttpClient();
			String baseUrl = Constants.getApiUrl();
			if (!baseUrl.endsWith("/")) {
				baseUrl += "/";
			}

			boolean newlyAdded = false;

			for (String packName : PACK_NAMES) {
				Path packFile = resourcePacksDir.resolve(packName);
				if (!Files.exists(packFile) && AxolotlClientConfigCommon.instance().apiEnabled.get()) {
					// Download from backend
					String encoded = URLEncoder.encode(packName, StandardCharsets.UTF_8).replace("+", "%20");
					String url = baseUrl + "resourcepacks/" + encoded;
					try {
						HttpRequest req = HttpRequest.newBuilder(URI.create(url))
							.header("User-Agent", NetworkUtil.getUserAgent())
							.GET()
							.build();
						HttpResponse<InputStream> res = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
						if (res.statusCode() == 200) {
							Files.copy(res.body(), packFile, StandardCopyOption.REPLACE_EXISTING);
							AxolotlClientCommon.getInstance().getLogger().info("Downloaded resource pack: {}", packName);
						}
					} catch (Exception dlEx) {
						AxolotlClientCommon.getInstance().getLogger().debug("Could not download {}: {}", packName, dlEx.getMessage());
					}
				}

				if (Files.exists(packFile)) {
					String packId = "file/" + packName;
					Minecraft mc = Minecraft.getInstance();
					if (mc != null && mc.options != null && mc.options.resourcePacks != null) {
						List<String> activePacks = new ArrayList<>(mc.options.resourcePacks);
						if (!activePacks.contains(packId)) {
							activePacks.add(packId);
							mc.options.resourcePacks = activePacks;
							mc.options.save();
							newlyAdded = true;
						}
					}
				}
			}

			if (newlyAdded) {
				Notifications.getInstance().addStatus(
					Component.literal("CatClient Resource Packs"),
					Component.literal("Applied recommended client resource packs!")
				);
			}

		} catch (Exception e) {
			AxolotlClientCommon.getInstance().getLogger().debug("CatResourcePackManager error: " + e.getMessage());
		}
	}
}
