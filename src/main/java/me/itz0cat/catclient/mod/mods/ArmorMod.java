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
import me.itz0cat.catclient.mod.setting.settings.ModeSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ArmorMod extends Mod implements HudElement {
    public final BooleanSetting showDura = new BooleanSetting("Durability", this, true);
    public final ModeSetting duraMode = new ModeSetting("Durability Mode", this, "Numbers", "Numbers", "Percentages");
    public final ColorSetting textSetting = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final ModeSetting direction = new ModeSetting("Direction", this, "Vertical", "Vertical", "Horizontal");

    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] {
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET
    };

    public ArmorMod() {
        super("Armor Status", "Shows your armor status.", "\uF132");
        this.category = Category.HUD;
        HudManager.getInstance().register(this);
    }

    @Override
    public int getWidth() {
        return direction.is("Vertical") ? (showDura.isEnabled() ? 48 : 20) : (4 * 20);
    }

    @Override
    public int getHeight() {
        return direction.is("Vertical") ? (4 * 20) : 20;
    }

    @Override
    public HudPosition getPosition() {
        return this.position;
    }

    @Override
    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        if (mc.player == null) return;
        boolean isVert = direction.is("Vertical");
        boolean shadow = textShadow.isEnabled();
        int textColor = textSetting.getColor().getRGB();

        for (int i = 0; i < 4; i++) {
            ItemStack stack = mc.player.getEquippedStack(ARMOR_SLOTS[i]);
            int x = isVert ? 0 : (i * 20);
            int y = isVert ? (i * 20) : 0;

            if (!stack.isEmpty()) {
                context.drawItem(stack, x, y);
                context.drawStackOverlay(mc.textRenderer, stack, x, y);

                if (showDura.isEnabled() && stack.isDamageable()) {
                    int maxDmg = stack.getMaxDamage();
                    int curDmg = stack.getDamage();
                    String duraText = duraMode.is("Numbers")
                            ? String.valueOf(maxDmg - curDmg)
                            : (int) ((1.0f - (float) curDmg / maxDmg) * 100) + "%";

                    if (isVert) {
                        context.drawText(mc.textRenderer, duraText, x + 20, y + 4, textColor, shadow);
                    }
                }
            }
        }
    }

    @Override
    public void renderPlaceholder(DrawContext context) {
        HudRenderer.drawPlaceholderBox(context, "Armor", getWidth(), getHeight());
    }
}
