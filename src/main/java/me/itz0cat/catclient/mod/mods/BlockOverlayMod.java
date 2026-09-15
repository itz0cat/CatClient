package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class BlockOverlayMod extends Mod {

    public final BooleanSetting enableOutline = new BooleanSetting("Enable Outline", this, true);
    public final ColorSetting outlineColor = new ColorSetting("Outline Color", this, new JColor(0.00f, 0.78f, 1.00f, 1.00f), false);
    public final NumberSetting outlineThickness = new NumberSetting("Thickness", this, 2.0, 0.5, 8.0, 0.5);
    public final BooleanSetting rainbowOutline = new BooleanSetting("Rainbow Outline", this, false);

    public final BooleanSetting enableFill = new BooleanSetting("Enable Fill", this, true);
    public final ColorSetting fillColor = new ColorSetting("Fill Color", this, new JColor(0.00f, 0.50f, 1.00f, 0.25f), true);
    public final BooleanSetting rainbowFill = new BooleanSetting("Rainbow Fill", this, false);

    public final BooleanSetting smoothAnimation = new BooleanSetting("Smooth Animation", this, true);
    public final NumberSetting animationSpeed = new NumberSetting("Animation Speed", this, 14.0, 1.0, 30.0, 1.0);
    public final BooleanSetting ignoreDepth = new BooleanSetting("Ignore Depth", this, false);

    private BlockPos lastPos = null;
    private float animX, animY, animZ;
    private float animW, animH, animD;
    private float animProgress = 0f;
    private long lastFrameTime = 0;

    public BlockOverlayMod() {
        super("Block Overlay", "Custom animated block selection highlight.", "\uF1B2");
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(this::onBlockOutline);
    }

    private boolean onBlockOutline(WorldRenderContext context, Object outlineContext) {
        if (!isEnabled()) {
            return true;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            lastPos = null;
            return false;
        }

        BlockHitResult blockHit = (BlockHitResult) mc.crosshairTarget;
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = mc.world.getBlockState(pos);

        if (state.isAir()) {
            lastPos = null;
            return false;
        }

        VoxelShape shape = state.getOutlineShape(mc.world, pos);
        if (shape.isEmpty()) {
            lastPos = null;
            return false;
        }

        Box box = shape.getBoundingBox();
        long timeNow = System.currentTimeMillis();
        if (lastFrameTime == 0) lastFrameTime = timeNow;
        float frameDelta = (timeNow - lastFrameTime) / 50.0f;
        lastFrameTime = timeNow;

        float inflate = 0.002f;
        float targetX = pos.getX() + (float) box.minX - inflate;
        float targetY = pos.getY() + (float) box.minY - inflate;
        float targetZ = pos.getZ() + (float) box.minZ - inflate;
        float targetW = (float) (box.maxX - box.minX) + (inflate * 2);
        float targetH = (float) (box.maxY - box.minY) + (inflate * 2);
        float targetD = (float) (box.maxZ - box.minZ) + (inflate * 2);

        if (smoothAnimation.isEnabled()) {
            if (lastPos == null || !lastPos.equals(pos)) {
                if (lastPos == null) {
                    animX = targetX; animY = targetY; animZ = targetZ;
                    animW = targetW; animH = targetH; animD = targetD;
                    animProgress = 0f;
                }
                lastPos = pos;
            }

            float speed = (float) animationSpeed.getValue();
            float smoothFactor = 1.0f - (float) Math.pow(1.0 - (speed / 40.0), frameDelta);
            smoothFactor = MathHelper.clamp(smoothFactor, 0.01f, 1.0f);

            animX += (targetX - animX) * smoothFactor;
            animY += (targetY - animY) * smoothFactor;
            animZ += (targetZ - animZ) * smoothFactor;
            animW += (targetW - animW) * smoothFactor;
            animH += (targetH - animH) * smoothFactor;
            animD += (targetD - animD) * smoothFactor;

            animProgress += (1.0f - animProgress) * smoothFactor;
        } else {
            animX = targetX; animY = targetY; animZ = targetZ;
            animW = targetW; animH = targetH; animD = targetD;
            animProgress = 1.0f;
            lastPos = pos;
        }

        renderCustomOutline(context, mc.gameRenderer.getCamera());
        return false;
    }

    private void renderCustomOutline(WorldRenderContext context, Camera camera) {
        if (animProgress < 0.01f) return;

        double camX = camera.getCameraPos().x;
        double camY = camera.getCameraPos().y;
        double camZ = camera.getCameraPos().z;

        float drawX = (float) (animX - camX);
        float drawY = (float) (animY - camY);
        float drawZ = (float) (animZ - camZ);

        int outColor = rainbowOutline.isEnabled() ? getRainbowColor(1.0f) : outlineColor.getColor().getRGB();
        int fColor = rainbowFill.isEnabled() ? (0x40 << 24) | (getRainbowColor(0.25f) & 0x00FFFFFF) : fillColor.getColor().getRGB();

        MatrixStack matrices = context.matrices();
        matrices.push();
        matrices.translate(drawX, drawY, drawZ);

        if (enableFill.isEnabled()) {
            VertexConsumer fillBuffer = context.consumers().getBuffer(net.minecraft.client.render.RenderLayers.debugQuads());
            drawBoxFill(matrices, fillBuffer, animW, animH, animD, fColor);
        }

        if (enableOutline.isEnabled()) {
            VertexConsumer lineBuffer = context.consumers().getBuffer(net.minecraft.client.render.RenderLayers.lines());
            drawBoxOutline(matrices, lineBuffer, animW, animH, animD, outColor);
        }

        matrices.pop();
    }

    private int getRainbowColor(float saturation) {
        float hue = (System.currentTimeMillis() % 4000L) / 4000.0f;
        return java.awt.Color.HSBtoRGB(hue, saturation, 1.0f);
    }

    private void drawBoxFill(MatrixStack matrices, VertexConsumer consumer, float w, float h, float d, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        MatrixStack.Entry entry = matrices.peek();

        consumer.vertex(entry, 0, 0, 0).color(r, g, b, a);
        consumer.vertex(entry, w, 0, 0).color(r, g, b, a);
        consumer.vertex(entry, w, 0, d).color(r, g, b, a);
        consumer.vertex(entry, 0, 0, d).color(r, g, b, a);

        consumer.vertex(entry, 0, h, 0).color(r, g, b, a);
        consumer.vertex(entry, 0, h, d).color(r, g, b, a);
        consumer.vertex(entry, w, h, d).color(r, g, b, a);
        consumer.vertex(entry, w, h, 0).color(r, g, b, a);

        consumer.vertex(entry, 0, 0, 0).color(r, g, b, a);
        consumer.vertex(entry, 0, h, 0).color(r, g, b, a);
        consumer.vertex(entry, w, h, 0).color(r, g, b, a);
        consumer.vertex(entry, w, 0, 0).color(r, g, b, a);

        consumer.vertex(entry, 0, 0, d).color(r, g, b, a);
        consumer.vertex(entry, w, 0, d).color(r, g, b, a);
        consumer.vertex(entry, w, h, d).color(r, g, b, a);
        consumer.vertex(entry, 0, h, d).color(r, g, b, a);

        consumer.vertex(entry, 0, 0, 0).color(r, g, b, a);
        consumer.vertex(entry, 0, 0, d).color(r, g, b, a);
        consumer.vertex(entry, 0, h, d).color(r, g, b, a);
        consumer.vertex(entry, 0, h, 0).color(r, g, b, a);

        consumer.vertex(entry, w, 0, 0).color(r, g, b, a);
        consumer.vertex(entry, w, h, 0).color(r, g, b, a);
        consumer.vertex(entry, w, h, d).color(r, g, b, a);
        consumer.vertex(entry, w, 0, d).color(r, g, b, a);
    }

    private void drawBoxOutline(MatrixStack matrices, VertexConsumer consumer, float w, float h, float d, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        if (a == 0) a = 255;

        MatrixStack.Entry entry = matrices.peek();

        addLine(consumer, entry, 0, 0, 0, w, 0, 0, r, g, b, a);
        addLine(consumer, entry, w, 0, 0, w, 0, d, r, g, b, a);
        addLine(consumer, entry, w, 0, d, 0, 0, d, r, g, b, a);
        addLine(consumer, entry, 0, 0, d, 0, 0, 0, r, g, b, a);

        addLine(consumer, entry, 0, h, 0, w, h, 0, r, g, b, a);
        addLine(consumer, entry, w, h, 0, w, h, d, r, g, b, a);
        addLine(consumer, entry, w, h, d, 0, h, d, r, g, b, a);
        addLine(consumer, entry, 0, h, d, 0, h, 0, r, g, b, a);

        addLine(consumer, entry, 0, 0, 0, 0, h, 0, r, g, b, a);
        addLine(consumer, entry, w, 0, 0, w, h, 0, r, g, b, a);
        addLine(consumer, entry, w, 0, d, w, h, d, r, g, b, a);
        addLine(consumer, entry, 0, 0, d, 0, h, d, r, g, b, a);
    }

    private void addLine(VertexConsumer consumer, MatrixStack.Entry entry, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        consumer.vertex(entry, x1, y1, z1).color(r, g, b, a).normal(entry, x2 - x1, y2 - y1, z2 - z1);
        consumer.vertex(entry, x2, y2, z2).color(r, g, b, a).normal(entry, x2 - x1, y2 - y1, z2 - z1);
    }
}
