package me.itz0cat.catclient.ui;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.hud.HudElement;
import me.itz0cat.catclient.hud.HudManager;
import me.itz0cat.catclient.hud.HudPosition;
import me.itz0cat.catclient.hud.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Optional;

public class CatHudEditScreen extends Screen {
    private final Screen parent;
    private HudElement draggingElement = null;
    private float dragOffsetX = 0;
    private float dragOffsetY = 0;
    private ButtonWidget snappingButton;

    public CatHudEditScreen() {
        this(null);
    }

    public CatHudEditScreen(Screen parent) {
        super(Text.literal("Edit HUD Layout"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int btnWidth = 90;
        int btnHeight = 20;
        int spacing = 6;
        int totalWidth = (btnWidth * 4) + (spacing * 3);
        int startX = (this.width - totalWidth) / 2;
        int topY = 10;

        snappingButton = ButtonWidget.builder(
                Text.literal("Snapping: " + (HudManager.getInstance().isSnapping() ? "ON" : "OFF")),
                btn -> {
                    HudManager.getInstance().setSnapping(!HudManager.getInstance().isSnapping());
                    btn.setMessage(Text.literal("Snapping: " + (HudManager.getInstance().isSnapping() ? "ON" : "OFF")));
                }
        ).dimensions(startX, topY, btnWidth, btnHeight).build();
        this.addDrawableChild(snappingButton);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Client Menu"),
                btn -> MinecraftClient.getInstance().setScreen(new CatClientScreen(this))
        ).dimensions(startX + btnWidth + spacing, topY, btnWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset Layout"),
                btn -> resetPositions()
        ).dimensions(startX + (btnWidth + spacing) * 2, topY, btnWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                btn -> close()
        ).dimensions(startX + (btnWidth + spacing) * 3, topY, btnWidth, btnHeight).build());
    }

    private void resetPositions() {
        float startY = 35;
        for (HudElement elem : HudManager.getInstance().getElements()) {
            elem.getPosition().set(10, startY);
            startY += elem.getHeight() + 4;
            if (startY > height - 40) startY = 35;
        }
        CatClient.configManager().saveConfig();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x44000000);

        // Center guide crosshair lines
        int centerX = width / 2;
        int centerY = height / 2;
        context.fill(centerX, 0, centerX + 1, height, 0x2200D2FF);
        context.fill(0, centerY, width, centerY + 1, 0x2200D2FF);

        // Render all HUD element placeholders
        HudManager.getInstance().renderPlaceholders(context);

        // Render hover outline
        if (draggingElement == null) {
            Optional<HudElement> hovered = HudManager.getInstance().getElementAt(mouseX, mouseY);
            hovered.ifPresent(elem -> {
                HudPosition pos = elem.getPosition();
                float scale = pos.scale > 0 ? pos.scale : 1.0f;
                int w = (int) (elem.getWidth() * scale);
                int h = (int) (elem.getHeight() * scale);
                HudRenderer.drawBorder(context, (int) pos.x - 1, (int) pos.y - 1, w + 2, h + 2, 0xFFFFFFFF);
            });
        } else {
            // Dragging outline
            HudPosition pos = draggingElement.getPosition();
            float scale = pos.scale > 0 ? pos.scale : 1.0f;
            int w = (int) (draggingElement.getWidth() * scale);
            int h = (int) (draggingElement.getHeight() * scale);
            HudRenderer.drawBorder(context, (int) pos.x - 2, (int) pos.y - 2, w + 4, h + 4, 0xFF00D2FF);
        }

        super.render(context, mouseX, mouseY, delta);

        // Hint bar at the bottom
        String hint = "Left Click: Drag | Right Click: Settings | ESC: Save & Exit";
        int hintW = textRenderer.getWidth(hint);
        context.fill((width - hintW) / 2 - 8, height - 22, (width + hintW) / 2 + 8, height - 6, 0x88000000);
        context.drawText(textRenderer, hint, (width - hintW) / 2, height - 17, 0xFFAAAAAA, false);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) {
            return true;
        }

        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        Optional<HudElement> clicked = HudManager.getInstance().getElementAt((int) mouseX, (int) mouseY);
        if (clicked.isPresent()) {
            HudElement elem = clicked.get();
            if (button == 0) { // Left click: drag
                draggingElement = elem;
                dragOffsetX = (float) mouseX - elem.getPosition().x;
                dragOffsetY = (float) mouseY - elem.getPosition().y;
                return true;
            } else if (button == 1) { // Right click: configure
                if (elem instanceof me.itz0cat.catclient.mod.Mod mod) {
                    MinecraftClient.getInstance().setScreen(new CatClientScreen(this, mod));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (draggingElement != null && button == 0) {
            float scale = draggingElement.getPosition().scale > 0 ? draggingElement.getPosition().scale : 1.0f;
            float elemW = draggingElement.getWidth() * scale;
            float elemH = draggingElement.getHeight() * scale;

            float targetX = (float) mouseX - dragOffsetX;
            float targetY = (float) mouseY - dragOffsetY;

            if (HudManager.getInstance().isSnapping()) {
                int snapDist = 4;
                // Screen edge snapping
                if (Math.abs(targetX) < snapDist) targetX = 0;
                if (Math.abs(targetX - (width - elemW)) < snapDist) targetX = width - elemW;
                if (Math.abs(targetY) < snapDist) targetY = 0;
                if (Math.abs(targetY - (height - elemH)) < snapDist) targetY = height - elemH;

                // Center snapping
                if (Math.abs((targetX + elemW / 2) - width / 2f) < snapDist) {
                    targetX = width / 2f - elemW / 2;
                }
                if (Math.abs((targetY + elemH / 2) - height / 2f) < snapDist) {
                    targetY = height / 2f - elemH / 2;
                }

                // Snap to other elements
                for (HudElement other : HudManager.getInstance().getElements()) {
                    if (other == draggingElement || !other.isEnabled()) continue;
                    HudPosition oPos = other.getPosition();
                    float oScale = oPos.scale > 0 ? oPos.scale : 1.0f;
                    float oW = other.getWidth() * oScale;
                    float oH = other.getHeight() * oScale;

                    if (Math.abs(targetX - oPos.x) < snapDist) targetX = oPos.x;
                    if (Math.abs(targetX - (oPos.x + oW)) < snapDist) targetX = oPos.x + oW;
                    if (Math.abs((targetX + elemW) - oPos.x) < snapDist) targetX = oPos.x - elemW;

                    if (Math.abs(targetY - oPos.y) < snapDist) targetY = oPos.y;
                    if (Math.abs(targetY - (oPos.y + oH)) < snapDist) targetY = oPos.y + oH;
                    if (Math.abs((targetY + elemH) - oPos.y) < snapDist) targetY = oPos.y - elemH;
                }
            }

            // Clamp inside screen bounds
            targetX = Math.max(0, Math.min(width - elemW, targetX));
            targetY = Math.max(0, Math.min(height - elemH, targetY));

            draggingElement.getPosition().set(targetX, targetY);
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (draggingElement != null) {
            draggingElement = null;
            CatClient.configManager().saveConfig();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        CatClient.configManager().saveConfig();
        MinecraftClient.getInstance().setScreen(parent);
    }
}
