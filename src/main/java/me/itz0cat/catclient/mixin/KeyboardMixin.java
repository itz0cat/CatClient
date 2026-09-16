package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.KeyPressEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        CatClient.EVENTBUS.post(KeyPressEvent.get(key, scancode, action, window));
    }
}
