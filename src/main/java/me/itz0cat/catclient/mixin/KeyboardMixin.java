package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.KeyPressEvent;
import me.itz0cat.catclient.menu.*;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.itz0cat.catclient.gui.ImguiLoader.imGuiGlfw;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if(!FirstMenu.getInstance().isVisible) imGuiGlfw.keyCallback(window, key, scancode, action, 0);
        CatClient.EVENTBUS.post(KeyPressEvent.get(key, scancode, action, window));
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS && (FirstMenu.getInstance().isVisible || ModMenu.getInstance().isVisible || ModSettings.getInstance().isVisible || ProfilesMenu.getInstance().isVisible || CosmeticsMenu.getInstance().isVisible)) {
            SideMenu.getInstance().selectedWindow = "Mods";
            ModSettings.getInstance().isVisible = false;
            ModMenu.getInstance().isVisible = true;
            FirstMenu.toggle(false);
            ModMenu.toggle(false);
            ModSettings.toggle(false);
            ProfilesMenu.toggle(false);
            CosmeticsMenu.toggle(false);
            SideMenu.toggle(false);
            SideMenu.toggle(false);
            ci.cancel();
        }
        if(action != GLFW.GLFW_RELEASE & (FirstMenu.getInstance().isVisible || ModMenu.getInstance().isVisible || ModSettings.getInstance().isVisible || SideMenu.getInstance().isVisible)) ci.cancel();
    }
}
