package com.thaddev.coolideas.mixins;

import com.thaddev.coolideas.CoolIdeasMod;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void handleSystemChat(CallbackInfo ci) {
        CoolIdeasMod.instance.isMismatching = false;
    }
}