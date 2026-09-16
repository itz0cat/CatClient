package me.itz0cat.catclient.mod;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.KeyPressEvent;
import me.itz0cat.catclient.api.event.orbit.EventHandler;
import me.itz0cat.catclient.api.helpers.KeystrokeHelper;
import me.itz0cat.catclient.mod.mods.*;
import me.itz0cat.catclient.ui.CatClientScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ModManager {

	public ArrayList<Mod> mods;

	public ModManager() {
		mods = new ArrayList<>();
		addMods();
	}

	public boolean isModEnabled(Class<? extends Mod> modClass) {
		Mod m = getMod(modClass);
		return m != null ? m.isEnabled() : false;
	}

	public boolean isModEnabled(String name) {
		Mod m = mods.stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		return m != null ? m.isEnabled() : false;
	}

	@Nullable
	public <T extends Mod> T getMod(Class<T> modClass) {
		for (Mod mod : mods) {
			if (modClass.isAssignableFrom(mod.getClass())) {
				return (T) mod;
			}
		}
		return null;
	}

	@Nullable
	public Mod getMod(String name) {
		for (Mod m : mods) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}

		return null;
	}

	public ArrayList<Mod> getMods() {
		return new ArrayList<>(mods);
	}

	public void addMod(Mod mod) {
		mods.add(mod);
	}

	public void addMods() {
		addMod(new GeneralSettings());

		addMod(new ScoreboardMod());
		addMod(new FPSMod());
		addMod(new CPSMod());
		addMod(new PingMod());
		addMod(new KeystrokesMod());
		addMod(new ReachDisplayMod());
		addMod(new CoordsMod());
		addMod(new ServerIPMod());
		addMod(new ToggleSprintMod());
		addMod(new ToggleSneakMod());
		addMod(new HurtCamMod());
		addMod(new PotionMod());
		addMod(new ArrowCountMod());
		addMod(new PotCountMod());
		addMod(new TotemCountMod());
		addMod(new ArmorMod());
		addMod(new CrosshairMod());
		addMod(new NametagsMod());
		addMod(new ZoomMod());
		addMod(new HitColorMod());
		addMod(new HitboxMod());
		addMod(new FreelookMod());
		addMod(new TimeMod());
		addMod(new TimeChangerMod());

		// Glued CodeX modules
		addMod(new AimAssistMod());
		addMod(new BlockOverlayMod());
		addMod(new FullbrightMod());
	}

	@EventHandler
	private void onKeyPress(KeyPressEvent event) {
		for (KeystrokeHelper k : KeystrokeHelper.list) {
			if (event.key == k.getKey() && event.action != GLFW.GLFW_RELEASE) {
				k.setPressTime(System.currentTimeMillis());
				k.setPressed(true);
				return;
			}
			if (event.key == k.getKey()) {
				k.setPressTime(System.currentTimeMillis());
				k.setPressed(false);
				return;
			}
		}
		if (event.action == GLFW.GLFW_RELEASE)
			return;

		if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen)
			return;

		if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_F3))
			return;

		GeneralSettings gs = CatClient.modManager().getMod(GeneralSettings.class);
		if (gs != null && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), gs.openMenu.getKeyCode())) {
			if (MinecraftClient.getInstance().currentScreen == null) {
				MinecraftClient.getInstance().setScreen(new CatClientScreen());
			}
		}
	}
}
