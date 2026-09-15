package me.itz0cat.catclient.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.itz0cat.catclient.CatClient.mc;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext drawContext, int i, int j, float f, CallbackInfo ci) {
        drawContext.drawTexture(Identifier.of("catclient", "cattext.png"), 0, mc.getWindow().getScaledHeight() - 32, 0, 0, 167, 28, 167, 28);
    }
}
