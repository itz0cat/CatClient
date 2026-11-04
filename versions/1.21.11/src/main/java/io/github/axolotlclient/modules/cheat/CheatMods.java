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

import com.mojang.blaze3d.platform.InputConstants;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.DoubleOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.hud.HudEditScreen;
import io.github.axolotlclient.util.keybinds.KeyBinds;
import java.lang.reflect.Field;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Cheat Modules section ported from Prestige utility client plus Cartcore, SpearCore, and MaceCore integrations.
 * Provides configurable module options organized under Combat, Cart, Spear, Mace, Misc, Render, and Client sub-sections.
 * Automatically bridges configurations directly into Cartcore, SpearCore, MaceCore, and Prestige instances natively.
 */
public class CheatMods extends AbstractModule {

	private static final CheatMods INSTANCE = new CheatMods();

	// Root section for Cheat
	private final OptionCategory category = OptionCategory.create("cheat");

	// Sub-sections
	private final OptionCategory combat = OptionCategory.create("cheat.combat");
	private final OptionCategory cartSection = OptionCategory.create("cheat.cart");
	private final OptionCategory spearSection = OptionCategory.create("cheat.spear");
	private final OptionCategory maceSection = OptionCategory.create("cheat.mace");
	private final OptionCategory misc = OptionCategory.create("cheat.misc");
	private final OptionCategory render = OptionCategory.create("cheat.render");
	private final OptionCategory clientSection = OptionCategory.create("cheat.client");

	// Keybind for opening Cheat category directly
	public KeyMapping openCheatKey;

	private boolean audited = false;

	// ==========================================
	// Combat Modules (23) - Ported from Prestige
	// ==========================================
	public final BooleanOption aimAssist = new BooleanOption("cheat.combat.aim_assist", false, v -> PrestigeBridge.set("aimAssist", v));
	public final BooleanOption autoMace = new BooleanOption("cheat.combat.auto_mace", false, v -> PrestigeBridge.set("autoMace", v));
	public final BooleanOption maceSwap = new BooleanOption("cheat.combat.mace_swap", false, v -> PrestigeBridge.set("maceSwap", v));
	public final BooleanOption autoWindCharge = new BooleanOption("cheat.combat.auto_wind_charge", false, v -> PrestigeBridge.set("autoWindCharge", v));
	public final BooleanOption windChargeKey = new BooleanOption("cheat.combat.wind_charge_key", false, v -> PrestigeBridge.set("windChargeKey", v));
	public final BooleanOption autoShieldBreak = new BooleanOption("cheat.combat.auto_shield_break", false, v -> PrestigeBridge.set("autoShieldBreak", v));
	public final BooleanOption anchorMacro = new BooleanOption("cheat.combat.anchor_macro", false, v -> PrestigeBridge.set("anchorMacro", v));
	public final BooleanOption autoCrystal = new BooleanOption("cheat.combat.auto_crystal", false, v -> PrestigeBridge.set("autoCrystal", v));
	public final BooleanOption autoDoubleHand = new BooleanOption("cheat.combat.auto_double_hand", false, v -> PrestigeBridge.set("autoDoubleHand", v));
	public final BooleanOption autoHitCrystal = new BooleanOption("cheat.combat.auto_hit_crystal", false, v -> PrestigeBridge.set("autoHitCrystal", v));
	public final BooleanOption autoInventoryTotem = new BooleanOption("cheat.combat.auto_inventory_totem", false, v -> PrestigeBridge.set("autoInventoryTotem", v));
	public final BooleanOption triggerBot = new BooleanOption("cheat.combat.trigger_bot", false, v -> PrestigeBridge.set("triggerBot", v));
	public final BooleanOption autoPot = new BooleanOption("cheat.combat.auto_pot", false, v -> PrestigeBridge.set("autoPot", v));
	public final BooleanOption autoPotRefill = new BooleanOption("cheat.combat.auto_pot_refill", false, v -> PrestigeBridge.set("autoPotRefill", v));
	public final BooleanOption autoWTap = new BooleanOption("cheat.combat.auto_wtap", false, v -> PrestigeBridge.set("autoWTap", v));
	public final BooleanOption crystalOptimizer = new BooleanOption("cheat.combat.crystal_optimizer", false, v -> PrestigeBridge.set("crystalOptimizer", v));
	public final BooleanOption doubleAnchor = new BooleanOption("cheat.combat.double_anchor", false, v -> PrestigeBridge.set("doubleAnchor", v));
	public final BooleanOption hoverTotem = new BooleanOption("cheat.combat.hover_totem", false, v -> PrestigeBridge.set("hoverTotem", v));
	public final BooleanOption noMissDelay = new BooleanOption("cheat.combat.no_miss_delay", false, v -> PrestigeBridge.set("noMissDelay", v));
	public final BooleanOption shieldDisabler = new BooleanOption("cheat.combat.shield_disabler", false, v -> PrestigeBridge.set("shieldDisabler", v));
	public final BooleanOption totemOffhand = new BooleanOption("cheat.combat.totem_offhand", false, v -> PrestigeBridge.set("totemOffhand", v));
	public final BooleanOption autoJumpReset = new BooleanOption("cheat.combat.auto_jump_reset", false, v -> PrestigeBridge.set("autoJumpReset", v));
	public final BooleanOption safeAnchor = new BooleanOption("cheat.combat.safe_anchor", false, v -> PrestigeBridge.set("safeAnchor", v));

