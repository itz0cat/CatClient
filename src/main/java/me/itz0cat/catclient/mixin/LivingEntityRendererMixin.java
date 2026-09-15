package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.mod.GeneralSettings;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.itz0cat.catclient.CatClient.mc;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private void hasLabel(LivingEntity livingEntity, double d, CallbackInfoReturnable<Boolean> cir) {
        if (CatClient.modManager().getMod(GeneralSettings.class).showOwnNametag.isEnabled() && mc.player != null && livingEntity.getId() == mc.player.getId()) {
            cir.setReturnValue(true);
        }
    }
}
