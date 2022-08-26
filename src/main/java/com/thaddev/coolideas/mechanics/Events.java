package com.thaddev.coolideas.mechanics;

import com.thaddev.coolideas.CoolIdeasMod;
import com.thaddev.coolideas.Utils;
import com.thaddev.coolideas.content.blocks.SoulBottleBlock;
import com.thaddev.coolideas.content.blocks.SoulVialBlock;
import com.thaddev.coolideas.content.entities.SoulOrb;
import com.thaddev.coolideas.content.entities.projectiles.DiamondHeadedArrow;
import com.thaddev.coolideas.content.entities.projectiles.ShortBowArrow;
import com.thaddev.coolideas.content.items.SoulchargableItemUtils;
import com.thaddev.coolideas.mechanics.capabilities.PlayerSouls;
import com.thaddev.coolideas.mechanics.capabilities.PlayerSoulsProvider;
import com.thaddev.coolideas.mechanics.inits.BlockInit;
import com.thaddev.coolideas.mechanics.inits.EffectInit;
import com.thaddev.coolideas.mechanics.inits.ItemInit;
import com.thaddev.coolideas.mechanics.inits.TagsInit;
import com.thaddev.coolideas.mechanics.networking.Packets;
import com.thaddev.coolideas.mechanics.networking.packets.ClientboundPlayerSoulsSyncPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.thaddev.coolideas.Utils.component;

