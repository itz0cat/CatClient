package me.itz0cat.catclient.mixin;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.mod.mods.TimeChangerMod;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @ModifyArg(method = "setTimeOfDay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld$Properties;setTimeOfDay(J)V"))
    public long setTimeOfDay(long time) {
        if (CatClient.modManager().getMod(TimeChangerMod.class).isEnabled())
            return CatClient.modManager().getMod(TimeChangerMod.class).getTimeInt();
        else
            return time;
    }
}