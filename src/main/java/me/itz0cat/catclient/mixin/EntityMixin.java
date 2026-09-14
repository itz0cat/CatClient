package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.mod.mods.FreelookMod;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    public void interceptMovement(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (CatClient.modManager().getMod(FreelookMod.class).consumeRotation(cursorDeltaX, cursorDeltaY)) {
            ci.cancel();
        }
    }
}