	// ==========================================
	// Cart Modules (11) - Ported from Cartcore
	// ==========================================
	public final BooleanOption instaCarting = new BooleanOption("cheat.cart.insta_carting", true, v -> syncAllMods());
	public final BooleanOption cartOverloading = new BooleanOption("cheat.cart.cart_overloading", true, v -> syncAllMods());
	public final BooleanOption crossbowCarting = new BooleanOption("cheat.cart.crossbow_carting", true, v -> syncAllMods());
	public final BooleanOption safeXbow = new BooleanOption("cheat.cart.safe_xbow", true, v -> syncAllMods());
	public final BooleanOption autoWebbing = new BooleanOption("cheat.cart.auto_webbing", true, v -> syncAllMods());
	public final BooleanOption cartReplenishing = new BooleanOption("cheat.cart.cart_replenishing", false, v -> syncAllMods());
	public final DoubleOption cartReplenishingSpeed = new DoubleOption("cheat.cart.cart_replenishing_speed", 2.0d, v -> syncAllMods(), 0.5d, 10.0d);
	public final IntegerOption replenishSlotsAbove = new IntegerOption("cheat.cart.replenish_slots_above", 7, v -> syncAllMods(), 1, 27);
	public final BooleanOption cartTickVariance = new BooleanOption("cheat.cart.tick_variance", true, v -> syncAllMods());
	public final BooleanOption cartSoundEffects = new BooleanOption("cheat.cart.sound_effects", true, v -> syncAllMods());
	public final BooleanOption cartChatLogs = new BooleanOption("cheat.cart.chat_logs", true, v -> syncAllMods());

	// ==========================================
	// Spear Modules (6) - Ported from SpearCore
	// ==========================================
	public final BooleanOption spearDashing = new BooleanOption("cheat.spear.dashing", true, v -> syncAllMods());
	public final BooleanOption spearOmnidirectionalDashing = new BooleanOption("cheat.spear.omnidirectional_dashing", false, v -> syncAllMods());
	public final BooleanOption spearRangeExtensions = new BooleanOption("cheat.spear.range_extensions", true, v -> syncAllMods());
	public final BooleanOption spearSoundEffects = new BooleanOption("cheat.spear.sound_effects", true, v -> syncAllMods());
	public final BooleanOption spearParticleEffects = new BooleanOption("cheat.spear.particle_effects", true, v -> syncAllMods());
	public final BooleanOption spearChatLogs = new BooleanOption("cheat.spear.chat_logs", true, v -> syncAllMods());

