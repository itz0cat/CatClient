package me.itz0cat.catclient.mixin;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.systems.RenderSystem;
import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.HudRenderEvent;
import me.itz0cat.catclient.mod.mods.ArmorMod;
import me.itz0cat.catclient.mod.mods.CrosshairMod;
import me.itz0cat.catclient.mod.mods.PotionMod;
import me.itz0cat.catclient.mod.mods.ScoreboardMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.itz0cat.catclient.CatClient.mc;

@Mixin(value = InGameHud.class, priority = 9999)
public class InGameHudMixin {

    @Shadow
    private PlayerEntity getCameraPlayer() {
        return null;
    }

    @Shadow
    private void renderHotbarItem(DrawContext drawContext, int i, int j, RenderTickCounter renderTickCounter, PlayerEntity playerEntity, ItemStack itemStack, int k) {

    }

    @Shadow private int renderHealthValue;

    @Inject(
            method = "renderHotbar",
            at = @At("TAIL")
    )
    private void armorhud$injectArmorHUD(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if (CatClient.modManager().getMod(ArmorMod.class).isEnabled()) this.armorhud$renderArmorHUD(renderTickCounter, drawContext);
    }

    private static final net.minecraft.entity.EquipmentSlot[] ARMOR_SLOTS = new net.minecraft.entity.EquipmentSlot[] {
        net.minecraft.entity.EquipmentSlot.HEAD,
        net.minecraft.entity.EquipmentSlot.CHEST,
        net.minecraft.entity.EquipmentSlot.LEGS,
        net.minecraft.entity.EquipmentSlot.FEET
    };

    private void armorhud$renderArmorHUD(RenderTickCounter renderTickCounter, DrawContext drawContext) {
        ArmorMod mod = CatClient.modManager().getMod(ArmorMod.class);

        PlayerEntity player = this.getCameraPlayer();
        int xPos = (int) (mod.position.x / mc.options.getGuiScale().getValue());
        int yPos = (int) (mod.position.y / mc.options.getGuiScale().getValue());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if(mod.direction.getMode().equals("Vertical")) {
            for (int i = 0; i < 4; i++) {
                this.renderHotbarItem(drawContext, xPos + 3, yPos + 16 * i, renderTickCounter, player, player.getEquippedStack(ARMOR_SLOTS[i]), 1);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                this.renderHotbarItem(drawContext, xPos +  16 * i, yPos + 3, renderTickCounter, player, player.getEquippedStack(ARMOR_SLOTS[i]), 1);
            }
        }
        RenderSystem.disableBlend();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo ci) {
        CatClient.EVENTBUS.post(HudRenderEvent.get(drawContext.getMatrices(), renderTickCounter.getTickDelta(true)));
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffectOverlay(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if(CatClient.modManager().getMod(PotionMod.class).isEnabled() && CatClient.modManager().getMod(PotionMod.class).hideVanilla.isEnabled()) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if(!CatClient.modManager().getMod(CrosshairMod.class).isEnabled()) return;
        ci.cancel();
        if (!mc.options.getPerspective().isFirstPerson()) return;

        CrosshairMod mod = CatClient.modManager().getMod(CrosshairMod.class);
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
                if (mod.crosshair[row][col]) {
                    if (mc.targetedEntity instanceof LivingEntity) {
                        drawRectangle(drawContext,
                                mc.getWindow().getScaledWidth() / 2 - 5 + col,
                                mc.getWindow().getScaledHeight() / 2 - 5 + row,
                                1, 1, mod.targetColor.getColor().getRGB()
                        );
                    } else {
                        drawRectangle(drawContext,
                                mc.getWindow().getScaledWidth() / 2 - 5 + col,
                                mc.getWindow().getScaledHeight() / 2 - 5 + row,
                                1, 1, mod.color.getColor().getRGB()
                        );
                    }
                }
            }
        }

