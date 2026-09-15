package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.PlayerTickEvent;
import me.itz0cat.catclient.api.event.events.WorldRenderEvent;
import me.itz0cat.catclient.api.event.orbit.EventHandler;
import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.ModeSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssistMod extends Mod {

    public final BooleanSetting targetPlayers = new BooleanSetting("Target Players", this, true);
    public final BooleanSetting targetHostiles = new BooleanSetting("Target Hostiles", this, false);
    public final BooleanSetting targetPassives = new BooleanSetting("Target Passives", this, false);
    public final BooleanSetting invisible = new BooleanSetting("Target Invisible", this, false);

    public final NumberSetting range = new NumberSetting("Tracking Range", this, 4.5, 1.0, 6.0, 0.1);
    public final NumberSetting fov = new NumberSetting("FOV", this, 90.0, 10.0, 360.0, 5.0);
    public final ModeSetting targetPart = new ModeSetting("Aim Part", this, "Dynamic", "Head", "Chest", "Legs", "Random", "Dynamic");
    public final NumberSetting dynamicSpeed = new NumberSetting("Dynamic Speed", this, 10.0, 1.0, 20.0, 1.0);

    public final NumberSetting rotationSpeed = new NumberSetting("Rotation Speed", this, 120.0, 1.0, 720.0, 1.0);
    public final BooleanSetting clickOnly = new BooleanSetting("Click To Aim", this, false);
    public final BooleanSetting weaponOnly = new BooleanSetting("Weapon Only", this, false);
    public final NumberSetting humanizeNoise = new NumberSetting("Humanize Noise", this, 0.5, 0.0, 3.0, 0.1);
    public final BooleanSetting aimLock = new BooleanSetting("Aim Lock (Focus 1 Target)", this, false);

    public final BooleanSetting showTargetEsp = new BooleanSetting("Show Target ESP", this, false);
    public final ColorSetting targetColor = new ColorSetting("ESP Color", this, new JColor(0.00f, 0.78f, 1.00f, 1.00f), false);

    private Entity currentTarget = null;
    private Entity lockedTarget = null;

    private float nextYaw;
    private float nextPitch;

    private double noiseX = 0;
    private double noiseY = 0;
    private double noiseZ = 0;
    private long lastNoiseUpdate = 0;

    private String currentDynamicPart = "Chest";
    private long lastDynamicSwitch = 0;

    private double interpolatedAimY = -1;
    private Entity lastInterpolatedEntity = null;

    public AimAssistMod() {
        super("Aim Assist", "Smooth legitimate combat assistance with blue flame visuals.", "\uF05B");
        WorldRenderEvents.AFTER_ENTITIES.register(this::onWorldRender);
    }

    @Override
    public void onEnable() {
        currentTarget = null;
        lockedTarget = null;
        lastInterpolatedEntity = null;
    }

    @Override
    public void onDisable() {
        currentTarget = null;
        lockedTarget = null;
        lastInterpolatedEntity = null;
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        if (!isEnabled()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        currentTarget = null;

        if (mc.world == null || mc.player == null || mc.currentScreen != null) return;

        if (clickOnly.isEnabled() && !mc.options.attackKey.isPressed()) return;

        if (weaponOnly.isEnabled()) {
            net.minecraft.item.Item mainHand = mc.player.getMainHandStack().getItem();
            if (!(mainHand instanceof net.minecraft.item.SwordItem) && !(mainHand instanceof net.minecraft.item.AxeItem)) {
                lockedTarget = null;
                return;
            }
        }

        double maxRangeSq = range.getValue() * range.getValue();
        double fovLimit = fov.getValue() / 2.0;

        if (aimLock.isEnabled() && mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
            Entity hitEnt = ((net.minecraft.util.hit.EntityHitResult) mc.crosshairTarget).getEntity();
            if (mc.options.attackKey.isPressed() && isValidTarget(hitEnt)) {
                lockedTarget = hitEnt;
            }
        }

        if (lockedTarget != null) {
            if (!isValidTarget(lockedTarget) || lockedTarget.isRemoved()) {
                lockedTarget = null;
            }
        }

        if (aimLock.isEnabled()) {
            if (lockedTarget != null) {
                double distSq = mc.player.squaredDistanceTo(lockedTarget);
                double angle = getAngleToLookVec(getAimPoint(lockedTarget));
                if (distSq <= maxRangeSq && angle <= fovLimit && mc.player.canSee(lockedTarget)) {
                    currentTarget = lockedTarget;
                }
            }
        } else {
            double closestAngle = Double.MAX_VALUE;
            for (Entity e : mc.world.getEntities()) {
                if (e == mc.player || !isValidTarget(e)) continue;

                double distSq = mc.player.squaredDistanceTo(e);
                if (distSq > maxRangeSq) continue;

                Vec3d aimPoint = getAimPoint(e);
                double angle = getAngleToLookVec(aimPoint);

                if (angle <= fovLimit && angle < closestAngle && mc.player.canSee(e)) {
                    closestAngle = angle;
                    currentTarget = e;
                }
            }
        }

        if (currentTarget != null) {
            Vec3d aimPoint = getAimPoint(currentTarget);

            double noiseLvl = humanizeNoise.getValue();
            if (noiseLvl > 0) {
                long timeNow = System.currentTimeMillis();
                if (timeNow - lastNoiseUpdate > 50) {
                    noiseX = (Math.random() - 0.5) * noiseLvl * 0.2;
                    noiseY = (Math.random() - 0.5) * noiseLvl * 0.2;
                    noiseZ = (Math.random() - 0.5) * noiseLvl * 0.2;
                    lastNoiseUpdate = timeNow;
                }
                aimPoint = aimPoint.add(noiseX, noiseY, noiseZ);
            }

            float[] needed = getNeededRotations(aimPoint);
            float currentYaw = mc.player.getYaw();
            float currentPitch = mc.player.getPitch();

            float speed = (float) rotationSpeed.getValue() / 10f;
            nextYaw = smoothlyTurn(currentYaw, needed[0], speed);
            nextPitch = smoothlyTurn(currentPitch, needed[1], speed);

            mc.player.setYaw(nextYaw);
            mc.player.setPitch(nextPitch);
        } else {
            lastInterpolatedEntity = null;
        }
    }

    private boolean isValidTarget(Entity e) {
        if (!e.isAlive()) return false;
        if (e.isInvisible() && !invisible.isEnabled()) return false;
        if (!(e instanceof LivingEntity)) return false;

        if (e instanceof PlayerEntity) return targetPlayers.isEnabled();
        if (e instanceof HostileEntity) return targetHostiles.isEnabled();
        if (e instanceof PassiveEntity) return targetPassives.isEnabled();

        return false;
    }

    private Vec3d getAimPoint(Entity e) {
        Box box = e.getBoundingBox();
        String part = targetPart.getMode();
        long timeNow = System.currentTimeMillis();

        if ("Random".equals(part)) {
            if ((timeNow / 2000) % 3 == 0) part = "Head";
            else if ((timeNow / 2000) % 3 == 1) part = "Chest";
            else part = "Legs";
        } else if ("Dynamic".equals(part)) {
            double switchDelayMs = 2500.0 - (dynamicSpeed.getValue() * 100.0);
            if (timeNow - lastDynamicSwitch > switchDelayMs + Math.random() * 500) {
                double rand = Math.random();
                if (rand < 0.70) currentDynamicPart = "Chest";
                else if (rand < 0.90) currentDynamicPart = "Head";
                else currentDynamicPart = "Legs";
                lastDynamicSwitch = timeNow;
            }
            part = currentDynamicPart;
        }

        double x = box.minX + (box.maxX - box.minX) / 2.0;
        double z = box.minZ + (box.maxZ - box.minZ) / 2.0;
        double targetY;

        if ("Head".equals(part)) {
            targetY = e.getEyeY() - 0.1;
        } else if ("Legs".equals(part)) {
            targetY = box.minY + 0.2;
        } else {
            targetY = box.minY + (box.maxY - box.minY) / 2.0;
        }

        if (lastInterpolatedEntity != e || interpolatedAimY == -1) {
            interpolatedAimY = targetY;
            lastInterpolatedEntity = e;
        } else {
            double transitionFactor = dynamicSpeed.getValue() / 40.0;
            double diff = targetY - interpolatedAimY;
            interpolatedAimY += diff * transitionFactor;
            if (Math.abs(diff) < 0.02) {
                interpolatedAimY = targetY;
            }
        }

        return new Vec3d(x, interpolatedAimY, z);
    }

    private void onWorldRender(WorldRenderContext context) {
        if (!isEnabled() || !showTargetEsp.isEnabled() || currentTarget == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        MatrixStack matrices = context.matrices();
        Camera camera = context.gameRenderer().getCamera();

        VertexConsumer buffer = context.consumers().getBuffer(net.minecraft.client.render.RenderLayers.lines());

        JColor c = targetColor.getColor();
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        Box box = currentTarget.getBoundingBox().offset(-camera.getCameraPos().x, -camera.getCameraPos().y, -camera.getCameraPos().z).expand(0.05);

        addLine(buffer, matrices.peek(), box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, r, g, b, 255);

        addLine(buffer, matrices.peek(), box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, r, g, b, 255);

        addLine(buffer, matrices.peek(), box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, r, g, b, 255);
        addLine(buffer, matrices.peek(), box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, 255);
    }

    private void addLine(VertexConsumer consumer, MatrixStack.Entry entry, double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a) {
        consumer.vertex(entry, (float)x1, (float)y1, (float)z1).color(r, g, b, a).normal(entry, (float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1));
        consumer.vertex(entry, (float)x2, (float)y2, (float)z2).color(r, g, b, a).normal(entry, (float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1));
    }

    private float[] getNeededRotations(Vec3d vec) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d eyes = mc.player.getEyePos();

        double diffX = vec.x - eyes.x;
        double diffZ = vec.z - eyes.z;
        double yaw = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0;

        double diffY = vec.y - eyes.y;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double pitch = -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[] { MathHelper.wrapDegrees((float) yaw), MathHelper.wrapDegrees((float) pitch) };
    }

    private double getAngleToLookVec(Vec3d vec) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float[] needed = getNeededRotations(vec);
        float currentYaw = MathHelper.wrapDegrees(mc.player.getYaw());
        float currentPitch = MathHelper.wrapDegrees(mc.player.getPitch());

        float yawDiff = Math.abs(currentYaw - needed[0]);
        if (yawDiff > 180) yawDiff = 360 - yawDiff;

        float pitchDiff = Math.abs(currentPitch - needed[1]);

        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }

    private float smoothlyTurn(float current, float target, float speed) {
        float diff = MathHelper.wrapDegrees(target - current);
        if (Math.abs(diff) < speed) {
            return current + diff;
        }
        return current + (diff > 0 ? speed : -speed);
    }
}
