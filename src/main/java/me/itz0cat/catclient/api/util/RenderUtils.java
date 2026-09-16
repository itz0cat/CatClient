package me.itz0cat.catclient.api.util;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.mod.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class RenderUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean isRenderable() {
        if (mc.player == null || mc.world == null) return false;
        if (mc.options.hudHidden) return false;
        if (mc.currentScreen instanceof me.itz0cat.catclient.ui.CatHudEditScreen) return false;

        GeneralSettings generalSettings = CatClient.modManager().getMod(GeneralSettings.class);
        if (generalSettings != null) {
            if (mc.currentScreen instanceof ChatScreen && !generalSettings.showInChat.isEnabled())
                return false;
            if (mc.currentScreen instanceof InventoryScreen && !generalSettings.showInInventory.isEnabled())
                return false;
        }

        if (mc.currentScreen != null &&
                !(mc.currentScreen instanceof InventoryScreen) &&
                !(mc.currentScreen instanceof ChatScreen)
        )
            return false;

        return true;
    }
}
