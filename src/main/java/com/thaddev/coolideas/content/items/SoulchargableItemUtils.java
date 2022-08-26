package com.thaddev.coolideas.content.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SoulchargableItemUtils {
    public static int MAX_WITHER_SKELETON_PROGRESS = 30;
    public static int MAX_SOUL_SAND_PROGRESS = 40;

    public static int getWitherSkeletonProgress(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt("WitherSkeletonProgress");
    }

    public static void incrementWitherSkeletonProgress(ItemStack stack, int toIncrement){
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("WitherSkeletonProgress", getWitherSkeletonProgress(stack) + toIncrement);
    }

    public static int getSoulSandProgress(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt("SoulSandProgress");
    }

    public static void incrementSoulSandProgress(ItemStack stack, int toIncrement){
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("SoulSandProgress", getSoulSandProgress(stack) + toIncrement);
    }

    public static boolean isReadyToCharge(ItemStack stack){
        return getSoulSandProgress(stack) >= MAX_SOUL_SAND_PROGRESS && getWitherSkeletonProgress(stack) >= MAX_WITHER_SKELETON_PROGRESS;
    }

    public static boolean isCharged(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getBoolean("IsCharged");
    }

    public static boolean tryCharge(ItemStack stack){
        if (isReadyToCharge(stack)){
            CompoundTag tag = stack.getOrCreateTag();
            tag.putBoolean("IsCharged", true);
            return true;
        }
        return false;
    }
}
