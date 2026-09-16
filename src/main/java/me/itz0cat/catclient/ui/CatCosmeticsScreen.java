package me.itz0cat.catclient.ui;

import me.itz0cat.catclient.hud.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CatCosmeticsScreen extends Screen {
    private final Screen parent;

    public CatCosmeticsScreen(Screen parent) {
        super(Text.literal("CatClient Cosmetics"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                btn -> MinecraftClient.getInstance().setScreen(parent)
        ).dimensions(width / 2 - 50, height / 2 + 35, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xCC080C16);
        int cardW = 300;
        int cardH = 130;
        int cardX = (width - cardW) / 2;
        int cardY = (height - cardH) / 2;

        context.fill(cardX, cardY, cardX + cardW, cardY + cardH, 0xF00A0F1D);
        HudRenderer.drawBorder(context, cardX, cardY, cardW, cardH, 0xFF00D2FF);

        String title = "Cosmetics System";
        int tw = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, cardX + (cardW - tw) / 2, cardY + 15, 0xFF00D2FF, true);

        String info = "Cloud capes and cosmetics are currently in development.";
        int iw = textRenderer.getWidth(info);
        context.drawText(textRenderer, info, cardX + (cardW - iw) / 2, cardY + 42, 0xFFCCCCCC, false);

        String info2 = "Active player badges are live in multiplayer.";
        int i2w = textRenderer.getWidth(info2);
        context.drawText(textRenderer, info2, cardX + (cardW - i2w) / 2, cardY + 58, 0xFF888888, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
