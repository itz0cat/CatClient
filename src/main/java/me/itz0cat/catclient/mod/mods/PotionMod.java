package me.itz0cat.catclient.mod.mods;

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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PotionMod extends Mod implements HudElement {
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final ColorSetting durationText = new ColorSetting("Duration Text Color", this, new JColor(0.7f, 0.7f, 0.7f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);
    public final BooleanSetting hideVanilla = new BooleanSetting("Hide Vanilla Potions", this, true);

    public PotionMod() {
        super("Potion HUD", "Shows your potions.", "\uF0C3");
        this.category = Category.HUD;
        HudManager.getInstance().register(this);
    }

    @Override
    public int getWidth() {
        return 90;
    }

    @Override
    public int getHeight() {
        if (mc.player == null) return 32;
        int count = mc.player.getStatusEffects().size();
        return Math.max(20, count * 20);
    }

    @Override
    public HudPosition getPosition() {
        return this.position;
    }

    @Override
    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        position.scale = scale.getFValue();
        if (mc.player == null) return;

        List<StatusEffectInstance> effects = new ArrayList<>(mc.player.getStatusEffects());
        int y = 0;
        int nameColor = text.getColor().getRGB();
        int durColor = durationText.getColor().getRGB();
        boolean shadow = textShadow.isEnabled();

        for (StatusEffectInstance effect : effects) {
            String name = Text.translatable(effect.getTranslationKey()).getString() + " " + (effect.getAmplifier() + 1);
            String dur = StatusEffectUtil.getDurationText(effect, 1, 20).getString();

            context.drawText(mc.textRenderer, name, 2, y, nameColor, shadow);
            context.drawText(mc.textRenderer, dur, 2, y + mc.textRenderer.fontHeight + 1, durColor, shadow);
            y += 20;
        }
    }

    @Override
    public void renderPlaceholder(DrawContext context) {
        HudRenderer.drawPlaceholderBox(context, getName(), getWidth(), 32);
    }
}
