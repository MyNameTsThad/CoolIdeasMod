package com.thaddev.coolideas.client;

import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CoolIdeasMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onRenderHud(final RenderGuiOverlayEvent.Post event) {
        if (CoolIdeasMod.instance.isMismatching){
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), Utils.fromNoTag("(%$white)(%$bold)(%$underline)Version Mismatch! (from CoolIdeasMod)"), 10, 10, 100);
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), Utils.fromNoTag("(%$white)Please change your modloader / mod version to match"), 10, 22, 100);
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), Utils.fromNoTag("(%$white)the server modloader / mod version in the warning"), 10, 32, 100);
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), Utils.fromNoTag("(%$white)message displayed when you join!"), 10, 42, 100);
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), Utils.fromNoTag("(%$gold)(%$underline)If you encounter a bug and report it, Anything that happens in"), 10, 62, 100);
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), Utils.fromNoTag("(%$gold)(%$underline)this server connection instance will not be considered valid evidence."), 10, 74, 100);
        }
    }
}
