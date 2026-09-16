package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.event.events.KeyPressEvent;
import me.itz0cat.catclient.api.event.events.TickEvent;
import me.itz0cat.catclient.api.event.orbit.EventHandler;
import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.hud.HudElement;
import me.itz0cat.catclient.hud.HudManager;
import me.itz0cat.catclient.hud.HudPosition;
import me.itz0cat.catclient.hud.HudRenderer;
import me.itz0cat.catclient.mod.Category;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;

public class ToggleSprintMod extends Mod implements HudElement {
    private boolean toggled = false;
    public final ColorSetting background = new ColorSetting("Background Color", this, new JColor(0f, 0f, 0f, 0.75f), true);
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);
    public final NumberSetting width = new NumberSetting("Width", this, 95, 40, 180, 1);
    public final NumberSetting height = new NumberSetting("Height", this, 16, 10, 50, 1);
    public final BooleanSetting backgroundEnabled = new BooleanSetting("Background", this, true);

    public ToggleSprintMod() {
        super("Toggle Sprint", "Allows you to toggle sprint.", "\uF183");
        this.category = Category.MOVEMENT;
        HudManager.getInstance().register(this);
    }

    public boolean isSprinting() {
        return toggled;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.options != null && mc.options.sprintKey != null) {
            mc.options.sprintKey.setPressed(false);
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post e) {
        if (mc.options != null && mc.options.sprintKey != null) {
            mc.options.sprintKey.setPressed(toggled);
        }
    }

    @EventHandler
    public void onKey(KeyPressEvent e) {
        if (mc.options != null && mc.options.sprintKey != null &&
                InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), mc.options.sprintKey.getDefaultKey().getCode())) {
            toggled = !toggled;
        }
    }

    @Override
    public int getWidth() {
        return (int) width.getValue();
    }

    @Override
    public int getHeight() {
        return (int) height.getValue();
    }

    @Override
    public HudPosition getPosition() {
        return this.position;
    }

    @Override
    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        position.scale = scale.getFValue();
        String status = isSprinting() ? "Toggled" : "Vanilla";
        String textStr = (backgroundEnabled.isEnabled() ? "" : "[") + "Sprint: " + status + (backgroundEnabled.isEnabled() ? "" : "]");
        HudRenderer.drawHudBox(context, getWidth(), getHeight(), background.getColor(), backgroundEnabled.isEnabled(), text.getColor(), textShadow.isEnabled(), textStr);
    }

    @Override
    public void renderPlaceholder(DrawContext context) {
        HudRenderer.drawPlaceholderBox(context, getName(), getWidth(), getHeight());
    }
}
