package me.itz0cat.catclient.ui;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.backend.CatBackendClient;
import me.itz0cat.catclient.hud.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CatConsentScreen extends Screen {
    private final Screen parent;
    private final List<String> messageLines = new ArrayList<>();

    public CatConsentScreen(Screen parent) {
        super(Text.literal("Online Services & Privacy"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        messageLines.clear();
        messageLines.add("CatClient connects to online services for:");
        messageLines.add("  - Active player badge verification near player nametags");
        messageLines.add("  - Server Message of the Day (MOTD)");
        messageLines.add("");
        messageLines.add("Note: Cloud capes and custom cosmetics are placeholders for future work.");
        messageLines.add("No sensitive player credentials or personal data are ever transmitted.");
        messageLines.add("");
        messageLines.add("Would you like to enable online badge verification?");

        int cardW = Math.min(380, width - 40);
        int cardX = (width - cardW) / 2;
        int btnW = (cardW - 30) / 2;
        int btnY = height / 2 + 50;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Accept & Connect"),
                btn -> {
                    CatClient.configManager().privacyConsent = true;
                    CatClient.configManager().privacyPrompted = true;
                    CatClient.configManager().saveConfig();
                    CatBackendClient.init();
                    MinecraftClient.getInstance().setScreen(parent);
                }
        ).dimensions(cardX + 10, btnY, btnW, 24).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Offline Mode"),
                btn -> {
                    CatClient.configManager().privacyConsent = false;
                    CatClient.configManager().privacyPrompted = true;
                    CatClient.configManager().saveConfig();
                    MinecraftClient.getInstance().setScreen(parent);
                }
        ).dimensions(cardX + cardW - btnW - 10, btnY, btnW, 24).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xAA000000);

        int cardW = Math.min(380, width - 40);
        int cardH = 200;
        int cardX = (width - cardW) / 2;
        int cardY = (height - cardH) / 2;

        // Card background & cyan border
        context.fill(cardX, cardY, cardX + cardW, cardY + cardH, 0xF00A0F1D);
        HudRenderer.drawBorder(context, cardX, cardY, cardW, cardH, 0xFF00D2FF);

        // Header
        String titleStr = "CatClient Online Services";
        int titleW = textRenderer.getWidth(titleStr);
        context.drawText(textRenderer, titleStr, cardX + (cardW - titleW) / 2, cardY + 12, 0xFF00D2FF, true);

        // Divider line
        context.fill(cardX + 15, cardY + 28, cardX + cardW - 15, cardY + 29, 0x4400D2FF);

        // Body text
        int textY = cardY + 36;
        for (String line : messageLines) {
            int col = line.startsWith("Note:") ? 0xFFFFAA00 : 0xFFDDDDDD;
            context.drawText(textRenderer, line, cardX + 15, textY, col, false);
            textY += textRenderer.fontHeight + 3;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        CatClient.configManager().privacyPrompted = true;
        CatClient.configManager().saveConfig();
        MinecraftClient.getInstance().setScreen(parent);
    }
}