@Mod.EventBusSubscriber(modid = CoolIdeasMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {
    @SubscribeEvent
    public static void onPlayerStripAxe(final BlockEvent.BlockToolModificationEvent event) {
        if (!event.isSimulated() && event.getPlayer() instanceof ServerPlayer player
            && event.getToolAction() == ToolActions.AXE_STRIP
            && event.getState().is(TagsInit.REGULAR_LOGS)) {
            Level level = player.getLevel();
            BlockPos blockpos = event.getPos();

            InteractionHand otherHand = event.getContext().getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            if (player.getItemInHand(otherHand).getItem() == Items.GLASS_BOTTLE) {
                ItemStack stack = player.getItemInHand(otherHand);
                ItemStack newStack = new ItemStack(ItemInit.RAW_RUBBER_BOTTLE.get(), 1);
                stack.shrink(1);
                player.level.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1F, 1F);
                if (stack.isEmpty()) {
                    player.getInventory().removeItem(stack);
                    player.setItemInHand(otherHand, newStack);
                } else {
                    if (!player.addItem(newStack)) {
                        ItemEntity drop = new ItemEntity(level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), newStack);
                        level.addFreshEntity(drop);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(final LivingHurtEvent event) {
        MobEffectInstance effect;
        if ((effect = event.getEntity().getEffect(EffectInit.VULNERABILITY.get())) != null) {
            int amplifier = Math.min(effect.getAmplifier() + 1, 4);
            int toReduce = amplifier > 3 ? ((amplifier - 1) * 2) + 3 : amplifier * 2;
            event.getEntity().invulnerableTime = 20 - toReduce;
        }
        if ((event.getSource().getDirectEntity() instanceof DiamondHeadedArrow | event.getSource().getDirectEntity() instanceof ShortBowArrow) && event.getSource().getEntity() instanceof Player player) {
            player.level.playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.3F, 0.5F);
        }
    }

    @SubscribeEvent
    public static void onLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.level.getServer() != null) {// just in case
            String loader = player.level.getServer().getServerModName().toLowerCase();
            player.sendSystemMessage(
                component(Utils.from("")).copy()
                    .append(Component.literal("https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message").setStyle(
                        Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/MyNameTsThad/CoolIdeasMod/blob/forge-119/README.md#ignore-if-you-did-not-come-from-an-in-game-chat-message"))
                            .withColor(ChatFormatting.BLUE)
                            .withUnderlined(true)
                    ))
                    .append(Component.literal(" (versionid:" + CoolIdeasMod.buildVersionString(loader) + ")"))
            );
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(PlayerSoulsProvider.PLAYER_SOULS).isPresent()) {
                event.addCapability(new ResourceLocation(CoolIdeasMod.MODID, "properties"), new PlayerSoulsProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(final PlayerEvent.Clone event) {
        CoolIdeasMod.LOGGER.info("Player cloned");
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(PlayerSoulsProvider.PLAYER_SOULS).ifPresent(oldPlayerSouls -> {
            CoolIdeasMod.LOGGER.info("capability present");
            if (event.isWasDeath() && !event.getOriginal().getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                SoulOrb.award((ServerLevel) event.getOriginal().getLevel(), event.getOriginal().position(), oldPlayerSouls.getSouls());
                CoolIdeasMod.LOGGER.info("dropped " + oldPlayerSouls.getSouls() + " levels");
            } else {
                event.getEntity().getCapability(PlayerSoulsProvider.PLAYER_SOULS).ifPresent(newPlayerSouls -> {
                    newPlayerSouls.copyFrom(oldPlayerSouls);
                    Packets.sendToPlayer(new ClientboundPlayerSoulsSyncPacket(newPlayerSouls.getSouls(), newPlayerSouls.getSavedSoulCapacity(), newPlayerSouls.isUseCustomSoulCapacity()), (ServerPlayer) event.getEntity());
                });
            }
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(PlayerSouls.class);
    }

    @SubscribeEvent
    public static void onDropExperience(final LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() != null &&
            (event.getAttackingPlayer().getItemInHand(InteractionHand.MAIN_HAND).is(ItemInit.SCYTHE.get()) ||
                event.getAttackingPlayer().getItemInHand(InteractionHand.OFF_HAND).is(ItemInit.SCYTHE.get()))) {
            if (!event.getEntity().getLevel().isClientSide) {
                SoulOrb.award((ServerLevel) event.getEntity().getLevel(), event.getEntity().position(), event.getDroppedExperience());
            }
        }
    }

    @SubscribeEvent
    public static void onKnockback(final LivingKnockBackEvent event) {
        if (event.getEntity().getKillCredit() instanceof Player player && player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemInit.SCYTHE.get())) {
            double strength = event.getStrength() * -1;
            double pX = event.getRatioX();
            double pZ = event.getRatioZ();
            strength *= 1.0D - event.getEntity().getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);

            event.getEntity().hasImpulse = true;
            Vec3 vec3 = event.getEntity().getDeltaMovement();
            Vec3 vec31 = (new Vec3(pX, 0, pZ)).normalize().scale(strength);
            event.getEntity().setDeltaMovement(vec3.x / 2.0D - vec31.x, event.getEntity().isOnGround() ? Math.min(0.4D, vec3.y / 2.0D + (-strength)) : vec3.y, vec3.z / 2.0D - vec31.z);

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUseBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().getItemInHand(event.getHand()).is(TagsInit.SOULCHARGABLE)) {
            if (event.getLevel().getBlockState(event.getPos()) == Blocks.SOUL_FIRE.defaultBlockState()) {
                if (SoulchargableItemUtils.tryCharge(event.getEntity().getItemInHand(event.getHand()))) {
                    event.getLevel().playSound(
                        null,
                        event.getEntity().position().x, event.getEntity().position().y, event.getEntity().position().z,
                        SoundEvents.ZOMBIE_VILLAGER_CURE,
                        SoundSource.NEUTRAL,
                        1, 1
                    );

                    if (event.getLevel().getBlockState(new BlockPos(event.getPos().getX(), event.getPos().getY() - 1, event.getPos().getZ())) != Blocks.AIR.defaultBlockState()) {
                        event.getLevel().setBlockAndUpdate(new BlockPos(event.getPos().getX(), event.getPos().getY() - 1, event.getPos().getZ()), Blocks.GRAVEL.defaultBlockState());
                    }
                    event.getLevel().setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            } else if (event.getLevel().getBlockState(event.getPos()) == Blocks.SOUL_CAMPFIRE.defaultBlockState()) {
                if (SoulchargableItemUtils.tryCharge(event.getEntity().getItemInHand(event.getHand()))) {
                    event.getLevel().playSound(
                        null,
                        event.getEntity().position().x, event.getEntity().position().y, event.getEntity().position().z,
                        SoundEvents.ZOMBIE_VILLAGER_CURE,
                        SoundSource.NEUTRAL,
                        1, 1
                    );
                    event.getLevel().setBlockAndUpdate(event.getPos(), event.getLevel().getBlockState(event.getPos()).setValue(BlockStateProperties.LIT, false));
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            } else if (event.getLevel().getBlockState(event.getPos()) == Blocks.SOUL_LANTERN.defaultBlockState()) {
                if (SoulchargableItemUtils.tryCharge(event.getEntity().getItemInHand(event.getHand()))) {
                    event.getLevel().playSound(
                        null,
                        event.getEntity().position().x, event.getEntity().position().y, event.getEntity().position().z,
                        SoundEvents.ZOMBIE_VILLAGER_CURE,
                        SoundSource.NEUTRAL,
                        1, 1
                    );
                    event.getLevel().setBlockAndUpdate(event.getPos(), Blocks.LANTERN.defaultBlockState());
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            } else if (event.getLevel().getBlockState(event.getPos()).getBlock() == BlockInit.SOUL_BOTTLE.get() ||
                event.getLevel().getBlockState(event.getPos()).getBlock() == BlockInit.SOUL_JAR.get() ||
                event.getLevel().getBlockState(event.getPos()).getBlock() == BlockInit.SOUL_GALLON.get()) {
                BlockState state = event.getLevel().getBlockState(event.getPos());
                if (state.getValue(SoulVialBlock.FILLED) && SoulchargableItemUtils.tryCharge(event.getEntity().getItemInHand(event.getHand()))) {
                    event.getLevel().playSound(
                        null,
                        event.getEntity().position().x, event.getEntity().position().y, event.getEntity().position().z,
                        SoundEvents.ZOMBIE_VILLAGER_CURE,
                        SoundSource.NEUTRAL,
                        1, 1
                    );
                    event.getLevel().setBlockAndUpdate(event.getPos(), state.setValue(SoulBottleBlock.FILLED, false));
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            } else if (event.getLevel().getBlockState(event.getPos()) == Blocks.SOUL_SAND.defaultBlockState()) {
                SoulchargableItemUtils.incrementSoulSandProgress(event.getEntity().getItemInHand(event.getHand()), 1);
                event.getLevel().playSound(
                    null,
                    event.getEntity().position().x, event.getEntity().position().y, event.getEntity().position().z,
                    SoundEvents.SOUL_ESCAPE,
                    SoundSource.NEUTRAL,
                    3, 1
                );

                RandomSource randomsource = RandomSource.create();
                for (int i = 0; i < 5; i++) {
                    double velX = -((event.getPos().getX() + randomsource.nextDouble()) - (event.getEntity().getX()) + (randomsource.nextDouble() / 10D));
                    double velZ = -((event.getPos().getZ() + randomsource.nextDouble()) - (event.getEntity().getZ()) + (randomsource.nextDouble() / 10D));
                    velX /= 10D;
                    velZ /= 10D;
                    CoolIdeasMod.LOGGER.info("velX: " + velX);
                    CoolIdeasMod.LOGGER.info("velZ: " + velZ);
                    event.getLevel().addParticle(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        (double) event.getPos().getX() + randomsource.nextDouble(),
                        (double) event.getPos().getY() + 1D,
                        (double) event.getPos().getZ() + randomsource.nextDouble(),
                        velX,
                        0.05D,
                        velZ
                    );
                }

                event.getEntity().sendSystemMessage(component(Utils.fromNoTag("(%$aqua)The Souls merge... (" +
                    SoulchargableItemUtils.getSoulSandProgress(event.getEntity().getItemInHand(event.getHand())) + "/" +
                    SoulchargableItemUtils.MAX_SOUL_SAND_PROGRESS + ")")));
                event.getLevel().setBlockAndUpdate(event.getPos(), Blocks.SOUL_SOIL.defaultBlockState());
                event.setCancellationResult(InteractionResult.SUCCESS);

            } else {
                event.setCancellationResult(InteractionResult.PASS);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player && !player.level.isClientSide()) {
            ItemStack stack;
            if ((stack = player.getItemInHand(InteractionHand.MAIN_HAND)).is(TagsInit.SOULCHARGABLE) && event.getEntity() instanceof WitherSkeleton) {
                SoulchargableItemUtils.incrementWitherSkeletonProgress(stack, 1);
                player.level.playSound(
                    null,
                    event.getEntity().position().x, event.getEntity().position().y, event.getEntity().position().z,
                    SoundEvents.SOUL_ESCAPE,
                    SoundSource.HOSTILE,
                    1, 1
                );
                player.sendSystemMessage(component(Utils.fromNoTag("(%$gray)The Souls merge... (" +
                    SoulchargableItemUtils.getWitherSkeletonProgress(stack) + "/" +
                    SoulchargableItemUtils.MAX_WITHER_SKELETON_PROGRESS + ")")));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(final EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerSoulsProvider.PLAYER_SOULS).ifPresent(playerSouls -> {
                    Packets.sendToPlayer(new ClientboundPlayerSoulsSyncPacket(playerSouls.getSouls(), playerSouls.getSavedSoulCapacity(), playerSouls.isUseCustomSoulCapacity()), player);
                });
            }
        }
    }
}
