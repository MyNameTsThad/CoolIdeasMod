package com.thaddev.coolideas.client;

import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.client.renderer.entity.DiamondHeadedArrowRenderer;
import com.thaddev.coolideas.client.renderer.entity.SoulOrbRenderer;
import com.thaddev.coolideas.mechanics.inits.EntityTypeInit;
import com.thaddev.coolideas.mechanics.inits.ItemInit;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CoolIdeasMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void clientSetupEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityTypeInit.DIAMOND_HEADED_ARROW.get(), DiamondHeadedArrowRenderer::new);
        event.registerEntityRenderer(EntityTypeInit.SOUL_ORB.get(), SoulOrbRenderer::new);
    }

    @SubscribeEvent
    public static void colorItems(final RegisterColorHandlersEvent.Item event) {
        event.register(
                (itemStack, color) -> color > 0 ? -1 : PotionUtils.getColor(itemStack),
                ItemInit.TIPPED_DIAMOND_HEADED_ARROW.get()
        );
    }
}
