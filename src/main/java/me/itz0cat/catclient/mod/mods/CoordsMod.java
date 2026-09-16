package me.itz0cat.catclient.mod.mods;

import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.hud.HudElement;
import me.itz0cat.catclient.hud.HudManager;
import me.itz0cat.catclient.hud.HudPosition;
import me.itz0cat.catclient.hud.HudRenderer;
import me.itz0cat.catclient.mod.Category;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.settings.BooleanSetting;
import me.itz0cat.catclient.mod.setting.settings.ColorSetting;
import me.itz0cat.catclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoordsMod extends Mod implements HudElement {
    public final ColorSetting background = new ColorSetting("Background Color", this, new JColor(0f, 0f, 0f, 0.75f), true);
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);
    public final NumberSetting width = new NumberSetting("Width", this, 120, 80, 250, 1);
    public final NumberSetting height = new NumberSetting("Height", this, 45, 30, 100, 1);
    public final BooleanSetting backgroundEnabled = new BooleanSetting("Background", this, true);
    public final BooleanSetting biome = new BooleanSetting("Biome", this, true);
    public final BooleanSetting direction = new BooleanSetting("Direction", this, true);

    public CoordsMod() {
        super("Coords", "Shows your coordinates.", "\uF124");
        this.category = Category.HUD;
        HudManager.getInstance().register(this);
    }

    @Override
    public int getWidth() {
        return (int) width.getValue();
    }

    @Override
    public int getHeight() {
        return (int) height.getValue();
    }

    @Override
    public HudPosition getPosition() {
        return this.position;
    }

    @Override
    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        position.scale = scale.getFValue();
        List<String> lines = new ArrayList<>();

        if (mc.player != null && mc.world != null) {
            String dirStr = "";
            if (direction.isEnabled()) {
                Direction facing = mc.player.getHorizontalFacing();
                String nameStr = switch (facing) {
                    case EAST -> "E";
                    case WEST -> "W";
                    case SOUTH -> "S";
                    case NORTH -> "N";
                    default -> "";
                };
                String axisStr = facing.getAxis() == Direction.Axis.X ?
                        (facing.getDirection() == Direction.AxisDirection.POSITIVE ? "+X" : "-X") :
                        (facing.getDirection() == Direction.AxisDirection.POSITIVE ? "+Z" : "-Z");
                dirStr = " (" + nameStr + " " + axisStr + ")";
            }

            lines.add(String.format(Locale.ROOT, "X: %.1f%s", mc.player.getX(), dirStr));
            lines.add(String.format(Locale.ROOT, "Y: %.1f", mc.player.getY()));
            lines.add(String.format(Locale.ROOT, "Z: %.1f", mc.player.getZ()));

            if (biome.isEnabled()) {
                var biomeEntry = mc.world.getBiome(mc.player.getBlockPos());
                String bPath = biomeEntry.getKey().map(k -> k.getValue().getPath()).orElse("Unknown");
                lines.add("Biome: " + WordUtils.capitalize(bPath.replace('_', ' ')));
            }
        } else {
            lines.add("X: 0.0");
            lines.add("Y: 0.0");
            lines.add("Z: 0.0");
            if (biome.isEnabled()) lines.add("Biome: Plains");
        }

        HudRenderer.drawMultiLineHudBox(context, getWidth(), getHeight(), background.getColor(), backgroundEnabled.isEnabled(), lines, text.getColor(), textShadow.isEnabled());
    }

    @Override
    public void renderPlaceholder(DrawContext context) {
        HudRenderer.drawPlaceholderBox(context, getName(), getWidth(), getHeight());
    }
}
