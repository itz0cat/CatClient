package me.itz0cat.catclient.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Shadow
    public void drawText(TextRenderer textRenderer, @Nullable String string, int i, int j, int k, boolean bl) {}

    @Inject(at = @At("HEAD"), method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", cancellable = true)
    public void drawTextWithShadow(TextRenderer textRenderer, String string, int i, int j, int k, CallbackInfo ci) {
        if (string != null && string.startsWith("Minecraft 1.21")) {
            drawText(textRenderer, "", i, j, k, true);
            ci.cancel();
        }
    }
}
