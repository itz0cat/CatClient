package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.mod.mods.HitboxMod;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugHudProfile.class)
public abstract class DebugHudProfileMixin {
    @Inject(method = "isEntryVisible", at = @At("HEAD"), cancellable = true)
    private void catclient$showHitboxes(Identifier entry, CallbackInfoReturnable<Boolean> cir) {
        if (DebugHudEntries.ENTITY_HITBOXES.equals(entry) && CatClient.modManager() != null && CatClient.modManager().isModEnabled(HitboxMod.class)) {
            cir.setReturnValue(true);
        }
    }
}
