package com.thaddev.coolideas.mechanics.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerSouls {
    private int souls = 0;

    private int customSoulCapacity = -1;
    private boolean useCustomSoulCapacity = false;

    public int getSouls() {
        return souls;
    }

    public void addSouls(int souls) {
        this.souls += souls;
    }

    public void setSouls(int souls) {
        this.souls = souls;
    }

    public int getSoulCapacity(Player playerOptional) {
        if (!useCustomSoulCapacity) {
            return getScaledSouls(playerOptional.experienceLevel);
        } else {
            return customSoulCapacity;
        }
    }

    public int getSavedSoulCapacity(){
        return customSoulCapacity;
    }

    public void setCustomSoulCapacity(int customSoulCapacity) {
        if (useCustomSoulCapacity) {
            this.customSoulCapacity = customSoulCapacity;
        }
    }

    public boolean isUseCustomSoulCapacity() {
        return useCustomSoulCapacity;
    }

    public void setUseCustomSoulCapacity(boolean useCustomSoulCapacity) {
        this.useCustomSoulCapacity = useCustomSoulCapacity;
    }

    private int getScaledSouls(int level) {
        return ((1 + level) * level) * 10;
    }

    public void copyFrom(PlayerSouls source) {
        this.souls = source.souls;
        this.useCustomSoulCapacity = source.useCustomSoulCapacity;
        this.customSoulCapacity = source.customSoulCapacity;
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putInt("Souls", souls);

        nbt.putBoolean("UseCustomSoulCapacity", useCustomSoulCapacity);
        nbt.putInt("CustomSoulCapacity", customSoulCapacity);
    }

    public void loadNBTData(CompoundTag nbt){
        souls = nbt.getInt("Souls");

        useCustomSoulCapacity = nbt.getBoolean("UseCustomSoulCapacity");
        customSoulCapacity = nbt.getInt("CustomSoulCapacity");
    }
}
