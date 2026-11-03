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

package io.github.axolotlclient.modules.updater;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.NetworkUtil;
import io.github.axolotlclient.util.ThreadExecuter;
import io.github.axolotlclient.util.notifications.Notifications;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;

public class CatAutoUpdater {

	private static final String RELEASES_API = "https://api.github.com/repos/itz0cat/CatClient/releases/latest";
	private static boolean updateChecked = false;

	public static void init() {
		if (updateChecked) {
			return;
		}
		updateChecked = true;

		CompletableFuture.runAsync(CatAutoUpdater::checkAndApplyUpdate, ThreadExecuter.service());
	}

	private static void checkAndApplyUpdate() {
		try {
			if (!AxolotlClientConfigCommon.instance().apiEnabled.get()) {
				return;
			}

			HttpClient client = NetworkUtil.createHttpClient();
			HttpRequest request = HttpRequest.newBuilder(URI.create(RELEASES_API))
				.header("User-Agent", NetworkUtil.getUserAgent())
				.header("Accept", "application/vnd.github.v3+json")
				.GET()
				.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				return;
			}

			JsonObject json = GsonHelper.fromJson(response.body());
			if (json == null || !json.has("tag_name")) {
				return;
			}

			String tagName = json.get("tag_name").getAsString(); // e.g. "v1.2.0"
			String latestVer = tagName.startsWith("v") ? tagName.substring(1) : tagName;
			String currentVer = AxolotlClientCommon.MOD_VERSION; // e.g. "1.2.0"

			if (!isNewerVersion(latestVer, currentVer)) {
				return;
			}

			JsonArray assets = json.getAsJsonArray("assets");
			if (assets == null) {
				return;
			}

			String downloadUrl = null;
			String targetFileName = null;
			for (JsonElement el : assets) {
				JsonObject asset = el.getAsJsonObject();
				String name = asset.get("name").getAsString();
				if (name.endsWith(".jar") && !name.contains("-sources") && !name.contains("-dev")) {
					downloadUrl = asset.get("browser_download_url").getAsString();
					targetFileName = name;
					break;
				}
			}

			if (downloadUrl == null || targetFileName == null) {
				return;
			}

			Path modsFolder = FabricLoader.getInstance().getGameDir().resolve("mods");
			if (!Files.isDirectory(modsFolder)) {
				Files.createDirectories(modsFolder);
			}

			Path newJarPath = modsFolder.resolve(targetFileName);
			Path tempJarPath = modsFolder.resolve(targetFileName + ".download");

			HttpRequest dlRequest = HttpRequest.newBuilder(URI.create(downloadUrl))
				.header("User-Agent", NetworkUtil.getUserAgent())
				.GET()
				.build();

			HttpResponse<InputStream> dlResponse = client.send(dlRequest, HttpResponse.BodyHandlers.ofInputStream());
			if (dlResponse.statusCode() != 200) {
				return;
			}

			try (InputStream in = dlResponse.body()) {
				Files.copy(in, tempJarPath, StandardCopyOption.REPLACE_EXISTING);
			}
			Files.move(tempJarPath, newJarPath, StandardCopyOption.REPLACE_EXISTING);

			// Locate old jar to remove on shutdown
			Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("catclient");
			Path oldJar = null;
			if (modContainer.isPresent()) {
				for (Path p : modContainer.get().getOrigin().getPaths()) {
					if (p != null && Files.isRegularFile(p) && !p.toAbsolutePath().equals(newJarPath.toAbsolutePath())) {
						oldJar = p.toAbsolutePath();
						break;
					}
				}
			}

			final Path finalOldJar = oldJar;
			if (finalOldJar != null) {
				scheduleOldJarDeletion(finalOldJar);
			}

			Notifications.getInstance().addStatus(
				Component.literal("CatClient Auto-Updater"),
				Component.literal("Downloaded " + tagName + "! Restart Minecraft to apply.")
			);
			AxolotlClientCommon.getInstance().getLogger().info("CatClient updated to {}! Saved to {}", tagName, newJarPath);

		} catch (Exception e) {
			AxolotlClientCommon.getInstance().getLogger().debug("CatAutoUpdater encountered an issue: " + e.getMessage());
		}
	}

	private static void scheduleOldJarDeletion(Path oldJar) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				Files.deleteIfExists(oldJar);
			} catch (Exception ignored) {
			}
		}));

		if (isWindows()) {
			try {
				long pid = ProcessHandle.current().pid();
				String cmd = String.format("cmd.exe /c \"timeout /t 3 >nul & del /f /q \"%s\"\"", oldJar.toString());
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					try {
						new ProcessBuilder("cmd.exe", "/c", "start", "/min", "cmd.exe", "/c", cmd).start();
					} catch (Exception ignored) {
					}
				}));
			} catch (Throwable ignored) {
			}
		}
	}

	private static boolean isWindows() {
		return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
	}

	private static boolean isNewerVersion(String latest, String current) {
		try {
			String[] lParts = latest.split("[+\\.-]");
			String[] cParts = current.split("[+\\.-]");
			int len = Math.max(lParts.length, cParts.length);
			for (int i = 0; i < len; i++) {
				int l = i < lParts.length ? parseOrZero(lParts[i]) : 0;
				int c = i < cParts.length ? parseOrZero(cParts[i]) : 0;
				if (l > c) return true;
				if (l < c) return false;
			}
		} catch (Exception e) {
			return !latest.equalsIgnoreCase(current);
		}
		return false;
	}

	private static int parseOrZero(String s) {
		try {
			return Integer.parseInt(s.replaceAll("\\D+", ""));
		} catch (Exception e) {
			return 0;
		}
	}
}
