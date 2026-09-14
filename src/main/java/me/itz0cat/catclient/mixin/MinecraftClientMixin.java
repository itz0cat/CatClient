package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.api.event.events.AttackEvent;
import me.itz0cat.catclient.api.event.events.BlockBreakEvent;
import me.itz0cat.catclient.api.event.events.ItemUseEvent;
import me.itz0cat.catclient.api.event.events.TickEvent;
import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.helpers.FPSHelper;
import me.itz0cat.catclient.mod.GeneralSettings;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.itz0cat.catclient.CatClient.mc;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private static int currentFps;

    //@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    //private void getWindowTitle(CallbackInfoReturnable<String> cir) {
    //    cir.setReturnValue("CatClient " + SharedConstants.getGameVersion().getName());
    //}

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo ci) {
        CatClient.EVENTBUS.post(TickEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onPostTick(CallbackInfo ci) {
        CatClient.EVENTBUS.post(TickEvent.Post.get());
        FPSHelper.setFPS(currentFps);
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onPreAttack(CallbackInfoReturnable<Boolean> cir) {
        if (CatClient.EVENTBUS.post(AttackEvent.Pre.get()).isCancelled()) cir.setReturnValue(false);
    }

    @Inject(method = "doAttack", at = @At("TAIL"), cancellable = true)
    private void onPostAttack(CallbackInfoReturnable<Boolean> cir) {
        if (CatClient.EVENTBUS.post(AttackEvent.Post.get()).isCancelled()) cir.setReturnValue(false);
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onPreItemUse(CallbackInfo ci) {
        if (CatClient.EVENTBUS.post(ItemUseEvent.Pre.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "doItemUse", at = @At("TAIL"), cancellable = true)
    private void onPostItemUse(CallbackInfo ci) {
        if (CatClient.EVENTBUS.post(ItemUseEvent.Post.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onPreBlockBreak(boolean breaking, CallbackInfo ci) {
        if (CatClient.EVENTBUS.post(BlockBreakEvent.Pre.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "handleBlockBreaking", at = @At("TAIL"), cancellable = true)
    private void onPostBlockBreak(boolean breaking, CallbackInfo ci) {
        if (CatClient.EVENTBUS.post(BlockBreakEvent.Post.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void onStart(CallbackInfo ci) {
        new Client();
        CatClient.INSTANCE.init();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        CatClient.configManager().saveConfig();
    }
}