	// ==========================================
	// Mace Modules (24) - Ported from MaceCore
	// ==========================================
	public final BooleanOption maceAxeSwapping = new BooleanOption("cheat.mace.axe_swapping", true, v -> syncAllMods());
	public final BooleanOption macePearlCatching = new BooleanOption("cheat.mace.pearl_catching", true, v -> syncAllMods());
	public final BooleanOption maceSwordSwapping = new BooleanOption("cheat.mace.sword_swapping", true, v -> syncAllMods());
	public final BooleanOption maceShieldSlamming = new BooleanOption("cheat.mace.shield_slamming", true, v -> syncAllMods());
	public final BooleanOption maceAutoAxeShift = new BooleanOption("cheat.mace.auto_axe_shift", false, v -> syncAllMods());
	public final BooleanOption maceAimAssist = new BooleanOption("cheat.mace.aim_assist", false, v -> syncAllMods());
	public final DoubleOption maceAimAssistLenience = new DoubleOption("cheat.mace.aim_assist_lenience", 30.0d, v -> syncAllMods(), 5.0d, 90.0d);
	public final FloatOption maceAimAssistSpeedMultiplier = new FloatOption("cheat.mace.aim_assist_speed", 1.0f, v -> syncAllMods(), 0.1f, 5.0f);
	public final DoubleOption maceAimAssistRangeInBlocks = new DoubleOption("cheat.mace.aim_assist_range", 5.0d, v -> syncAllMods(), 1.0d, 10.0d);
	public final BooleanOption maceFreeHitting = new BooleanOption("cheat.mace.free_hitting", true, v -> syncAllMods());
	public final BooleanOption maceSoundEffects = new BooleanOption("cheat.mace.sound_effects", true, v -> syncAllMods());
	public final BooleanOption maceLegacyGlidingFov = new BooleanOption("cheat.mace.legacy_gliding_fov", true, v -> syncAllMods());
	public final BooleanOption maceShieldDraining = new BooleanOption("cheat.mace.shield_draining", false, v -> syncAllMods());
	public final IntegerOption maceShieldDrainingTickVariance = new IntegerOption("cheat.mace.shield_draining_tick_variance", 2, v -> syncAllMods(), 0, 20);
	public final BooleanOption maceParticleEffects = new BooleanOption("cheat.mace.particle_effects", true, v -> syncAllMods());
	public final BooleanOption maceElytraPotting = new BooleanOption("cheat.mace.elytra_potting", false, v -> syncAllMods());
	public final BooleanOption maceSmartR = new BooleanOption("cheat.mace.smart_r", false, v -> syncAllMods());
	public final BooleanOption maceElySwap = new BooleanOption("cheat.mace.ely_swap", false, v -> syncAllMods());
	public final BooleanOption maceChatLogs = new BooleanOption("cheat.mace.chat_logs", false, v -> syncAllMods());
	public final DoubleOption maceTickVariance = new DoubleOption("cheat.mace.tick_variance", 5.0d, v -> syncAllMods(), 0.0d, 20.0d);
	public final BooleanOption maceElytraBounce = new BooleanOption("cheat.mace.elytra_bounce", false, v -> syncAllMods());
	public final BooleanOption maceAimAssistFallOnly = new BooleanOption("cheat.mace.aim_assist_fall_only", false, v -> syncAllMods());
	public final BooleanOption maceSwordSwapDensityOnly = new BooleanOption("cheat.mace.sword_swap_density_only", false, v -> syncAllMods());
	public final BooleanOption maceMobAutoTarget = new BooleanOption("cheat.mace.mob_auto_target", false, v -> syncAllMods());

	// ==========================================
	// Misc Modules (12) - Ported from Prestige
	// ==========================================
	public final BooleanOption prevent = new BooleanOption("cheat.misc.prevent", false, v -> PrestigeBridge.set("prevent", v));
	public final BooleanOption autoXP = new BooleanOption("cheat.misc.auto_xp", false, v -> PrestigeBridge.set("autoXP", v));
	public final BooleanOption elytraSwap = new BooleanOption("cheat.misc.elytra_swap", false, v -> PrestigeBridge.set("elytraSwap", v));
	public final BooleanOption noJumpDelay = new BooleanOption("cheat.misc.no_jump_delay", false, v -> PrestigeBridge.set("noJumpDelay", v));
	public final BooleanOption pingSpoof = new BooleanOption("cheat.misc.ping_spoof", false, v -> PrestigeBridge.set("pingSpoof", v));
	public final BooleanOption fakeLag = new BooleanOption("cheat.misc.fake_lag", false, v -> PrestigeBridge.set("fakeLag", v));
	public final BooleanOption autoClicker = new BooleanOption("cheat.misc.auto_clicker", false, v -> PrestigeBridge.set("autoClicker", v));
	public final BooleanOption keyPearl = new BooleanOption("cheat.misc.key_pearl", false, v -> PrestigeBridge.set("keyPearl", v));
	public final BooleanOption noBreakDelay = new BooleanOption("cheat.misc.no_break_delay", false, v -> PrestigeBridge.set("noBreakDelay", v));
	public final BooleanOption freecam = new BooleanOption("cheat.misc.freecam", false, v -> PrestigeBridge.set("freecam", v));
	public final BooleanOption packSpoof = new BooleanOption("cheat.misc.pack_spoof", false, v -> PrestigeBridge.set("packSpoof", v));
	public final BooleanOption sprint = new BooleanOption("cheat.misc.sprint", false, v -> PrestigeBridge.set("sprint", v));

