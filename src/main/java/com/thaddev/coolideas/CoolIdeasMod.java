package com.thaddev.coolideas;

import com.mojang.logging.LogUtils;
import com.thaddev.coolideas.mechanics.inits.BlockInit;
import com.thaddev.coolideas.mechanics.inits.EffectInit;
import com.thaddev.coolideas.mechanics.inits.EnchantmentInit;
import com.thaddev.coolideas.mechanics.inits.EntityTypeInit;
import com.thaddev.coolideas.mechanics.inits.ItemInit;
import com.thaddev.coolideas.mechanics.inits.PotionInit;
import com.thaddev.coolideas.mechanics.inits.PotionRecipeInit;
import com.thaddev.coolideas.mechanics.inits.RecipeSerializerInit;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


@Mod(CoolIdeasMod.MODID)
public class CoolIdeasMod {
    public static final String MODID = "coolideas";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static CoolIdeasMod instance;
    public static String VERSION = "1.8.0";

    public static final String MESSAGE_WELCOME = "message.coolideas.welcome";
    public static final String SCREEN_VERSION_MISMATCH = "menu.coolideas.modmismatch";

    public Minecraft mc;

    //CLIENT ONLY
    public boolean isMismatching = false;

    public CoolIdeasMod() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CoolIdeasMod.LOGGER.info("Initializing CoolIdeasMod version {}", VERSION);

        modEventBus.addListener(this::setup);
        ItemInit.ITEMS.register(modEventBus);
        EntityTypeInit.ENTITIES.register(modEventBus);
        EnchantmentInit.ENCHANTMENTS.register(modEventBus);
        EffectInit.MOB_EFFECTS.register(modEventBus);
        PotionInit.POTIONS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        RecipeSerializerInit.RECIPES.register(modEventBus);
    }

    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PotionRecipeInit::register);
    }

    public static String buildVersionString(String modLoader) {
        return modLoader + "-mc" + SharedConstants.VERSION_STRING + "-" + VERSION;
    }
}
