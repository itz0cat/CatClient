package me.itz0cat.catclient.mod.setting.settings;

import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.Setting;

public class ColorSetting extends Setting {
    private JColor color;
    private final boolean alpha;
    private boolean rainbow;

    public ColorSetting(String name, Mod parent, JColor color, boolean alpha) {
        this.name = name;
        this.parent = parent;
        this.color = color;
        this.alpha = alpha;
        this.rainbow = false;

        if (parent != null) parent.addSettings(this);
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public boolean hasAlpha() {
        return alpha;
    }

    public JColor getValue() {
        return color;
    }

    public JColor getColor() {
        if (rainbow) return getRainbow(0, this.getValue().getAlpha());
        return color;
    }

    public static JColor getRainbow(int incr, int alpha) {
        JColor color = JColor.fromHSB(((System.currentTimeMillis() + incr * 200) % (360 * 20)) / (360f * 20), 0.5f, 1f);
        return new JColor(color.getRed(), color.getBlue(), color.getGreen(), alpha);
    }

    public void setColor(JColor color, boolean rainbow) {
        this.color = color;
        this.rainbow = rainbow;
    }

    public void setColor(JColor color) {
        this.color = color;
    }
}
