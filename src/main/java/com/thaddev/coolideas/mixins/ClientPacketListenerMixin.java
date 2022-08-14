package com.thaddev.coolideas.mixins;

import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.client.gui.ModMismatchScreen;
import com.thaddev.coolideas.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.thaddev.coolideas.util.Utils.component;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow public abstract RegistryAccess registryAccess();

    @Inject(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;handleChat(Lnet/minecraft/network/chat/ChatType;Lnet/minecraft/network/chat/Component;Ljava/util/UUID;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void handleSystemChat(ClientboundChatPacket packet, CallbackInfo ci) {
        if (packet.getType() == ChatType.SYSTEM){
            String message = packet.getMessage().getString();
            if (message.contains("CoolIdeas")) {
                CoolIdeasMod.LOGGER.info("Message successfully suppressed: " + message);
                //FORMAT: [CoolIdeas] https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message (versionid:<Modloader>-mc<GameVersion>-<ModVersion>)
                //  example: [CoolIdeas] https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message (versionid:forge-mc1.19-2.0.0)
                String[] split = message.split(" ");
                String serverVersionString = split[2].substring(11, split[2].length() - 1);
                String serverVersion = serverVersionString.split("-")[2];
                if (serverVersionString.split("-").length > 3)
                    serverVersion += serverVersionString.split("-")[3];
                String serverModLoader = serverVersionString.split("-")[0];

                String clientModLoader = ClientBrandRetriever.getClientModName();
                String clientVersion = CoolIdeasMod.VERSION;
                //TO SEND: [CoolIdeas] Welcome, <playerName>! Server is running Cool Ideas Mod version <modVersion>, For <modLoader> <gameVersion>.

                String niceServerModLoader = Utils.niceify(serverModLoader);
                String niceClientModLoader = Utils.niceify(clientModLoader);
                Minecraft.getInstance().gui.handleChat(
                    packet.getType(),
                    component(Utils.from(""))
                        .append(new TranslatableComponent(CoolIdeasMod.MESSAGE_WELCOME, Utils.fromNoTag("(%$yellow)" + Minecraft.getInstance().player.getName().getString() + "(%$reset)"), serverVersion, niceServerModLoader, SharedConstants.VERSION_STRING)
                            .withStyle(ChatFormatting.GRAY)
                        ),
                    packet.getSender()
                );

                if (!serverVersion.equals(clientVersion)) {
                    Minecraft.getInstance().gui.getChat()
                        .addMessage(component(Utils.from("(%$red)(%$bold) Mod Version mismatch! Client is " + clientVersion + ", while server is " + serverVersion)));
                    Minecraft.getInstance().setScreen(new ModMismatchScreen(clientVersion, serverVersion, niceClientModLoader, niceServerModLoader));
                }
                if (!serverModLoader.equals(clientModLoader)) {
                    Minecraft.getInstance().gui.getChat()
                        .addMessage(component(Utils.from("(%$red)(%$bold) Modloader mismatch! Client is " + niceClientModLoader + ", while server is " + niceServerModLoader)));
                    Minecraft.getInstance().setScreen(new ModMismatchScreen(clientVersion, serverVersion, niceClientModLoader, niceServerModLoader));
                }

                ci.cancel();
            }
        }
    }
}