	// ==========================================
	// Render Modules (8) - Ported from Prestige
	// ==========================================
	public final BooleanOption hud = new BooleanOption("cheat.render.hud", false, v -> PrestigeBridge.set("hud", v));
	public final BooleanOption hitAnimations = new BooleanOption("cheat.render.hit_animations", false, v -> PrestigeBridge.set("hitAnimations", v));
	public final BooleanOption obsidianGlow = new BooleanOption("cheat.render.obsidian_glow", false, v -> PrestigeBridge.set("obsidianGlow", v));
	public final BooleanOption targetEffect = new BooleanOption("cheat.render.target_effect", false, v -> PrestigeBridge.set("targetEffect", v));
	public final BooleanOption noBounce = new BooleanOption("cheat.render.no_bounce", false, v -> PrestigeBridge.set("noBounce", v));
	public final BooleanOption playerESP = new BooleanOption("cheat.render.player_esp", false, v -> PrestigeBridge.set("playerESP", v));
	public final BooleanOption storageEsp = new BooleanOption("cheat.render.storage_esp", false, v -> PrestigeBridge.set("storageEsp", v));
	public final BooleanOption targetHud = new BooleanOption("cheat.render.target_hud", false, v -> PrestigeBridge.set("targetHud", v));

	// ==========================================
	// Client Modules (3) - Ported from Prestige
	// ==========================================
	public final BooleanOption prestige = new BooleanOption("cheat.client.prestige", false, v -> PrestigeBridge.set("prestige", v));
	public final BooleanOption friends = new BooleanOption("cheat.client.friends", false, v -> PrestigeBridge.set("friends", v));
	public final BooleanOption selfDestruct = new BooleanOption("cheat.client.self_destruct", false, v -> PrestigeBridge.set("selfDestruct", v));

	public static CheatMods getInstance() {
		return INSTANCE;
	}

