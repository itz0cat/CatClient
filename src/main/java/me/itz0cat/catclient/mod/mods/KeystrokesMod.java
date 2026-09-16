package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.api.helpers.KeystrokeHelper;
import me.itz0cat.catclient.hud.HudElement;
import me.itz0cat.catclient.hud.HudManager;
import me.itz0cat.catclient.hud.HudPosition;
import me.itz0cat.catclient.hud.HudRenderer;
import me.itz0cat.catclient.mod.Category;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.glfw.GLFW;

public class KeystrokesMod extends Mod implements HudElement {
    public final ColorSetting background = new ColorSetting("Background Color", this, new JColor(0f, 0f, 0f, 0.75f), true);
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final ColorSetting pressedBackground = new ColorSetting("Pressed Background Color", this, new JColor(1f, 1f, 1f, 0.75f), true);
    public final ColorSetting pressedText = new ColorSetting("Pressed Text Color", this, new JColor(0f, 0f, 0f), false);

    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final BooleanSetting mouseButtons = new BooleanSetting("Mouse Buttons", this, true);
    public final BooleanSetting spaceBar = new BooleanSetting("Space Bar", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);

    public KeystrokesMod() {
        super("Keystrokes", "Shows your keystrokes.", "\uF11C");
        this.category = Category.HUD;
        HudManager.getInstance().register(this);

        if (KeystrokeHelper.list.isEmpty()) {
            new KeystrokeHelper(GLFW.GLFW_KEY_W, "W");
            new KeystrokeHelper(GLFW.GLFW_KEY_A, "A");
            new KeystrokeHelper(GLFW.GLFW_KEY_S, "S");
            new KeystrokeHelper(GLFW.GLFW_KEY_D, "D");
            new KeystrokeHelper(GLFW.GLFW_MOUSE_BUTTON_LEFT, "LMB");
            new KeystrokeHelper(GLFW.GLFW_MOUSE_BUTTON_RIGHT, "RMB");
            new KeystrokeHelper(GLFW.GLFW_KEY_SPACE, "---");
        }
    }

    @Override
    public int getWidth() {
        return 70;
    }

    @Override
    public int getHeight() {
        int h = 46;
        if (mouseButtons.isEnabled()) h += 22;
        if (spaceBar.isEnabled()) h += 16;
        return h;
    }

    @Override
    public HudPosition getPosition() {
        return this.position;
    }

    @Override
    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        position.scale = scale.getFValue();
        boolean shadow = textShadow.isEnabled();

        KeystrokeHelper wKey = KeystrokeHelper.getHelper(GLFW.GLFW_KEY_W);
        KeystrokeHelper aKey = KeystrokeHelper.getHelper(GLFW.GLFW_KEY_A);
        KeystrokeHelper sKey = KeystrokeHelper.getHelper(GLFW.GLFW_KEY_S);
        KeystrokeHelper dKey = KeystrokeHelper.getHelper(GLFW.GLFW_KEY_D);

        if (wKey != null) wKey.draw(context, 24, 0, 22, 22, background.getColor(), pressedBackground.getColor(), text.getColor(), pressedText.getColor(), shadow);
        if (aKey != null) aKey.draw(context, 0, 24, 22, 22, background.getColor(), pressedBackground.getColor(), text.getColor(), pressedText.getColor(), shadow);
        if (sKey != null) sKey.draw(context, 24, 24, 22, 22, background.getColor(), pressedBackground.getColor(), text.getColor(), pressedText.getColor(), shadow);
        if (dKey != null) dKey.draw(context, 48, 24, 22, 22, background.getColor(), pressedBackground.getColor(), text.getColor(), pressedText.getColor(), shadow);

        int currentY = 48;
        if (mouseButtons.isEnabled()) {
            KeystrokeHelper lmb = KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_LEFT);
            KeystrokeHelper rmb = KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
            if (lmb != null) lmb.draw(context, 0, currentY, 34, 20, background.getColor(), pressedBackground.getColor(), text.getColor(), pressedText.getColor(), shadow);
            if (rmb != null) rmb.draw(context, 36, currentY, 34, 20, background.getColor(), pressedBackground.getColor(), text.getColor(), pressedText.getColor(), shadow);
            currentY += 22;
        }

        if (spaceBar.isEnabled()) {
            KeystrokeHelper space = KeystrokeHelper.getHelper(GLFW.GLFW_KEY_SPACE);
            if (space != null) space.draw(context, 0, currentY, 70, 14, background.getColor(), pressedBackground.getColor(), text.getColor(), pressedText.getColor(), shadow);
        }
    }

    @Override
    public void renderPlaceholder(DrawContext context) {
        HudRenderer.drawPlaceholderBox(context, getName(), getWidth(), getHeight());
    }
}
