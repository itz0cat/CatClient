package me.itz0cat.catclient;
import me.itz0cat.catclient.api.config.ConfigManager;
import me.itz0cat.catclient.api.discord.DiscordClient;
import me.itz0cat.catclient.api.event.events.OverlayReloadListener;
import me.itz0cat.catclient.api.event.events.WorldRenderEvent;
import me.itz0cat.catclient.api.event.orbit.EventBus;
import me.itz0cat.catclient.api.event.orbit.IEventBus;
import me.itz0cat.catclient.api.helpers.CPSHelper;
import me.itz0cat.catclient.api.helpers.CapeHelper;
import me.itz0cat.catclient.api.helpers.IndicatorHelper;
import me.itz0cat.catclient.menu.*;
import me.itz0cat.catclient.mod.ModManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static me.itz0cat.catclient.api.helpers.CapeHelper.getPlayerCosmetics;

public final class CatClient implements ModInitializer {
	public static String name = "CatClient";
	public static String packagePrefix = "me.itz0cat.catclient";

	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static IEventBus EVENTBUS = new EventBus();
	public static CatClient INSTANCE;

	public static UUID uuid = null;

	public static Path nextTick = null;

	public static ArrayList<Map.Entry<String, String>> partneredServers = new ArrayList<>();
	public static ArrayList<String> starServers = new ArrayList<>();
	public static final Logger LOGGER = LoggerFactory.getLogger("catclient");
	public static ModManager modManager() {
		return INSTANCE.modManager;
	}

	public static ConfigManager configManager() {
		return INSTANCE.configManager;
	}

	public CatClient() {
		INSTANCE = this;
	}

	public ModManager modManager;
	public ConfigManager configManager;

	public void init() {
		this.modManager = new ModManager();
		this.configManager = new ConfigManager();

		EVENTBUS.registerLambdaFactory(packagePrefix, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		EVENTBUS.subscribe(modManager);
		EVENTBUS.subscribe(MainMenuButtons.class);
		EVENTBUS.subscribe(CPSHelper.class);
		WorldRenderEvents.END_MAIN.register((context) -> { EVENTBUS.post(WorldRenderEvent.get(context)); });

		MainMenuButtons.toggleVisibility();
		FirstMenu.toggleVisibility();
		ModMenu.toggleVisibility();
		ModSettings.toggleVisibility();
		SideMenu.toggleVisibility();
		ProfilesMenu.toggleVisibility();
		CosmeticsMenu.toggleVisibility();
		this.configManager.loadConfig();

		DiscordClient.init();
		CapeHelper.init();
		partneredServers.add(new AbstractMap.SimpleEntry<>("as.catpvp.xyz", "CatPvP AS"));
		partneredServers.add(new AbstractMap.SimpleEntry<>("eu.catpvp.xyz", "CatPvP EU"));
		partneredServers.add(new AbstractMap.SimpleEntry<>("flakepvp.me", "FlakePvP"));

		// Cat PvP
		starServers.add("au.catpvp.xyz");
		starServers.add("me.catpvp.xyz");
		starServers.add("east.catpvp.xyz");
		starServers.add("west.catpvp.xyz");
		starServers.add("as.catpvp.xyz");
		starServers.add("eu.catpvp.xyz");

		// Cat PvP but .com
		starServers.add("au.catpvp.com");
		starServers.add("me.catpvp.com");
		starServers.add("east.catpvp.com");
		starServers.add("west.catpvp.com");
		starServers.add("as.catpvp.com");
		starServers.add("eu.catpvp.com");

		// Anarchia
		starServers.add("play.anarchianetwork.com");

		// Tropical Club
		starServers.add("tropicalclub.wisteria.host");

		// Flake PvP
		starServers.add("flakepvp.me");
		starServers.add("as.flakepvp.net");
		starServers.add("me.flakepvp.net");
	}


	private int tick = 0;
	@Override
	public void onInitialize() {
		//init();
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> IndicatorHelper.enableClient());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> IndicatorHelper.disableClient());

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> CatClient.configManager().saveConfig());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> CatClient.configManager().saveConfig());

		ClientTickEvents.END_WORLD_TICK.register((client) -> {
			OverlayReloadListener.callEvent();
			if(tick != 100) {
				tick++;
			} else {
				tick = 0;
				IndicatorHelper.getUsers();
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if(nextTick != null) {
				CatClient.configManager().loadConfigFromPath(nextTick);
				nextTick = null;
			}
			if(uuid == null && mc.getSession().getUuidOrNull() != null) {
				uuid = mc.getSession().getUuidOrNull();
				getPlayerCosmetics();
			}
		});
	}
}
