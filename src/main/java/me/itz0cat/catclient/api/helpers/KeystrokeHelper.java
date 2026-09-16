package me.itz0cat.catclient.api.helpers;

import me.itz0cat.catclient.api.font.JColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class KeystrokeHelper {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private int key;
    private String display;
    private long pressTime;
    private boolean pressed;

    public static final List<KeystrokeHelper> list = new ArrayList<>();

    public KeystrokeHelper(int key, String display) {
        this.key = key;
        this.display = display;
        list.add(this);
    }

    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public String getDisplay() { return display; }
    public void setDisplay(String display) { this.display = display; }
    public long getPressTime() { return pressTime; }
    public void setPressTime(long pressTime) { this.pressTime = pressTime; }
    public boolean isPressed() { return pressed; }
    public void setPressed(boolean pressed) { this.pressed = pressed; }

    public static KeystrokeHelper getHelper(int key) {
        for (KeystrokeHelper k : list) {
            if (k.key == key) return k;
        }
        return null;
    }

    public void draw(DrawContext context, int x, int y, int w, int h, JColor normalBg, JColor pressedBg, JColor normalText, JColor pressedText, boolean shadow) {
        int bgColor = pressed ? pressedBg.getRGB() : normalBg.getRGB();
        int textColor = pressed ? pressedText.getRGB() : normalText.getRGB();
        context.fill(x, y, x + w, y + h, bgColor);

        int textW = mc.textRenderer.getWidth(display);
        int textX = x + Math.max(1, (w - textW) / 2);
        int textY = y + Math.max(1, (h - mc.textRenderer.fontHeight) / 2);
        context.drawText(mc.textRenderer, display, textX, textY, textColor, shadow);
    }
}
