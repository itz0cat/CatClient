package me.itz0cat.catclient.ui;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.api.util.KeyUtils;
import me.itz0cat.catclient.hud.HudRenderer;
import me.itz0cat.catclient.mod.Category;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.Setting;
import me.itz0cat.catclient.mod.setting.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CatClientScreen extends Screen {
    private final Screen parent;
    private Category currentCategory = Category.ALL;
    private Mod selectedModSettings = null;

    private TextFieldWidget searchField;
    private double scrollOffset = 0;
    private double maxScroll = 0;

    public CatClientScreen() {
        this(null, null);
    }

    public CatClientScreen(Screen parent) {
        this(parent, null);
    }

    public CatClientScreen(Screen parent, Mod initialModSettings) {
        super(Text.literal("CatClient"));
        this.parent = parent;
        this.selectedModSettings = initialModSettings;
    }

    @Override
    protected void init() {
        this.clearChildren();

        // Top bar buttons
        int topY = 8;
        int editHudBtnW = 110;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Edit HUD Layout"),
                btn -> MinecraftClient.getInstance().setScreen(new CatHudEditScreen(this))
        ).dimensions(width - editHudBtnW - 60, topY, editHudBtnW, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                btn -> close()
        ).dimensions(width - 50, topY, 42, 20).build());

        // Search input
        int searchW = 140;
        int searchX = 110;
        searchField = new TextFieldWidget(textRenderer, searchX, topY, searchW, 20, Text.literal("Search..."));
        searchField.setPlaceholder(Text.literal("Search mods..."));
        searchField.setChangedListener(s -> scrollOffset = 0);
        this.addDrawableChild(searchField);

        // Sidebar category buttons
        int sideX = 10;
        int sideY = 36;
        int sideW = 90;
        int btnH = 22;
        int gap = 4;

        Category[] categories = { Category.ALL, Category.COMBAT, Category.MOVEMENT, Category.RENDER, Category.HUD, Category.SETTINGS };
        for (int i = 0; i < categories.length; i++) {
            Category cat = categories[i];
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(cat.getName()),
                    btn -> {
                        currentCategory = cat;
                        selectedModSettings = null;
                        scrollOffset = 0;
                    }
            ).dimensions(sideX, sideY + (i * (btnH + gap)), sideW, btnH).build());
        }

        // Back button when inside module settings
        if (selectedModSettings != null) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("<- Back to Mods"),
                    btn -> {
                        selectedModSettings = null;
                        init();
                    }
            ).dimensions(110, 36, 110, 20).build());
        }
    }

    private List<Mod> getFilteredMods() {
        List<Mod> list = new ArrayList<>();
        String query = searchField != null ? searchField.getText().trim().toLowerCase(Locale.ROOT) : "";

        for (Mod mod : CatClient.modManager().getMods()) {
            if (currentCategory != Category.ALL && mod.category != currentCategory) {
                continue;
            }
            if (!query.isEmpty() && !mod.getName().toLowerCase(Locale.ROOT).contains(query)) {
                continue;
            }
            list.add(mod);
        }
        return list;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Darkened background
        context.fill(0, 0, width, height, 0xCC080C16);

        // Header bar
        context.fill(0, 0, width, 32, 0xDD0A0F1F);
        HudRenderer.drawBorder(context, 0, 0, width, 32, 0xFF00D2FF);

        // Header Title
        context.drawText(textRenderer, "CatClient", 12, 11, 0xFF00D2FF, true);

        // Sidebar background
        int sideW = 100;
        context.fill(0, 32, sideW + 6, height, 0xEE0A0F1D);
        context.fill(sideW + 6, 32, sideW + 7, height, 0x4400D2FF);

        // Highlight active category
        Category[] categories = { Category.ALL, Category.COMBAT, Category.MOVEMENT, Category.RENDER, Category.HUD, Category.SETTINGS };
        int sideY = 36;
        for (int i = 0; i < categories.length; i++) {
            if (categories[i] == currentCategory && selectedModSettings == null) {
                HudRenderer.drawBorder(context, 9, sideY + (i * 26) - 1, 92, 24, 0xFF00D2FF);
            }
        }

        // Content Area Bounds
        int contentX = sideW + 14;
        int contentY = selectedModSettings != null ? 62 : 36;
        int contentW = width - contentX - 12;
        int contentH = height - contentY - 8;

        context.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);

        if (selectedModSettings != null) {
            renderModSettings(context, contentX, contentY, contentW, contentH, mouseX, mouseY);
        } else {
            renderModList(context, contentX, contentY, contentW, contentH, mouseX, mouseY);
        }

        context.disableScissor();

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderModList(DrawContext context, int contentX, int contentY, int contentW, int contentH, int mouseX, int mouseY) {
        List<Mod> mods = getFilteredMods();
        int cardH = 34;
        int gap = 6;
        int totalHeight = mods.size() * (cardH + gap);
        maxScroll = Math.max(0, totalHeight - contentH);
        scrollOffset = MathHelper.clamp(scrollOffset, 0, maxScroll);

        int curY = contentY - (int) scrollOffset;

        for (Mod mod : mods) {
            if (curY + cardH >= contentY && curY <= contentY + contentH) {
                boolean hovered = mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= curY && mouseY <= curY + cardH;

                // Card background
                int bgCol = hovered ? 0xFF141C30 : 0xFF0E1524;
                context.fill(contentX, curY, contentX + contentW, curY + cardH, bgCol);
                HudRenderer.drawBorder(context, contentX, curY, contentW, cardH, mod.isEnabled() ? 0xFF00D2FF : 0xFF2A354A);

                // Mod Name & description
                context.drawText(textRenderer, mod.getName(), contentX + 8, curY + 6, mod.isEnabled() ? 0xFF00D2FF : 0xFFEEEEEE, false);
                String desc = mod.getDescription() != null ? mod.getDescription() : "";
                context.drawText(textRenderer, desc, contentX + 8, curY + 19, 0xFF888888, false);

                // Toggle switch
                int toggleW = 44;
                int toggleH = 18;
                int toggleX = contentX + contentW - toggleW - 8;
                int toggleY = curY + (cardH - toggleH) / 2;

                int switchBg = mod.isEnabled() ? 0xFF00D2FF : 0xFF253045;
                context.fill(toggleX, toggleY, toggleX + toggleW, toggleY + toggleH, switchBg);
                String switchLabel = mod.isEnabled() ? "ON" : "OFF";
                int labelW = textRenderer.getWidth(switchLabel);
                context.drawText(textRenderer, switchLabel, toggleX + (toggleW - labelW) / 2, toggleY + 5, mod.isEnabled() ? 0xFF001020 : 0xFFAAAAAA, false);

                // Settings gear button if mod has settings
                if (!mod.settings.isEmpty()) {
                    int gearW = 22;
                    int gearX = toggleX - gearW - 6;
                    int gearY = toggleY;
                    boolean gearHover = mouseX >= gearX && mouseX <= gearX + gearW && mouseY >= gearY && mouseY <= gearY + toggleH;
                    context.fill(gearX, gearY, gearX + gearW, gearY + toggleH, gearHover ? 0xFF253550 : 0xFF182235);
                    HudRenderer.drawBorder(context, gearX, gearY, gearW, toggleH, 0xFF405070);
                    context.drawText(textRenderer, "*", gearX + 8, gearY + 5, 0xFFCCCCCC, false);
                }
            }
            curY += cardH + gap;
        }
    }

    private void renderModSettings(DrawContext context, int contentX, int contentY, int contentW, int contentH, int mouseX, int mouseY) {
        int cardH = 28;
        int gap = 5;
        int totalHeight = selectedModSettings.settings.size() * (cardH + gap);
        maxScroll = Math.max(0, totalHeight - contentH);
        scrollOffset = MathHelper.clamp(scrollOffset, 0, maxScroll);

        int curY = contentY - (int) scrollOffset;

        for (Setting setting : selectedModSettings.settings) {
            if (curY + cardH >= contentY && curY <= contentY + contentH) {
                // Setting row background
                context.fill(contentX, curY, contentX + contentW, curY + cardH, 0xFF0F1728);
                HudRenderer.drawBorder(context, contentX, curY, contentW, cardH, 0xFF202C40);

                // Setting name
                context.drawText(textRenderer, setting.getName(), contentX + 8, curY + 9, 0xFFEEEEEE, false);

                int widgetX = contentX + contentW - 130;
                int widgetY = curY + 4;

                if (setting instanceof BooleanSetting boolSet) {
                    int switchW = 40;
                    int switchH = 18;
                    int sX = contentX + contentW - switchW - 8;
                    context.fill(sX, widgetY, sX + switchW, widgetY + switchH, boolSet.isEnabled() ? 0xFF00D2FF : 0xFF253045);
                    String lbl = boolSet.isEnabled() ? "ON" : "OFF";
                    int lw = textRenderer.getWidth(lbl);
                    context.drawText(textRenderer, lbl, sX + (switchW - lw) / 2, widgetY + 5, boolSet.isEnabled() ? 0xFF001020 : 0xFFAAAAAA, false);
                } else if (setting instanceof ModeSetting modeSet) {
                    int modeW = 100;
                    int modeH = 18;
                    int mX = contentX + contentW - modeW - 8;
                    context.fill(mX, widgetY, mX + modeW, widgetY + modeH, 0xFF182235);
                    HudRenderer.drawBorder(context, mX, widgetY, modeW, modeH, 0xFF00D2FF);
                    String curMode = modeSet.getMode();
                    int mw = textRenderer.getWidth(curMode);
                    context.drawText(textRenderer, curMode, mX + Math.max(2, (modeW - mw) / 2), widgetY + 5, 0xFF00D2FF, false);
                } else if (setting instanceof NumberSetting numSet) {
                    int btnSize = 16;
                    int minusX = contentX + contentW - 100;
                    int plusX = contentX + contentW - btnSize - 8;
                    int valX = minusX + btnSize + 4;
                    int valW = plusX - valX - 4;

                    context.fill(minusX, widgetY + 1, minusX + btnSize, widgetY + 1 + btnSize, 0xFF1C283E);
                    context.drawText(textRenderer, "-", minusX + 5, widgetY + 4, 0xFF00D2FF, false);

                    String valStr = numSet.decimal ? String.format(Locale.ROOT, "%.1f", numSet.getValue()) : String.valueOf((int) numSet.getValue());
                    int vw = textRenderer.getWidth(valStr);
                    context.drawText(textRenderer, valStr, valX + Math.max(0, (valW - vw) / 2), widgetY + 4, 0xFFEEEEEE, false);

                    context.fill(plusX, widgetY + 1, plusX + btnSize, widgetY + 1 + btnSize, 0xFF1C283E);
                    context.drawText(textRenderer, "+", plusX + 5, widgetY + 4, 0xFF00D2FF, false);
                } else if (setting instanceof ColorSetting colSet) {
                    int colorW = 32;
                    int colorH = 18;
                    int cX = contentX + contentW - colorW - 8;
                    context.fill(cX, widgetY, cX + colorW, widgetY + colorH, colSet.getColor().getRGB());
                    HudRenderer.drawBorder(context, cX, widgetY, colorW, colorH, 0xFFFFFFFF);
                } else if (setting instanceof KeybindSetting keySet) {
                    int keyW = 75;
                    int keyH = 18;
                    int kX = contentX + contentW - keyW - 8;
                    context.fill(kX, widgetY, kX + keyW, widgetY + keyH, 0xFF182235);
                    HudRenderer.drawBorder(context, kX, widgetY, keyW, keyH, keySet.isListening() ? 0xFFFF5555 : 0xFF00D2FF);
                    String keyName = keySet.isListening() ? "Press..." : KeyUtils.getKeyName(keySet.getKeyCode());
                    int kw = textRenderer.getWidth(keyName);
                    context.drawText(textRenderer, keyName, kX + Math.max(2, (keyW - kw) / 2), widgetY + 5, keySet.isListening() ? 0xFFFF5555 : 0xFFEEEEEE, false);
                } else if (setting instanceof ButtonSetting btnSet) {
                    int actionW = 60;
                    int actionH = 18;
                    int bX = contentX + contentW - actionW - 8;
                    context.fill(bX, widgetY, bX + actionW, widgetY + actionH, 0xFF182235);
                    HudRenderer.drawBorder(context, bX, widgetY, actionW, actionH, 0xFF00D2FF);
                    int bw = textRenderer.getWidth("Click");
                    context.drawText(textRenderer, "Click", bX + (actionW - bw) / 2, widgetY + 5, 0xFF00D2FF, false);
                }
            }
            curY += cardH + gap;
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) {
            return true;
        }

        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        int sideW = 100;
        int contentX = sideW + 14;
        int contentY = selectedModSettings != null ? 62 : 36;
        int contentW = width - contentX - 12;
        int contentH = height - contentY - 8;

        if (mouseX < contentX || mouseX > contentX + contentW || mouseY < contentY || mouseY > contentY + contentH) {
            return false;
        }

        if (selectedModSettings != null) {
            // Click inside module settings
            int cardH = 28;
            int gap = 5;
            int curY = contentY - (int) scrollOffset;

            for (Setting setting : selectedModSettings.settings) {
                if (mouseY >= curY && mouseY <= curY + cardH) {
                    int widgetY = curY + 4;
                    if (setting instanceof BooleanSetting boolSet) {
                        int switchW = 40;
                        int sX = contentX + contentW - switchW - 8;
                        if (mouseX >= sX && mouseX <= sX + switchW) {
                            boolSet.toggle();
                            CatClient.configManager().saveConfig();
                            return true;
                        }
                    } else if (setting instanceof ModeSetting modeSet) {
                        int modeW = 100;
                        int mX = contentX + contentW - modeW - 8;
                        if (mouseX >= mX && mouseX <= mX + modeW) {
                            modeSet.cycle();
                            CatClient.configManager().saveConfig();
                            return true;
                        }
                    } else if (setting instanceof NumberSetting numSet) {
                        int btnSize = 16;
                        int minusX = contentX + contentW - 100;
                        int plusX = contentX + contentW - btnSize - 8;

                        if (mouseX >= minusX && mouseX <= minusX + btnSize) {
                            numSet.increment(false);
                            CatClient.configManager().saveConfig();
                            return true;
                        } else if (mouseX >= plusX && mouseX <= plusX + btnSize) {
                            numSet.increment(true);
                            CatClient.configManager().saveConfig();
                            return true;
                        }
                    } else if (setting instanceof ColorSetting colSet) {
                        int colorW = 32;
                        int cX = contentX + contentW - colorW - 8;
                        if (mouseX >= cX && mouseX <= cX + colorW) {
                            colSet.setRainbow(!colSet.isRainbow());
                            CatClient.configManager().saveConfig();
                            return true;
                        }
                    } else if (setting instanceof KeybindSetting keySet) {
                        int keyW = 75;
                        int kX = contentX + contentW - keyW - 8;
                        if (mouseX >= kX && mouseX <= kX + keyW) {
                            keySet.setListening(!keySet.isListening());
                            return true;
                        }
                    } else if (setting instanceof ButtonSetting btnSet) {
                        int actionW = 60;
                        int bX = contentX + contentW - actionW - 8;
                        if (mouseX >= bX && mouseX <= bX + actionW) {
                            btnSet.click();
                            return true;
                        }
                    }
                }
                curY += cardH + gap;
            }
        } else {
            // Click inside mod list
            List<Mod> mods = getFilteredMods();
            int cardH = 34;
            int gap = 6;
            int curY = contentY - (int) scrollOffset;

            for (Mod mod : mods) {
                if (mouseY >= curY && mouseY <= curY + cardH) {
                    int toggleW = 44;
                    int toggleH = 18;
                    int toggleX = contentX + contentW - toggleW - 8;
                    int toggleY = curY + (cardH - toggleH) / 2;

                    int gearW = 22;
                    int gearX = toggleX - gearW - 6;

                    if (mouseX >= gearX && mouseX <= gearX + gearW && !mod.settings.isEmpty()) {
                        selectedModSettings = mod;
                        scrollOffset = 0;
                        init();
                        return true;
                    } else if (mouseX >= toggleX && mouseX <= toggleX + toggleW) {
                        mod.toggle();
                        CatClient.configManager().saveConfig();
                        return true;
                    } else {
                        // Clicking card toggles mod
                        mod.toggle();
                        CatClient.configManager().saveConfig();
                        return true;
                    }
                }
                curY += cardH + gap;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = MathHelper.clamp(scrollOffset - (verticalAmount * 20), 0, maxScroll);
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (selectedModSettings != null) {
            for (Setting setting : selectedModSettings.settings) {
                if (setting instanceof KeybindSetting keySet && keySet.isListening()) {
                    if (keyInput.key() == GLFW.GLFW_KEY_ESCAPE) {
                        keySet.setKeyCode(GLFW.GLFW_KEY_UNKNOWN);
                    } else {
                        keySet.setKeyCode(keyInput.key());
                    }
                    keySet.setListening(false);
                    CatClient.configManager().saveConfig();
                    return true;
                }
            }
            if (keyInput.key() == GLFW.GLFW_KEY_ESCAPE) {
                selectedModSettings = null;
                init();
                return true;
            }
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public void close() {
        CatClient.configManager().saveConfig();
        MinecraftClient.getInstance().setScreen(parent);
    }
}
