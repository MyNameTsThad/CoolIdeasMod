package com.thaddev.coolideas.mechanics.networking.packets;

import com.thaddev.coolideas.mechanics.capabilities.PlayerSoulsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundPlayerSoulsSyncPacket {
    private int souls;

    private int customSoulCapacity;
    private boolean useCustomSoulCapacity;

    public ClientboundPlayerSoulsSyncPacket(int souls, int customSoulCapacity, boolean useCustomSoulCapacity){
        this.souls = souls;
        this.customSoulCapacity = customSoulCapacity;
        this.useCustomSoulCapacity = useCustomSoulCapacity;
    }

    public ClientboundPlayerSoulsSyncPacket(FriendlyByteBuf buf){
        this.souls = buf.readInt();
        this.customSoulCapacity = buf.readInt();
        this.useCustomSoulCapacity = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(this.souls);
        buf.writeInt(this.customSoulCapacity);
        buf.writeBoolean(this.useCustomSoulCapacity);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            //client
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(PlayerSoulsProvider.PLAYER_SOULS).ifPresent(playerSouls -> {
                    playerSouls.setSouls(this.souls);
                    playerSouls.setCustomSoulCapacity(this.customSoulCapacity);
                    playerSouls.setUseCustomSoulCapacity(this.useCustomSoulCapacity);
                });
            }
        });
        return true;
    }
}
