package com.thaddev.coolideas.mixins;

import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.Utils;
import com.thaddev.coolideas.client.gui.ModMismatchScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.thaddev.coolideas.Utils.component;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow public abstract RegistryAccess registryAccess();

    @Inject(method = "handleSystemChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;handleSystemChat(Lnet/minecraft/network/chat/ChatType;Lnet/minecraft/network/chat/Component;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void handleSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        String message = packet.content().getString();
        if (message.contains("CoolIdeas")) {
            CoolIdeasMod.LOGGER.info("Message successfully suppressed: " + message);
            //FORMAT: [CoolIdeas] https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message (versionid:<Modloader>-mc<GameVersion>-<ModVersion>)
            //  example: [CoolIdeas] https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message (versionid:forge-mc1.19-2.0.0)
            String[] split = message.split(" ");
            String serverVersionString = split[2].substring(11, split[2].length() - 1);
            String serverVersion = serverVersionString.split("-")[2];
            if (serverVersionString.split("-").length > 3) {
                serverVersion += "-";
                serverVersion += serverVersionString.split("-")[3];
            }
            String serverModLoader = serverVersionString.split("-")[0];

            String clientModLoader = ClientBrandRetriever.getClientModName();
            String clientVersion = CoolIdeasMod.VERSION;
            //TO SEND: [CoolIdeas] Welcome, <playerName>! Server is running Cool Ideas Mod version <modVersion>, For <modLoader> <gameVersion>.

            String niceServerModLoader = Utils.niceify(serverModLoader);
            String niceClientModLoader = Utils.niceify(clientModLoader);
            Registry<ChatType> registry = this.registryAccess().registryOrThrow(Registry.CHAT_TYPE_REGISTRY);
            ChatType chatType = packet.resolveType(registry);
            Minecraft.getInstance().gui.handleSystemChat(
                chatType,
                component(Utils.from(""))
                    .append(Component.translatable(CoolIdeasMod.MESSAGE_WELCOME, Utils.fromNoTag("(%$yellow)" + Minecraft.getInstance().player.getName().getString() + "(%$reset)"), serverVersion, niceServerModLoader, SharedConstants.VERSION_STRING)
                        .withStyle(ChatFormatting.GRAY)
                    )
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
