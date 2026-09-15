package me.itz0cat.catclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.RenderTickEvent;
import me.itz0cat.catclient.gui.ImguiLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.obfuscate.DontObfuscate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(at = @At("HEAD"), method = "flipFrame", remap = false)
    private static void runTickTail(CallbackInfo ci) {
        CatClient.EVENTBUS.post(RenderTickEvent.get());
        net.minecraft.util.profiler.Profilers.get().push("ImGui Render");
        ImguiLoader.onFrameRender();
        net.minecraft.util.profiler.Profilers.get().pop();
    }
}
