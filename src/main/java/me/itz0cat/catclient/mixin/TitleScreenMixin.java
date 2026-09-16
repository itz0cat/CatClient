package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.ui.CatClientScreen;
import me.itz0cat.catclient.ui.CatConsentScreen;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.itz0cat.catclient.CatClient.mc;

@Mixin(value = TitleScreen.class, priority = 9999)
public abstract class TitleScreenMixin {
    @Shadow @Nullable @Mutable
    private SplashTextRenderer splashText;

    @Inject(method = "render", at = @At("HEAD"))
    protected void renderBackground(DrawContext drawContext, int i, int j, float f, CallbackInfo info) {
        splashText = null;
    }

    @Inject(method = "render", at = @At("TAIL"))
    protected void renderLogo(DrawContext drawContext, int i, int j, float f, CallbackInfo ci) {
        int size = 48;
        drawContext.drawTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, Identifier.of("catclient", "icon.png"), 12, 12, 0f, 0f, size, size, size, size);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void addCatClientButton(CallbackInfo info) {
        TitleScreen screen = (TitleScreen) (Object) this;
        Screens.getButtons(screen).add(
            ButtonWidget.builder(
                Text.literal("CatClient"),
                btn -> mc.setScreen(new CatClientScreen(screen))
            ).dimensions(12, 64, 70, 20).build()
        );

        if (!CatClient.configManager().privacyPrompted) {
            mc.execute(() -> {
                if (mc.currentScreen instanceof TitleScreen) {
                    mc.setScreen(new CatConsentScreen(screen));
                }
            });
        }
    }
}
