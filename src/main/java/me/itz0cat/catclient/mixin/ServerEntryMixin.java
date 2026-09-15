package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.multiplayer.*;
import net.minecraft.client.network.ServerInfo;

@Mixin(value = MultiplayerServerListWidget.ServerEntry.class, priority = 0)
public class ServerEntryMixin {

    @Shadow
    private @Final ServerInfo server;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)V", ordinal = 0))
    public void changeText(DrawContext instance, TextRenderer textRenderer, String string, int i, int j, int k, boolean bl) {
        if (server != null && CatClient.starServers.contains(server.address.toLowerCase())) {
            instance.drawText(textRenderer, "★", i - 50, j + 14, 16776960, true);
        }
        instance.drawText(textRenderer, string, i, j, k, bl);
    }
}
