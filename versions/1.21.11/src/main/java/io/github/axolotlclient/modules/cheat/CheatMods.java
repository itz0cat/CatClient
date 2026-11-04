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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Cheat Modules section ported from Prestige utility client plus Cartcore, SpearCore, and MaceCore integrations.
 * Provides configurable module options organized under Combat, Cart, Spear, Mace, Misc, Render, and Client sub-sections.
 * Automatically bridges configurations directly into Cartcore, SpearCore, and MaceCore instances natively.
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

	// ==========================================
	// Combat Modules (23) - Ported from Prestige
	// ==========================================
	public final BooleanOption aimAssist = new BooleanOption("cheat.combat.aim_assist", false);
	public final BooleanOption autoMace = new BooleanOption("cheat.combat.auto_mace", false);
	public final BooleanOption maceSwap = new BooleanOption("cheat.combat.mace_swap", false);
	public final BooleanOption autoWindCharge = new BooleanOption("cheat.combat.auto_wind_charge", false);
	public final BooleanOption windChargeKey = new BooleanOption("cheat.combat.wind_charge_key", false);
	public final BooleanOption autoShieldBreak = new BooleanOption("cheat.combat.auto_shield_break", false);
	public final BooleanOption anchorMacro = new BooleanOption("cheat.combat.anchor_macro", false);
	public final BooleanOption autoCrystal = new BooleanOption("cheat.combat.auto_crystal", false);
	public final BooleanOption autoDoubleHand = new BooleanOption("cheat.combat.auto_double_hand", false);
	public final BooleanOption autoHitCrystal = new BooleanOption("cheat.combat.auto_hit_crystal", false);
	public final BooleanOption autoInventoryTotem = new BooleanOption("cheat.combat.auto_inventory_totem", false);
	public final BooleanOption triggerBot = new BooleanOption("cheat.combat.trigger_bot", false);
	public final BooleanOption autoPot = new BooleanOption("cheat.combat.auto_pot", false);
	public final BooleanOption autoPotRefill = new BooleanOption("cheat.combat.auto_pot_refill", false);
	public final BooleanOption autoWTap = new BooleanOption("cheat.combat.auto_wtap", false);
	public final BooleanOption crystalOptimizer = new BooleanOption("cheat.combat.crystal_optimizer", false);
	public final BooleanOption doubleAnchor = new BooleanOption("cheat.combat.double_anchor", false);
	public final BooleanOption hoverTotem = new BooleanOption("cheat.combat.hover_totem", false);
	public final BooleanOption noMissDelay = new BooleanOption("cheat.combat.no_miss_delay", false);
	public final BooleanOption shieldDisabler = new BooleanOption("cheat.combat.shield_disabler", false);
	public final BooleanOption totemOffhand = new BooleanOption("cheat.combat.totem_offhand", false);
	public final BooleanOption autoJumpReset = new BooleanOption("cheat.combat.auto_jump_reset", false);
	public final BooleanOption safeAnchor = new BooleanOption("cheat.combat.safe_anchor", false);

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
	public final BooleanOption prevent = new BooleanOption("cheat.misc.prevent", false);
	public final BooleanOption autoXP = new BooleanOption("cheat.misc.auto_xp", false);
	public final BooleanOption elytraSwap = new BooleanOption("cheat.misc.elytra_swap", false);
	public final BooleanOption noJumpDelay = new BooleanOption("cheat.misc.no_jump_delay", false);
	public final BooleanOption pingSpoof = new BooleanOption("cheat.misc.ping_spoof", false);
	public final BooleanOption fakeLag = new BooleanOption("cheat.misc.fake_lag", false);
	public final BooleanOption autoClicker = new BooleanOption("cheat.misc.auto_clicker", false);
	public final BooleanOption keyPearl = new BooleanOption("cheat.misc.key_pearl", false);
	public final BooleanOption noBreakDelay = new BooleanOption("cheat.misc.no_break_delay", false);
	public final BooleanOption freecam = new BooleanOption("cheat.misc.freecam", false);
	public final BooleanOption packSpoof = new BooleanOption("cheat.misc.pack_spoof", false);
	public final BooleanOption sprint = new BooleanOption("cheat.misc.sprint", false);

	// ==========================================
	// Render Modules (8) - Ported from Prestige
	// ==========================================
	public final BooleanOption hud = new BooleanOption("cheat.render.hud", false);
	public final BooleanOption hitAnimations = new BooleanOption("cheat.render.hit_animations", false);
	public final BooleanOption obsidianGlow = new BooleanOption("cheat.render.obsidian_glow", false);
	public final BooleanOption targetEffect = new BooleanOption("cheat.render.target_effect", false);
	public final BooleanOption noBounce = new BooleanOption("cheat.render.no_bounce", false);
	public final BooleanOption playerESP = new BooleanOption("cheat.render.player_esp", false);
	public final BooleanOption storageEsp = new BooleanOption("cheat.render.storage_esp", false);
	public final BooleanOption targetHud = new BooleanOption("cheat.render.target_hud", false);

	// ==========================================
	// Client Modules (3) - Ported from Prestige
	// ==========================================
	public final BooleanOption prestige = new BooleanOption("cheat.client.prestige", false);
	public final BooleanOption friends = new BooleanOption("cheat.client.friends", false);
	public final BooleanOption selfDestruct = new BooleanOption("cheat.client.self_destruct", false);

	public static CheatMods getInstance() {
		return INSTANCE;
	}

	@Override
	public void init() {
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
		if (client == null || client.player == null || client.level == null || client.gameMode == null) {
			return;
		}

		// Keep config continuously synchronized
		syncAllMods();

		// Native Auto Cart placement fallback if instaCarting is enabled
		if (instaCarting.get()) {
			handleAutoCart();
		}
	}

	public void syncAllMods() {
		syncWithCartcore();
		syncWithSpearcore();
		syncWithMacecore();
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
				setFieldSafe(configObj, "instaCarting", instaCarting.get());
				setFieldSafe(configObj, "cartOverloading", cartOverloading.get());
				setFieldSafe(configObj, "crossbowCarting", crossbowCarting.get());
				setFieldSafe(configObj, "safeXbow", safeXbow.get());
				setFieldSafe(configObj, "autoWebbing", autoWebbing.get());
				setFieldSafe(configObj, "cartReplenishing", cartReplenishing.get());
				setFieldSafe(configObj, "cartReplenishingSpeed", cartReplenishingSpeed.get());
				setFieldSafe(configObj, "replenishSlotsAbove", replenishSlotsAbove.get());
				setFieldSafe(configObj, "tickVariance", cartTickVariance.get());
				setFieldSafe(configObj, "soundEffects", cartSoundEffects.get());
				setFieldSafe(configObj, "chatLogs", cartChatLogs.get());
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
				setFieldSafe(configObj, "dashing", spearDashing.get());
				setFieldSafe(configObj, "omnidirectionalDashing", spearOmnidirectionalDashing.get());
				setFieldSafe(configObj, "rangeExtensions", spearRangeExtensions.get());
				setFieldSafe(configObj, "soundEffects", spearSoundEffects.get());
				setFieldSafe(configObj, "particleEffects", spearParticleEffects.get());
				setFieldSafe(configObj, "chatLogs", spearChatLogs.get());
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
				setFieldSafe(configObj, "axeSwapping", maceAxeSwapping.get());
				setFieldSafe(configObj, "pearlCatching", macePearlCatching.get());
				setFieldSafe(configObj, "swordSwapping", maceSwordSwapping.get());
				setFieldSafe(configObj, "shieldSlamming", maceShieldSlamming.get());
				setFieldSafe(configObj, "autoAxeShift", maceAutoAxeShift.get());
				setFieldSafe(configObj, "aimAssist", maceAimAssist.get());
				setFieldSafe(configObj, "aimAssistLenience", maceAimAssistLenience.get());
				setFieldSafe(configObj, "aimAssistSpeedMultiplier", maceAimAssistSpeedMultiplier.get());
				setFieldSafe(configObj, "aimAssistRangeInBlocks", maceAimAssistRangeInBlocks.get());
				setFieldSafe(configObj, "freeHitting", maceFreeHitting.get());
				setFieldSafe(configObj, "soundEffects", maceSoundEffects.get());
				setFieldSafe(configObj, "legacyGlidingFov", maceLegacyGlidingFov.get());
				setFieldSafe(configObj, "shieldDraining", maceShieldDraining.get());
				setFieldSafe(configObj, "shieldDrainingTickVariance", maceShieldDrainingTickVariance.get());
				setFieldSafe(configObj, "particleEffects", maceParticleEffects.get());
				setFieldSafe(configObj, "elytraPotting", maceElytraPotting.get());
				setFieldSafe(configObj, "smartR", maceSmartR.get());
				setFieldSafe(configObj, "elySwap", maceElySwap.get());
				setFieldSafe(configObj, "chatLogs", maceChatLogs.get());
				setFieldSafe(configObj, "tickVariance", maceTickVariance.get());

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
						setFieldSafe(extrasObj, "elytraBounce", maceElytraBounce.get());
						setFieldSafe(extrasObj, "aimAssistFallOnly", maceAimAssistFallOnly.get());
						setFieldSafe(extrasObj, "swordSwapDensityOnly", maceSwordSwapDensityOnly.get());
						setFieldSafe(extrasObj, "mobAutoTarget", maceMobAutoTarget.get());
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

	private static void setFieldSafe(Object target, String fieldName, Object value) {
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

	private void handleAutoCart() {
		if (client == null || client.player == null || client.gameMode == null || client.level == null) return;
		if (client.player.isUsingItem() && client.player.getUseItem().getItem().getDescriptionId().contains("bow")) {
			HitResult hit = client.hitResult;
			if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
				double distSq = client.player.distanceToSqr(hit.getLocation());
				if (distSq <= 20.25) { // within ~4.5 blocks placeable reach
					client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, blockHit);
					client.player.swing(InteractionHand.MAIN_HAND);
				}
			}
		}
	}

	public OptionCategory getCategory() {
		return category;
	}
}
