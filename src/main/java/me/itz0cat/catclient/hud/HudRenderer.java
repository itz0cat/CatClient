package me.itz0cat.catclient.hud;

import me.itz0cat.catclient.api.font.JColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import java.util.List;

public class HudRenderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawHudBox(DrawContext context, int width, int height, JColor bg, boolean drawBg, JColor textCol, boolean shadow, String text) {
        if (drawBg && bg != null) {
            context.fill(0, 0, width, height, bg.getRGB());
        }
        int textWidth = mc.textRenderer.getWidth(text);
        int textHeight = mc.textRenderer.fontHeight;
        int textX = Math.max(2, (width - textWidth) / 2);
        int textY = Math.max(1, (height - textHeight) / 2);
        int color = textCol != null ? textCol.getRGB() : 0xFFFFFFFF;
        context.drawText(mc.textRenderer, text, textX, textY, color, shadow);
    }

    public static void drawMultiLineHudBox(DrawContext context, int width, int height, JColor bg, boolean drawBg, List<String> lines, JColor textCol, boolean shadow) {
        if (drawBg && bg != null) {
            context.fill(0, 0, width, height, bg.getRGB());
        }
        int color = textCol != null ? textCol.getRGB() : 0xFFFFFFFF;
        int lineHeight = mc.textRenderer.fontHeight + 2;
        int startY = Math.max(2, (height - (lines.size() * lineHeight)) / 2);
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(mc.textRenderer, lines.get(i), 4, startY + (i * lineHeight), color, shadow);
        }
    }

    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.drawStrokedRectangle(x, y, width, height, color);
    }

    public static void drawPlaceholderBox(DrawContext context, String name, int width, int height) {
        context.fill(0, 0, width, height, 0x900A0F1D);
        drawBorder(context, 0, 0, width, height, 0xFF00D2FF);
        int textWidth = mc.textRenderer.getWidth(name);
        int textX = Math.max(2, (width - textWidth) / 2);
        int textY = Math.max(1, (height - mc.textRenderer.fontHeight) / 2);
        context.drawText(mc.textRenderer, name, textX, textY, 0xFF00D2FF, true);
    }

    public static void drawItemWithOverlay(DrawContext context, ItemStack stack, int x, int y, String overlayText) {
        context.drawItem(stack, x, y);
        context.drawStackOverlay(mc.textRenderer, stack, x, y, overlayText);
    }
}
