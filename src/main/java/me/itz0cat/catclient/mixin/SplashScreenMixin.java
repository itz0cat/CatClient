package me.itz0cat.catclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.itz0cat.catclient.menu.MainMenuButtons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.util.Optional;
import java.util.OptionalInt;

import static net.minecraft.util.math.ColorHelper.Abgr.withAlpha;

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
        //LOGO =  Identifier.of("catclient","icon.png");
        Identifier BG = Identifier.of("catclient", "waves.png");
        MOJANG_RED = ColorHelper.Argb.getArgb(255, 30, 30, 46);
        MONOCHROME_BLACK = ColorHelper.Argb.getArgb(255, 30, 30, 46);
        if (this.reloadCompleteTime > 1) {
            this.client.setOverlay(null);
            MainMenuButtons.reloadComplete = true;
        }
    }
}