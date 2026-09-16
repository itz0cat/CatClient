package me.itz0cat.catclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public abstract class SplashScreenMixin extends Overlay {
    @Shadow
    private long reloadCompleteTime;
    @Shadow @Final @Mutable
    private static int MOJANG_RED;
    @Shadow @Final @Mutable
    private static int MONOCHROME_BLACK;

    @Shadow @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext drawContext, int i, int j, float f, CallbackInfo ci) {
        MOJANG_RED = ColorHelper.getArgb(255, 30, 30, 46);
        MONOCHROME_BLACK = ColorHelper.getArgb(255, 30, 30, 46);
        if (this.reloadCompleteTime > 1) {
            this.client.setOverlay(null);
        }
    }
}