package me.itz0cat.catclient.api.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.backend.CatBackendClient;
import me.itz0cat.catclient.mod.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class IndicatorHelper {
    public static Identifier badgeIcon = Identifier.of("catclient", "badge.png");

    public static void addBadge(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (!CatClient.modManager().getMod(GeneralSettings.class).showClientBadges.isEnabled()) return;
        if (entity instanceof PlayerEntity && !entity.isSneaky()) {
            if (isUsingClient(entity.getUuid())) {
                RenderSystem.enableDepthTest();
                RenderSystem.setShaderTexture(0, badgeIcon);

                assert MinecraftClient.getInstance().player != null;
                int x = -(MinecraftClient.getInstance().textRenderer
                        .getWidth(
                                (Team.decorateName(entity.getScoreboardTeam(), entity.getName())
                                        .getString()))
                        / 2
                        + (10));
            }
        }
    }

    public static boolean isUsingClient(UUID u) {
        if (!CatClient.modManager().getMod(GeneralSettings.class).showClientBadges.isEnabled()) return false;
        return CatBackendClient.isClientUser(u);
    }

    public static void getUsers() {
        if (!CatClient.modManager().getMod(GeneralSettings.class).showClientBadges.isEnabled()) return;
        CatBackendClient.syncOnlinePlayers();
    }

    public static void enableClient() {
        CatBackendClient.onPlayerJoin();
    }

    public static void disableClient() {
        CatBackendClient.onPlayerDisconnect();
    }
}
