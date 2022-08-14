package com.thaddev.coolideas.mechanics;


import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.mechanics.inits.ItemInit;
import com.thaddev.coolideas.mechanics.inits.TagsInit;
import com.thaddev.coolideas.util.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

import static com.thaddev.coolideas.util.Utils.component;

public class Events {
    private static final HashMap<Block, Block> STRIPPED_BLOCKS = new HashMap<>();

    static {
        STRIPPED_BLOCKS.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG);
        STRIPPED_BLOCKS.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG);
        STRIPPED_BLOCKS.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG);
        STRIPPED_BLOCKS.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG);
        STRIPPED_BLOCKS.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG);
        STRIPPED_BLOCKS.put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG);
        STRIPPED_BLOCKS.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG);
        STRIPPED_BLOCKS.put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM);
        STRIPPED_BLOCKS.put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM);
        STRIPPED_BLOCKS.put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD);
        STRIPPED_BLOCKS.put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD);
        STRIPPED_BLOCKS.put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD);
        STRIPPED_BLOCKS.put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD);
        STRIPPED_BLOCKS.put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD);
        STRIPPED_BLOCKS.put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD);
        STRIPPED_BLOCKS.put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD);
        STRIPPED_BLOCKS.put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE);
        STRIPPED_BLOCKS.put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE);
    }

    public static void registerEvents() {
        CoolIdeasMod.LOGGER.debug("Registering Events for " + CoolIdeasMod.MODID + " (1/11)"); //it now does something :sunglasses:

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos targetPos = hitResult.getBlockPos();
            BlockState targetBlock = world.getBlockState(targetPos);
            ItemStack heldItem = player.getStackInHand(hand);
            Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;

            if (heldItem.getItem() instanceof AxeItem && player.getStackInHand(otherHand).getItem() == Items.GLASS_BOTTLE) {
                if (targetBlock.isIn(TagsInit.REGULAR_LOGS)) {
                    if (player instanceof ServerPlayerEntity) {
                        Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, targetPos, heldItem);
                        if (!player.isCreative())
                            heldItem.damage(1, net.minecraft.util.math.random.Random.create(), null);
                        world.setBlockState(targetPos, STRIPPED_BLOCKS.get(targetBlock.getBlock()).getStateWithProperties(targetBlock));
                        world.playSound(player, targetPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }
                    ItemStack stack = player.getStackInHand(otherHand);
                    ItemStack newStack = new ItemStack(ItemInit.RAW_RUBBER_BOTTLE, 1);
                    stack.decrement(1);
                    world.playSound(null, targetPos.getX(), targetPos.getY(), targetPos.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1F, 1F);
                    if (stack.isEmpty()) {
                        player.getInventory().removeOne(stack);
                        player.setStackInHand(otherHand, newStack);
                    } else {
                        if (!player.giveItemStack(newStack)) {
                            ItemEntity drop = new ItemEntity(world, targetPos.getX(), targetPos.getY(), targetPos.getZ(), newStack);
                            world.spawnEntity(drop);
                        }
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            String loader = server.getServerModName().toLowerCase();
            handler.player.sendMessage(
                component(Utils.from("")).copy()
                    .append(Text.of("https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message").copy().setStyle(
                        Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message"))
                            .withColor(Formatting.BLUE)
                            .withUnderline(true)
                    ))
                    .append(Text.of(" (versionid:" + CoolIdeasMod.buildVersionString(loader) + ")"))
            );
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (CoolIdeasMod.instance.isMismatching) {
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, Utils.fromNoTag("(%$white)(%$bold)(%$underline)Version Mismatch! (from CoolIdeasMod)"), 10, 10, 100);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, Utils.fromNoTag("(%$white)Please change your modloader / mod version to match"), 10, 22, 100);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, Utils.fromNoTag("(%$white)the server modloader / mod version in the warning"), 10, 32, 100);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, Utils.fromNoTag("(%$white)message displayed when you join!"), 10, 42, 100);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, Utils.fromNoTag("(%$gold)(%$underline)If you encounter a bug and report it, Anything that happens in"), 10, 62, 100);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, Utils.fromNoTag("(%$gold)(%$underline)this server connection instance will not be considered valid evidence."), 10, 74, 100);
            }
        });
    }
}
