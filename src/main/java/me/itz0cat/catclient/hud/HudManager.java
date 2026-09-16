package me.itz0cat.catclient.hud;

import me.itz0cat.catclient.api.util.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HudManager {
    private static final HudManager INSTANCE = new HudManager();
    private final List<HudElement> elements = new ArrayList<>();
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean snapping = true;

    private HudManager() {
        HudRenderCallback.EVENT.register(this::onRender);
    }

    public static HudManager getInstance() {
        return INSTANCE;
    }

    public void register(HudElement element) {
        if (!elements.contains(element)) {
            elements.add(element);
        }
    }

    public List<HudElement> getElements() {
        return elements;
    }

    public boolean isSnapping() {
        return snapping;
    }

    public void setSnapping(boolean snapping) {
        this.snapping = snapping;
    }

    private void onRender(DrawContext context, RenderTickCounter tickCounter) {
        if (!RenderUtils.isRenderable()) return;

        for (HudElement element : elements) {
            if (element.isEnabled()) {
                context.getMatrices().push();
                HudPosition pos = element.getPosition();
                float scale = pos.scale > 0 ? pos.scale : 1.0f;
                context.getMatrices().translate(pos.x, pos.y, 0);
                context.getMatrices().scale(scale, scale, 1.0f);
                element.renderHud(context, tickCounter);
                context.getMatrices().pop();
            }
        }
    }

    public void renderPlaceholders(DrawContext context) {
        for (HudElement element : elements) {
            if (element.isEnabled()) {
                context.getMatrices().push();
                HudPosition pos = element.getPosition();
                float scale = pos.scale > 0 ? pos.scale : 1.0f;
                context.getMatrices().translate(pos.x, pos.y, 0);
                context.getMatrices().scale(scale, scale, 1.0f);
                element.renderPlaceholder(context);
                context.getMatrices().pop();
            }
        }
    }

    public Optional<HudElement> getElementAt(int mouseX, int mouseY) {
        for (HudElement element : elements) {
            if (!element.isEnabled()) continue;
            HudPosition pos = element.getPosition();
            float scale = pos.scale > 0 ? pos.scale : 1.0f;
            float w = element.getWidth() * scale;
            float h = element.getHeight() * scale;
            if (mouseX >= pos.x && mouseX <= pos.x + w && mouseY >= pos.y && mouseY <= pos.y + h) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }
}
