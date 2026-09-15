package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.OverlayReloadListener;
import me.itz0cat.catclient.mod.mods.HitColorMod;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(OverlayTexture.class)
public abstract class OverlayTextureMixin implements OverlayReloadListener {
    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void modifyHitColor(CallbackInfo ci) {
        this.reloadOverlay();
        OverlayReloadListener.register(this);
    }

    public void onOverlayReload() {
        this.reloadOverlay();
    }

    private static int getColorInt(int red, int green, int blue, int alpha) {
        alpha = 255 - alpha;
        return (alpha << 24) + (blue << 16) + (green << 8) + red;
    }

    public void reloadOverlay() {
        if (CatClient.modManager() == null) return;
        NativeImage nativeImage = this.texture.getImage();
        if (nativeImage == null) return;

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i < 8) {
                    Color color = CatClient.modManager().getMod(HitColorMod.class).hitColor.getColor();
                    if (CatClient.modManager().getMod(HitColorMod.class).isEnabled())
                        nativeImage.setColorArgb(j, i, getColorInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                    else
                        nativeImage.setColorArgb(j, i, -1291911168);
                }
            }
        }

        this.texture.upload();
    }
}