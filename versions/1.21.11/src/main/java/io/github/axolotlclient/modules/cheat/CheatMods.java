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

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.modules.AbstractModule;

/**
 * Cheat Modules section ported from Prestige utility client.
 * Provides configurable module options organized under Combat, Misc, Render, and Client sub-sections.
 */
public class CheatMods extends AbstractModule {

	private static final CheatMods INSTANCE = new CheatMods();

	// Root section for Cheat
	private final OptionCategory category = OptionCategory.create("cheat");

	// Sub-sections matching Prestige.jar categories
	private final OptionCategory combat = OptionCategory.create("cheat.combat");
	private final OptionCategory misc = OptionCategory.create("cheat.misc");
	private final OptionCategory render = OptionCategory.create("cheat.render");
	private final OptionCategory clientSection = OptionCategory.create("cheat.client");

	// ==========================================
	// Combat Modules (23)
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
	// Misc Modules (12)
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
	// Render Modules (8)
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
	// Client Modules (3)
	// ==========================================
	public final BooleanOption prestige = new BooleanOption("cheat.client.prestige", false);
	public final BooleanOption friends = new BooleanOption("cheat.client.friends", false);
	public final BooleanOption selfDestruct = new BooleanOption("cheat.client.self_destruct", false);

	public static CheatMods getInstance() {
		return INSTANCE;
	}

	@Override
	public void init() {
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
		category.add(misc);
		category.add(render);
		category.add(clientSection);

		// Register to main CatClient settings menu
		AxolotlClientConfigCommon.instance().addCategory(category);
	}

	public OptionCategory getCategory() {
		return category;
	}
}
