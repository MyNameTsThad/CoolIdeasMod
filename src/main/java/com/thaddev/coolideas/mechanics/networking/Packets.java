package com.thaddev.coolideas.mechanics.networking;

import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.mechanics.networking.packets.ClientboundPlayerSoulsSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Packets {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    public static int id() {
        return packetId++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(CoolIdeasMod.MODID, "packets_main"))
            .networkProtocolVersion(() -> CoolIdeasMod.VERSION)
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ClientboundPlayerSoulsSyncPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
            .decoder(ClientboundPlayerSoulsSyncPacket::new)
            .encoder(ClientboundPlayerSoulsSyncPacket::toBytes)
            .consumerMainThread(ClientboundPlayerSoulsSyncPacket::handle)
            .add();
    }

    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
