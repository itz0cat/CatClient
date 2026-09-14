package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.event.events.PlayerTickEvent;
import me.itz0cat.catclient.api.event.orbit.EventHandler;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ModeSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullbrightMod extends Mod {

    public final ModeSetting mode = new ModeSetting("Mode", this, "Gamma", "Gamma", "Night Vision");
    public final NumberSetting brightness = new NumberSetting("Brightness", this, 10.0, 1.0, 20.0, 0.5);
    public final BooleanSetting disableInMultiplayer = new BooleanSetting("Disable in MP", this, false);
    public final BooleanSetting hideNvEffect = new BooleanSetting("Hide NV Icon", this, true);

    private Double originalGamma = null;

    public FullbrightMod() {
        super("Fullbright", "Maximum brightness without torches or darkness.", "\uF185");
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null && "Gamma".equals(mode.getMode())) {
            originalGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(brightness.getValue());
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null && originalGamma != null) {
            mc.options.getGamma().setValue(originalGamma);
            originalGamma = null;
        }
        if (mc.player != null && mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        if (!isEnabled()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        if (disableInMultiplayer.isEnabled() && !mc.isInSingleplayer()) {
            return;
        }

        String currentMode = mode.getMode();
        if ("Night Vision".equals(currentMode)) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1000, 0, false, !hideNvEffect.isEnabled(), !hideNvEffect.isEnabled()));
        } else {
            if (mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                StatusEffectInstance effect = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
                if (effect != null && effect.getDuration() > 600) {
                    mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                }
            }
        }

        if ("Gamma".equals(currentMode)) {
            if (originalGamma == null && mc.options != null) {
                originalGamma = mc.options.getGamma().getValue();
            }
            if (mc.options != null) {
                mc.options.getGamma().setValue(brightness.getValue());
            }
        } else if (originalGamma != null && mc.options != null) {
            mc.options.getGamma().setValue(originalGamma);
            originalGamma = null;
        }
    }
}
