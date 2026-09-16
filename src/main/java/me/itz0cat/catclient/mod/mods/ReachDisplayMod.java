package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.event.events.AttackEntityEvent;
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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;

public class ReachDisplayMod extends Mod implements HudElement {
    public final ColorSetting background = new ColorSetting("Background Color", this, new JColor(0f, 0f, 0f, 0.75f), true);
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);
    public final NumberSetting width = new NumberSetting("Width", this, 75, 30, 150, 1);
    public final NumberSetting height = new NumberSetting("Height", this, 16, 10, 50, 1);
    public final BooleanSetting backgroundEnabled = new BooleanSetting("Background", this, true);

    private double blocks = 0.000;
    private final DecimalFormat dF = new DecimalFormat("#.###");

    public ReachDisplayMod() {
        super("Reach Display", "Shows your reach.", "\uF6DE");
        this.category = Category.COMBAT;
        HudManager.getInstance().register(this);
    }

    @EventHandler
    public void onAttack(AttackEntityEvent e) {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY && mc.player != null) {
            Vec3d cameraPos = mc.player.getCameraPosVec(1);
            blocks = mc.crosshairTarget.getPos().distanceTo(cameraPos);
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
        String textStr = (backgroundEnabled.isEnabled() ? "" : "[") + dF.format(blocks) + " blocks" + (backgroundEnabled.isEnabled() ? "" : "]");
        HudRenderer.drawHudBox(context, getWidth(), getHeight(), background.getColor(), backgroundEnabled.isEnabled(), text.getColor(), textShadow.isEnabled(), textStr);
    }

    @Override
    public void renderPlaceholder(DrawContext context) {
        HudRenderer.drawPlaceholderBox(context, getName(), getWidth(), getHeight());
    }
}
