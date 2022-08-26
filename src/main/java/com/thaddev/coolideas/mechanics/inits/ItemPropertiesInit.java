package com.thaddev.coolideas.mechanics.inits;

import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.content.items.SoulchargableItemUtils;
import com.thaddev.coolideas.content.items.materials.SoulContainerBlockItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ItemPropertiesInit {
    public static void register() {
        ItemProperties.register(ItemInit.SCYTHE.get(), new ResourceLocation(CoolIdeasMod.MODID, "charged"),
            (pStack, pLevel, pEntity, pSeed) -> SoulchargableItemUtils.isCharged(pStack) ? 1.0F : 0.0F
        );

        ItemProperties.register(ItemInit.SOUL_VIAL.get(), new ResourceLocation(CoolIdeasMod.MODID, "filled"),
            (pStack, pLevel, pEntity, pSeed) -> SoulContainerBlockItem.isFilled(pStack) ? 1.0F : 0.0F
        );

        ItemProperties.register(ItemInit.SOUL_BOTTLE.get(), new ResourceLocation(CoolIdeasMod.MODID, "filled"),
            (pStack, pLevel, pEntity, pSeed) -> SoulContainerBlockItem.isFilled(pStack) ? 1.0F : 0.0F
        );

        ItemProperties.register(ItemInit.SOUL_JAR.get(), new ResourceLocation(CoolIdeasMod.MODID, "filled"),
            (pStack, pLevel, pEntity, pSeed) -> SoulContainerBlockItem.isFilled(pStack) ? 1.0F : 0.0F
        );

        ItemProperties.register(ItemInit.SOUL_GALLON.get(), new ResourceLocation(CoolIdeasMod.MODID, "filled"),
            (pStack, pLevel, pEntity, pSeed) -> SoulContainerBlockItem.isFilled(pStack) ? 1.0F : 0.0F
        );
    }
}