	@Override
	public void init() {
		// Log startup status of backing mods
		logModStatus();

		// Register Right Ctrl keybinding to open Cheat menu directly
		openCheatKey = KeyBinds.getInstance().registerWithSimpleAction(
			new KeyMapping("key.catclient.open_cheat", InputConstants.KEY_RCONTROL, KeyBinds.CATEGORY_AXOLOTLCLIENT),
			this::openCheatMenu
		);

		// Combat Sub-section
		combat.add(aimAssist);
		combat.add(autoMace);
		combat.add(maceSwap);
		combat.add(autoWindCharge);
		combat.add(windChargeKey);
		combat.add(autoShieldBreak);
		combat.add(anchorMacro);
		combat.add(autoCrystal);
		combat.add(autoDoubleHand);
		combat.add(autoHitCrystal);
		combat.add(autoInventoryTotem);
		combat.add(triggerBot);
		combat.add(autoPot);
		combat.add(autoPotRefill);
		combat.add(autoWTap);
		combat.add(crystalOptimizer);
		combat.add(doubleAnchor);
		combat.add(hoverTotem);
		combat.add(noMissDelay);
		combat.add(shieldDisabler);
		combat.add(totemOffhand);
		combat.add(autoJumpReset);
		combat.add(safeAnchor);

		// Cart Sub-section
		cartSection.add(instaCarting);
		cartSection.add(cartOverloading);
		cartSection.add(crossbowCarting);
		cartSection.add(safeXbow);
		cartSection.add(autoWebbing);
		cartSection.add(cartReplenishing);
		cartSection.add(cartReplenishingSpeed);
		cartSection.add(replenishSlotsAbove);
		cartSection.add(cartTickVariance);
		cartSection.add(cartSoundEffects);
		cartSection.add(cartChatLogs);

		// Spear Sub-section
		spearSection.add(spearDashing);
		spearSection.add(spearOmnidirectionalDashing);
		spearSection.add(spearRangeExtensions);
		spearSection.add(spearSoundEffects);
		spearSection.add(spearParticleEffects);
		spearSection.add(spearChatLogs);

		// Mace Sub-section
		maceSection.add(maceAxeSwapping);
		maceSection.add(macePearlCatching);
		maceSection.add(maceSwordSwapping);
		maceSection.add(maceShieldSlamming);
		maceSection.add(maceAutoAxeShift);
		maceSection.add(maceAimAssist);
		maceSection.add(maceAimAssistLenience);
		maceSection.add(maceAimAssistSpeedMultiplier);
		maceSection.add(maceAimAssistRangeInBlocks);
		maceSection.add(maceFreeHitting);
		maceSection.add(maceSoundEffects);
		maceSection.add(maceLegacyGlidingFov);
		maceSection.add(maceShieldDraining);
		maceSection.add(maceShieldDrainingTickVariance);
		maceSection.add(maceParticleEffects);
		maceSection.add(maceElytraPotting);
		maceSection.add(maceSmartR);
		maceSection.add(maceElySwap);
		maceSection.add(maceChatLogs);
		maceSection.add(maceTickVariance);
		maceSection.add(maceElytraBounce);
		maceSection.add(maceAimAssistFallOnly);
		maceSection.add(maceSwordSwapDensityOnly);
		maceSection.add(maceMobAutoTarget);

		// Misc Sub-section
		misc.add(prevent);
		misc.add(autoXP);
		misc.add(elytraSwap);
		misc.add(noJumpDelay);
		misc.add(pingSpoof);
		misc.add(fakeLag);
		misc.add(autoClicker);
		misc.add(keyPearl);
		misc.add(noBreakDelay);
		misc.add(freecam);
		misc.add(packSpoof);
		misc.add(sprint);

		// Render Sub-section
		render.add(hud);
		render.add(hitAnimations);
		render.add(obsidianGlow);
		render.add(targetEffect);
		render.add(noBounce);
		render.add(playerESP);
		render.add(storageEsp);
		render.add(targetHud);

		// Client Sub-section
		clientSection.add(prestige);
		clientSection.add(friends);
		clientSection.add(selfDestruct);

		// Group sub-sections under root Cheat category
		category.add(combat);
		category.add(cartSection);
		category.add(spearSection);
		category.add(maceSection);
		category.add(misc);
		category.add(render);
		category.add(clientSection);

		// Register to main CatClient settings menu
		AxolotlClientConfigCommon.instance().addCategory(category);

		// Initial sync with Cartcore, SpearCore, and MaceCore
		syncAllMods();

		// Asynchronously poll for Prestige initialisation to sync defaults
		initPrestigePolling();
	}

	private void logModStatus() {
		boolean cartcore = isClassAvailable("thetruetrident.cartcore.CartcoreCore");
		boolean spearcore = isClassAvailable("thetruetrident.spearcore.SpearcoreClient");
		boolean macecore = isClassAvailable("thetruetrident.macecore.MacecoreClient");
		boolean prestige = isClassAvailable("juxxl.prestige.Prestige");

		System.out.println("[CatClient] cartcore loaded: " + cartcore);
		System.out.println("[CatClient] spearcore loaded: " + spearcore);
		System.out.println("[CatClient] macecore loaded: " + macecore);
		System.out.println("[CatClient] prestige loaded: " + prestige);
	}

