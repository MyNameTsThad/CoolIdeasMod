package com.thaddev.coolideas.mixins;

import com.thaddev.coolideas.content.items.SoulchargableItemUtils;
import com.thaddev.coolideas.mechanics.AbstractItemStackMixin;
import com.thaddev.coolideas.mechanics.capabilities.PlayerSoulsProvider;
import com.thaddev.coolideas.mechanics.inits.TagsInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements AbstractItemStackMixin {
    @Shadow
    public abstract Item getItem();

    private int maxDurability;

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(getMaxDurability());
    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(Level pLevel, Entity pEntity, int pInventorySlot, boolean pIsCurrentItem, CallbackInfo ci) {
        ItemStack thisStack;
        if ((thisStack = (ItemStack) (Object) this).is(TagsInit.SOULCHARGABLE) && pEntity instanceof Player player) {
            if (SoulchargableItemUtils.isCharged(thisStack)) {
                player.getCapability(PlayerSoulsProvider.PLAYER_SOULS).ifPresent(playerSouls -> {
                    setMaxDurability(getItem().getMaxDamage((ItemStack) (Object) this) + (playerSouls.getSouls() / 2));
                });
            }
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    public void constructor1(ItemLike p_41604_, int p_41605_, CompoundTag p_41606_, CallbackInfo ci) {
        setMaxDurability(getItem().getMaxDamage((ItemStack) (Object) this));
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    public void constructor2(CompoundTag pCompoundTag, CallbackInfo ci) {
        setMaxDurability(getItem().getMaxDamage((ItemStack) (Object) this));
    }


    @Override
    public int getMaxDurability() {
        return maxDurability;
    }

    @Override
    public void setMaxDurability(int durability) {
        this.maxDurability = durability;
    }

    //added to fix the bar width and color mismatch with the real durability

    @Inject(method = "getBarWidth", at = @At("RETURN"), cancellable = true)
    public void getBarWidth(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(
            Math.round(13.0F - (float) ((ItemStack) (Object) this).getDamageValue() * 13.0F / getMaxDurability())
        );
    }

    @Inject(method = "getBarColor", at = @At("RETURN"), cancellable = true)
    public void getBarColor(CallbackInfoReturnable<Integer> cir) {
        float f = Math.max(0.0F, (getMaxDurability() - (float) ((ItemStack) (Object) this).getDamageValue()) / getMaxDurability());
        cir.setReturnValue(
            Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F)
        );
    }
}
