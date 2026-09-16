package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.helpers.IndicatorHelper;
import me.itz0cat.catclient.mod.mods.NametagsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Shadow @Final protected EntityRenderManager dispatcher;
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo ci) {
        NametagsMod mod = CatClient.modManager().getMod(NametagsMod.class);
        if (!mod.isEnabled()) return;
        ci.cancel();

        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0)) {
            Vec3d vec3d = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw(f));
            if (vec3d != null) {
                boolean bl = !entity.isSneaky();
                int j = "deadmau5".equals(text.getString()) ? -10 : 0;
                matrixStack.push();
                matrixStack.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
                matrixStack.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
                matrixStack.scale(0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
                float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
                int k = (int)(g * 255.0F) << 24;
                TextRenderer textRenderer = this.getTextRenderer();
                float h = (float)(-textRenderer.getWidth(text) / 2);
                k = (int)(mod.opacity.getFValue() * 255.0F) << 24;
                if (mod.textShadow.isEnabled()) {
                    textRenderer.draw(text, h, (float) j, 553648127, true, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, k, i);
                    if (bl) {
                        textRenderer.draw(text, h, (float) j, -1, true, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i);
                    }
                    textRenderer.draw(text, h, (float) j, 553648127, false, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, k, i);
                    if (bl) {
                        textRenderer.draw(text, h, (float) j, -1, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i);
                    }
                } else {
                    textRenderer.draw(text, h, (float) j, 553648127, false, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, k, i);
                    if (bl) {
                        textRenderer.draw(text, h, (float) j, -1, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i);
                    }
                }

                matrixStack.pop();
            }
        }
    }

    @Inject(method = "renderLabelIfPresent", at = @At("TAIL"))
    public void addBadges(T entity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayerEntity && text.getString().contains(entity.getName().getString()))
            IndicatorHelper.addBadge(entity, matrixStack, vertexConsumerProvider);
    }
}
*/
