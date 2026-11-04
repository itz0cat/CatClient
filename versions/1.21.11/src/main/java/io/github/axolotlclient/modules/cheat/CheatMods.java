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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Cheat Modules section ported from Prestige utility client plus specialized Cart combat modules.
 * Provides configurable module options organized under Combat, Cart, Misc, Render, and Client sub-sections.
 */
public class CheatMods extends AbstractModule {

	private static final CheatMods INSTANCE = new CheatMods();

	// Root section for Cheat
	private final OptionCategory category = OptionCategory.create("cheat");

	// Sub-sections
	private final OptionCategory combat = OptionCategory.create("cheat.combat");
	private final OptionCategory cartSection = OptionCategory.create("cheat.cart");
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
	// Cart Modules (3)
	// ==========================================
	public final BooleanOption autoCart = new BooleanOption("cheat.cart.auto_cart", false);
	public final BooleanOption safeCart = new BooleanOption("cheat.cart.safe_cart", false);
	public final BooleanOption cartAura = new BooleanOption("cheat.cart.cart_aura", false);

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

		// Cart Sub-section
		cartSection.add(autoCart);
		cartSection.add(safeCart);
		cartSection.add(cartAura);

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
		category.add(misc);
		category.add(render);
		category.add(clientSection);

		// Register to main CatClient settings menu
		AxolotlClientConfigCommon.instance().addCategory(category);
	}

	@Override
	public void tick() {
		if (client == null || client.player == null || client.level == null || client.gameMode == null) {
			return;
		}

		// Cart Aura: spam-click break minecarts placed near the player
		if (cartAura.get()) {
			handleCartAura();
		}

		// Safe Cart: place protective block from cart explosion when arrow is detected
		if (safeCart.get()) {
			handleSafeCart();
		}

		// Auto Cart: automatic rail & cart placement when aiming with bow
		if (autoCart.get()) {
			handleAutoCart();
		}
	}

	private void handleCartAura() {
		if (client == null || client.player == null || client.level == null || client.gameMode == null) return;
		for (Entity entity : client.level.entitiesForRendering()) {
			if (entity == null || entity == client.player) continue;
			String typeId = entity.getType().getDescriptionId();
			if (typeId.contains("minecart")) {
				double distSq = client.player.distanceToSqr(entity);
				if (distSq <= 16.0) { // within 4 blocks
					client.gameMode.attack(client.player, entity);
					client.player.swing(InteractionHand.MAIN_HAND);
					break;
				}
			}
		}
	}

	private void handleSafeCart() {
		if (client == null || client.player == null || client.level == null || client.gameMode == null) return;
		boolean minecartNearby = false;
		for (Entity entity : client.level.entitiesForRendering()) {
			if (entity != null && entity.getType().getDescriptionId().contains("minecart") && client.player.distanceToSqr(entity) <= 36.0) {
				minecartNearby = true;
				break;
			}
		}
		if (!minecartNearby) return;

		for (Entity entity : client.level.entitiesForRendering()) {
			if (entity != null && entity.getType().getDescriptionId().contains("arrow")) {
				if (client.player.distanceToSqr(entity) <= 25.0) {
					placeProtectiveBlock();
					break;
				}
			}
		}
	}

	private void placeProtectiveBlock() {
		if (client == null || client.player == null || client.gameMode == null || client.level == null) return;
		BlockPos playerPos = client.player.blockPosition();
		BlockPos shieldPos = playerPos.relative(client.player.getDirection());
		if (client.level.getBlockState(shieldPos).isAir()) {
			BlockHitResult hitResult = new BlockHitResult(
				Vec3.atCenterOf(shieldPos),
				Direction.UP,
				shieldPos.below(),
				false
			);
			client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, hitResult);
			client.player.swing(InteractionHand.MAIN_HAND);
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
