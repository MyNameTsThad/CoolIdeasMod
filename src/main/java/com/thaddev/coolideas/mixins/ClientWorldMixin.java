package com.thaddev.coolideas.mixins;

import com.thaddev.coolideas.CoolIdeasMod;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void handleSystemChat(CallbackInfo ci) {
        CoolIdeasMod.instance.isMismatching = false;
    }
}