        AttackIndicator indicator = mc.options.getAttackIndicator().getValue();
        if (indicator == AttackIndicator.CROSSHAIR) {
            float progress = mc.player.getAttackCooldownProgress(0.0F);

            // Whether a cross should be displayed under the indicator
            boolean targetingEntity = false;
            if (mc.targetedEntity != null && mc.targetedEntity instanceof LivingEntity
                    && progress >= 1.0F) {
                targetingEntity = mc.player.getAttackCooldownProgressPerTick() > 5.0F;
                targetingEntity &= mc.targetedEntity.isAlive();
            }

            int x = (int) ((mc.getWindow().getScaledWidth()) / 2 - 8);
            int y = (int) ((mc.getWindow().getScaledHeight()) / 2 - 7 + 16);

            if (targetingEntity) {
                drawContext.drawTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, Identifier.of("textures/gui/icons.png"), x, y, 68f, 94f, 16, 16, 256, 256);
            } else if (progress < 1.0F) {
                int k = (int) (progress * 17.0F);
                drawContext.drawTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, Identifier.of("textures/gui/icons.png"), x, y, 36f, 94f, 16, 4, 256, 256);
                drawContext.drawTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, Identifier.of("textures/gui/icons.png"), x, y, 52f, 94f, k, 4, 256, 256);
            }
            RenderSystem.defaultBlendFunc();
        }
    }
    private static void drawRectangle(DrawContext drawContext, int x, int y, int w, int h, int color) {
        drawContext.fill(x, y, x + w, y + h, color);
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(DrawContext drawContext, ScoreboardObjective scoreboardObjective, CallbackInfo ci) {
        if (CatClient.modManager().getMod(ScoreboardMod.class).isEnabled() && CatClient.modManager().getMod(ScoreboardMod.class).hideScoreboard.isEnabled()) {
            ci.cancel();
        }
    }

    //@Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", ordinal = 1))
    //private int scoreboard$drawHeadingText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
    //    if (CatClient.modManager().getMod(ScoreboardMod.class).isEnabled() && CatClient.modManager().getMod(ScoreboardMod.class).textShadow.isEnabled()) {
    //        return context.drawTextWithShadow(textRenderer, text, x, y, color);
    //    }
    //    return context.drawText(textRenderer, text, x, y, color, false);
    //}
//
    //@Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", ordinal = 0))
    //private int scoreboard$drawText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
    //    if (CatClient.modManager().getMod(ScoreboardMod.class).isEnabled() && CatClient.modManager().getMod(ScoreboardMod.class).textShadow.isEnabled()) {
    //        return context.drawTextWithShadow(textRenderer, text, x, y, color);
    //    }
    //    return context.drawText(textRenderer, text, x, y, color, false);
    //}
//
    //@ModifyArg(
    //        method = "renderScoreboardSidebar",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"
    //        ),
    //        index = 1
    //)
    //public String removeNumbers(String text) {
    //    if (CatClient.modManager().getMod(ScoreboardMod.class).isEnabled() && !CatClient.modManager().getMod(ScoreboardMod.class).numbers.isEnabled()) return "";
    //    else return text;
    //}
//
    //@Redirect(method = "renderScoreboardSidebar", slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 0))
    //private int scoreboard$modifyScoreWidth(TextRenderer textRenderer, String score, DrawContext context, ScoreboardObjective objective) {
    //    return (CatClient.modManager().getMod(ScoreboardMod.class).isEnabled() && !CatClient.modManager().getMod(ScoreboardMod.class).numbers.isEnabled()) ? 0 : textRenderer.getWidth(score);
    //}
//
//
    //@Redirect(
    //        method = "renderScoreboardSidebar",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I",
    //                ordinal = 0
    //        )
    //)
    //public int scoreboard$setBackgroundOpacity(GameOptions instance, float fallbackOpacity) {
    //    if(!CatClient.modManager().getMod(ScoreboardMod.class).isEnabled()) return mc.options.getTextBackgroundColor(fallbackOpacity);
    //    return (CatClient.modManager().getMod(ScoreboardMod.class).background.isEnabled()) ? mc.options.getTextBackgroundColor(fallbackOpacity) : mc.options.getTextBackgroundColor(0F);
    //}
//
    //@Redirect(
    //        method = "renderScoreboardSidebar",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I",
    //                ordinal = 1
    //        )
    //)
    //public int scoreboard$setBackgroundOpacity2(GameOptions instance, float fallbackOpacity) {
    //    if(!CatClient.modManager().getMod(ScoreboardMod.class).isEnabled()) return mc.options.getTextBackgroundColor(fallbackOpacity);
    //    return (CatClient.modManager().getMod(ScoreboardMod.class).background.isEnabled()) ? mc.options.getTextBackgroundColor(fallbackOpacity) : mc.options.getTextBackgroundColor(0F);
    //}
}
