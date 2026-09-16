package me.itz0cat.catclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.RenderTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class RenderSystemMixin {
    /*
    @Inject(at = @At("HEAD"), method = "flipFrame", remap = false)
    private static void runTickTail(CallbackInfo ci) {
        CatClient.EVENTBUS.post(RenderTickEvent.get());
    }
    */
}