	private boolean isClassAvailable(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	private void initPrestigePolling() {
		Thread t = new Thread(() -> {
			for (int i = 0; i < 20; i++) {
				try {
					Thread.sleep(500);
					Class<?> p = Class.forName("juxxl.prestige.Prestige");
					Object inst = p.getField("INSTANCE").get(null);
					if (inst != null) {
						syncAllPrestigeOptions();
						break;
					}
				} catch (Throwable ignored) {
				}
			}
		}, "CatClient-Prestige-Sync");
		t.setDaemon(true);
		t.start();
	}

	public void openCheatMenu() {
		Minecraft mc = Minecraft.getInstance();
		mc.execute(() -> {
			Screen parent = (mc.screen instanceof HudEditScreen) ? null : mc.screen;
			Screen screen = ConfigStyles.createScreen(parent, category);
			mc.setScreen(screen);
		});
	}

	@Override
	public void tick() {
		if (client == null || client.player == null || client.level == null) {
			return;
		}

		if (!audited) {
			audited = true;
			syncAllMods();
			syncAllPrestigeOptions();
			runRuntimeAudit();
		}
	}

	public void syncAllMods() {
		syncWithCartcore();
		syncWithSpearcore();
		syncWithMacecore();
	}

	public void syncAllPrestigeOptions() {
		PrestigeBridge.set("aimAssist", this.aimAssist.get());
		PrestigeBridge.set("autoMace", this.autoMace.get());
		PrestigeBridge.set("maceSwap", this.maceSwap.get());
		PrestigeBridge.set("autoWindCharge", this.autoWindCharge.get());
		PrestigeBridge.set("windChargeKey", this.windChargeKey.get());
		PrestigeBridge.set("autoShieldBreak", this.autoShieldBreak.get());
		PrestigeBridge.set("anchorMacro", this.anchorMacro.get());
		PrestigeBridge.set("autoCrystal", this.autoCrystal.get());
		PrestigeBridge.set("autoDoubleHand", this.autoDoubleHand.get());
		PrestigeBridge.set("autoHitCrystal", this.autoHitCrystal.get());
		PrestigeBridge.set("autoInventoryTotem", this.autoInventoryTotem.get());
		PrestigeBridge.set("triggerBot", this.triggerBot.get());
		PrestigeBridge.set("autoPot", this.autoPot.get());
		PrestigeBridge.set("autoPotRefill", this.autoPotRefill.get());
		PrestigeBridge.set("autoWTap", this.autoWTap.get());
		PrestigeBridge.set("crystalOptimizer", this.crystalOptimizer.get());
		PrestigeBridge.set("doubleAnchor", this.doubleAnchor.get());
		PrestigeBridge.set("hoverTotem", this.hoverTotem.get());
		PrestigeBridge.set("noMissDelay", this.noMissDelay.get());
		PrestigeBridge.set("shieldDisabler", this.shieldDisabler.get());
		PrestigeBridge.set("totemOffhand", this.totemOffhand.get());
		PrestigeBridge.set("autoJumpReset", this.autoJumpReset.get());
		PrestigeBridge.set("safeAnchor", this.safeAnchor.get());

		PrestigeBridge.set("prevent", this.prevent.get());
		PrestigeBridge.set("autoXP", this.autoXP.get());
		PrestigeBridge.set("elytraSwap", this.elytraSwap.get());
		PrestigeBridge.set("noJumpDelay", this.noJumpDelay.get());
		PrestigeBridge.set("pingSpoof", this.pingSpoof.get());
		PrestigeBridge.set("fakeLag", this.fakeLag.get());
		PrestigeBridge.set("autoClicker", this.autoClicker.get());
		PrestigeBridge.set("keyPearl", this.keyPearl.get());
		PrestigeBridge.set("noBreakDelay", this.noBreakDelay.get());
		PrestigeBridge.set("freecam", this.freecam.get());
		PrestigeBridge.set("packSpoof", this.packSpoof.get());
		PrestigeBridge.set("sprint", this.sprint.get());

		PrestigeBridge.set("hud", this.hud.get());
		PrestigeBridge.set("hitAnimations", this.hitAnimations.get());
		PrestigeBridge.set("obsidianGlow", this.obsidianGlow.get());
		PrestigeBridge.set("targetEffect", this.targetEffect.get());
		PrestigeBridge.set("noBounce", this.noBounce.get());
		PrestigeBridge.set("playerESP", this.playerESP.get());
		PrestigeBridge.set("storageEsp", this.storageEsp.get());
		PrestigeBridge.set("targetHud", this.targetHud.get());

		PrestigeBridge.set("prestige", this.prestige.get());
		PrestigeBridge.set("friends", this.friends.get());
		PrestigeBridge.set("selfDestruct", this.selfDestruct.get());
	}

	public void syncWithCartcore() {
		try {
			Class<?> coreClass = Class.forName("thetruetrident.cartcore.CartcoreCore");
			Field configField = coreClass.getField("cartConfig");
			Object configObj = configField.get(null);
			if (configObj == null) {
				Class<?> configClass = Class.forName("thetruetrident.cartcore.cartConfig");
				configObj = allocateOrInstantiate(configClass);
				if (configObj != null) {
					configField.set(null, configObj);
				}
			}
			if (configObj != null) {
				setFieldSafe(configObj, "instaCarting", this.instaCarting.get());
				setFieldSafe(configObj, "cartOverloading", this.cartOverloading.get());
				setFieldSafe(configObj, "crossbowCarting", this.crossbowCarting.get());
				setFieldSafe(configObj, "safeXbow", this.safeXbow.get());
				setFieldSafe(configObj, "autoWebbing", this.autoWebbing.get());
				setFieldSafe(configObj, "cartReplenishing", this.cartReplenishing.get());
				setFieldSafe(configObj, "cartReplenishingSpeed", this.cartReplenishingSpeed.get());
				setFieldSafe(configObj, "replenishSlotsAbove", this.replenishSlotsAbove.get());
				setFieldSafe(configObj, "tickVariance", this.cartTickVariance.get());
				setFieldSafe(configObj, "soundEffects", this.cartSoundEffects.get());
				setFieldSafe(configObj, "chatLogs", this.cartChatLogs.get());
			}
		} catch (Throwable ignored) {
		}
	}

	public void syncWithSpearcore() {
		try {
			Class<?> clientClass = Class.forName("thetruetrident.spearcore.SpearcoreClient");
			Field configField = clientClass.getField("spearConfig");
			Object configObj = configField.get(null);
			if (configObj == null) {
				Class<?> configClass = Class.forName("thetruetrident.spearcore.spearcoreConfig");
				configObj = allocateOrInstantiate(configClass);
				if (configObj != null) {
					configField.set(null, configObj);
				}
			}
			if (configObj != null) {
				setFieldSafe(configObj, "dashing", this.spearDashing.get());
				setFieldSafe(configObj, "omnidirectionalDashing", this.spearOmnidirectionalDashing.get());
				setFieldSafe(configObj, "rangeExtensions", this.spearRangeExtensions.get());
				setFieldSafe(configObj, "soundEffects", this.spearSoundEffects.get());
				setFieldSafe(configObj, "particleEffects", this.spearParticleEffects.get());
				setFieldSafe(configObj, "chatLogs", this.spearChatLogs.get());
			}
		} catch (Throwable ignored) {
		}
	}

	public void syncWithMacecore() {
		try {
			Class<?> clientClass = Class.forName("thetruetrident.macecore.MacecoreClient");
			Field configField = clientClass.getField("clientConfig");
			Object configObj = configField.get(null);
			if (configObj == null) {
				Class<?> configClass = Class.forName("thetruetrident.macecore.macecoreConfig");
				configObj = allocateOrInstantiate(configClass);
				if (configObj != null) {
					configField.set(null, configObj);
				}
			}
			if (configObj != null) {
				setFieldSafe(configObj, "axeSwapping", this.maceAxeSwapping.get());
				setFieldSafe(configObj, "pearlCatching", this.macePearlCatching.get());
				setFieldSafe(configObj, "swordSwapping", this.maceSwordSwapping.get());
				setFieldSafe(configObj, "shieldSlamming", this.maceShieldSlamming.get());
				setFieldSafe(configObj, "autoAxeShift", this.maceAutoAxeShift.get());
				setFieldSafe(configObj, "aimAssist", this.maceAimAssist.get());
				setFieldSafe(configObj, "aimAssistLenience", this.maceAimAssistLenience.get());
				setFieldSafe(configObj, "aimAssistSpeedMultiplier", this.maceAimAssistSpeedMultiplier.get());
				setFieldSafe(configObj, "aimAssistRangeInBlocks", this.maceAimAssistRangeInBlocks.get());
				setFieldSafe(configObj, "freeHitting", this.maceFreeHitting.get());
				setFieldSafe(configObj, "soundEffects", this.maceSoundEffects.get());
				setFieldSafe(configObj, "legacyGlidingFov", this.maceLegacyGlidingFov.get());
				setFieldSafe(configObj, "shieldDraining", this.maceShieldDraining.get());
				setFieldSafe(configObj, "shieldDrainingTickVariance", this.maceShieldDrainingTickVariance.get());
				setFieldSafe(configObj, "particleEffects", this.maceParticleEffects.get());
				setFieldSafe(configObj, "elytraPotting", this.maceElytraPotting.get());
				setFieldSafe(configObj, "smartR", this.maceSmartR.get());
				setFieldSafe(configObj, "elySwap", this.maceElySwap.get());
				setFieldSafe(configObj, "chatLogs", this.maceChatLogs.get());
				setFieldSafe(configObj, "tickVariance", this.maceTickVariance.get());

				// Sync Extras section if present
				try {
					Field extrasField = configObj.getClass().getField("Extras");
					Object extrasObj = extrasField.get(configObj);
					if (extrasObj == null) {
						Class<?> extrasClass = Class.forName("thetruetrident.macecore.macecoreConfig$MySection");
						extrasObj = allocateOrInstantiate(extrasClass);
						if (extrasObj != null) {
							extrasField.set(configObj, extrasObj);
						}
					}
					if (extrasObj != null) {
						setFieldSafe(extrasObj, "elytraBounce", this.maceElytraBounce.get());
						setFieldSafe(extrasObj, "aimAssistFallOnly", this.maceAimAssistFallOnly.get());
						setFieldSafe(extrasObj, "swordSwapDensityOnly", this.maceSwordSwapDensityOnly.get());
						setFieldSafe(extrasObj, "mobAutoTarget", this.maceMobAutoTarget.get());
					}
				} catch (Throwable ignored) {
				}
			}
		} catch (Throwable ignored) {
		}
	}

	private static Object allocateOrInstantiate(Class<?> clazz) {
		try {
			var constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Throwable t) {
			try {
				Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
				f.setAccessible(true);
				sun.misc.Unsafe unsafe = (sun.misc.Unsafe) f.get(null);
				return unsafe.allocateInstance(clazz);
			} catch (Throwable t2) {
				return null;
			}
		}
	}

	public static void setFieldSafe(Object target, String fieldName, Object value) {
		if (target == null) return;
		try {
			Field field = target.getClass().getField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Throwable ignored) {
			try {
				Field field = target.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(target, value);
			} catch (Throwable ignored2) {
			}
		}
	}

	private void runRuntimeAudit() {
		System.out.println("================== [CatClient Runtime Audit] ==================");
		boolean cartcore = isClassAvailable("thetruetrident.cartcore.CartcoreCore");
		boolean spearcore = isClassAvailable("thetruetrident.spearcore.SpearcoreClient");
		boolean macecore = isClassAvailable("thetruetrident.macecore.MacecoreClient");
		boolean prestige = isClassAvailable("juxxl.prestige.Prestige");

		int working = 0;
		int total = 87;

		// Audit 23 Combat + 12 Misc + 8 Render + 3 Client = 46 Prestige options
		String statusPrestige = prestige ? "WORKING" : "DUMMY";
		if (prestige) working += 46;

		// Audit 11 Cart options
		String statusCart = cartcore ? "WORKING" : "DUMMY";
		if (cartcore) working += 11;

		// Audit 6 Spear options
		String statusSpear = spearcore ? "WORKING" : "DUMMY";
		if (spearcore) working += 6;

		// Audit 24 Mace options
		String statusMace = macecore ? "WORKING" : "DUMMY";
		if (macecore) working += 24;

		System.out.println("Cart Options (11): " + statusCart);
		System.out.println("Spear Options (6): " + statusSpear);
		System.out.println("Mace Options (24): " + statusMace);
		System.out.println("Prestige Options (46): " + statusPrestige);
		System.out.println(total + " options, " + working + " working, " + (total - working) + " dummy/broken");
		System.out.println("===============================================================");
	}

	public OptionCategory getCategory() {
		return category;
	}
}
