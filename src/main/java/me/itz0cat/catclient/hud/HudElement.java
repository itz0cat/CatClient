package me.itz0cat.catclient.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface HudElement {
    String getName();
    boolean isEnabled();
    HudPosition getPosition();
    int getWidth();
    int getHeight();
    void renderHud(DrawContext context, RenderTickCounter tickCounter);
    void renderPlaceholder(DrawContext context);
}
