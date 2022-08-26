package com.thaddev.coolideas.content.entities;

import com.thaddev.coolideas.mechanics.capabilities.PlayerSoulsProvider;
import com.thaddev.coolideas.mechanics.inits.EntityTypeInit;
import com.thaddev.coolideas.mechanics.inits.ItemInit;
import com.thaddev.coolideas.mechanics.networking.Packets;
import com.thaddev.coolideas.mechanics.networking.packets.ClientboundPlayerSoulsSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SoulOrb extends Entity {
    private static final int LIFETIME = 6000;
    private static final int ENTITY_SCAN_PERIOD = 10;
    private static final int MAX_FOLLOW_DIST = 100;
    private static final int ORB_GROUPS_PER_AREA = 40;
    private static final double ORB_MERGE_DISTANCE = 0.5D;
    private int age;
    private int health = 5;
    public int souls;
    private int count = 1;
    private Player followingPlayer;

    public SoulOrb(Level pLevel, double pX, double pY, double pZ, int pSouls) {
        this(EntityTypeInit.SOUL_ORB.get(), pLevel);
        this.setPos(pX, pY, pZ);
        this.setYRot((float) (this.random.nextDouble() * 360.0D));
        this.setDeltaMovement((this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D);
        this.souls = pSouls;
    }

    public SoulOrb(EntityType<? extends SoulOrb> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    protected void defineSynchedData() {
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
            this.setUnderwaterMovement();
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
            this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        if (!this.level.noCollision(this.getBoundingBox())) {
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }

        if (this.tickCount % ENTITY_SCAN_PERIOD == 1) {
            this.scanForEntities();
        }

        if (this.followingPlayer != null &&
            (this.followingPlayer.isSpectator() || this.followingPlayer.isDeadOrDying() ||
                (!this.followingPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(ItemInit.SCYTHE.get()) &&
                    !this.followingPlayer.getItemInHand(InteractionHand.OFF_HAND).is(ItemInit.SCYTHE.get())))) {
            this.followingPlayer = null;
        }

        if (this.followingPlayer != null) {
            float i = 1f;
            double diffY = this.followingPlayer.getEyeY() - this.getY();
            Vec3 vec3d = new Vec3(this.followingPlayer.getX() - this.getX(), diffY, this.followingPlayer.getZ() - this.getZ());
            this.setPos(this.getX(), this.getY() + vec3d.y * 0.015D * (double) i, this.getZ());
            if (this.level.isClientSide) {
                this.yOld = this.getY();
            }

            double d = 0.1D * (double) i;
            this.setDeltaMovement(this.getDeltaMovement()
                .multiply(0.75D, 0.75D, 0.75D)
                .add(vec3d.normalize().multiply(d, d, d)));
            this.hasImpulse = true;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        float f = 0.98F;
        if (this.onGround) {
            BlockPos pos = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
            f = this.level.getBlockState(pos).getFriction(this.level, pos, this) * 0.98F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.98D, f));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
        }

        ++this.age;
        if (this.age >= LIFETIME) {
            this.discard();
        }

    }

    private void scanForEntities() {
        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > MAX_FOLLOW_DIST ||
            (!this.followingPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(ItemInit.SCYTHE.get()) &&
                !this.followingPlayer.getItemInHand(InteractionHand.OFF_HAND).is(ItemInit.SCYTHE.get()))) {
            AABB box = AABB.ofSize(this.position(), MAX_FOLLOW_DIST, MAX_FOLLOW_DIST, MAX_FOLLOW_DIST);
            List<Player> list = getLevel().getEntities(EntityTypeTest.forClass(Player.class), box,
                (entity) ->
                    (entity.getItemInHand(InteractionHand.MAIN_HAND).is(ItemInit.SCYTHE.get()) ||
                        entity.getItemInHand(InteractionHand.OFF_HAND).is(ItemInit.SCYTHE.get()))
            );
            double minDist = Double.MAX_VALUE;
            Player closest = null;
            for (Player player : list) {
                double dist = player.distanceToSqr(this);
                if (dist < minDist) {
                    minDist = dist;
                    closest = player;
                }
            }
            this.followingPlayer = closest;
        }

        if (this.level instanceof ServerLevel) {
            for (SoulOrb experienceorb : this.level.getEntities(EntityTypeTest.forClass(SoulOrb.class), this.getBoundingBox().inflate(ORB_MERGE_DISTANCE), this::canMerge)) {
                this.merge(experienceorb);
            }
        }

    }

    public static void award(ServerLevel pLevel, Vec3 pPos, int pAmount) {
        while (pAmount > 0) {
            int i = getExperienceValue(pAmount);
            pAmount -= i;
            if (!tryMergeToExisting(pLevel, pPos, i)) {
                pLevel.addFreshEntity(new SoulOrb(pLevel, pPos.x(), pPos.y(), pPos.z(), i));
            }
        }

    }

    private static boolean tryMergeToExisting(ServerLevel pLevel, Vec3 pPos, int pAmount) {
        AABB aabb = AABB.ofSize(pPos, 1.0D, 1.0D, 1.0D);
        int i = pLevel.getRandom().nextInt(40);
        List<SoulOrb> list = pLevel.getEntities(EntityTypeTest.forClass(SoulOrb.class), aabb, (p_147081_) -> canMerge(p_147081_, i, pAmount));
        if (!list.isEmpty()) {
            SoulOrb experienceorb = list.get(0);
            ++experienceorb.count;
            experienceorb.age = 0;
            return true;
        } else {
            return false;
        }
    }

    private boolean canMerge(SoulOrb p_147087_) {
        return p_147087_ != this && canMerge(p_147087_, this.getId(), this.souls);
    }

    private static boolean canMerge(SoulOrb pOrb, int pAmount, int pOther) {
        return !pOrb.isRemoved() && (pOrb.getId() - pAmount) % ORB_GROUPS_PER_AREA == 0 && pOrb.souls == pOther;
    }

    private void merge(SoulOrb pOrb) {
        this.count += pOrb.count;
        this.age = Math.min(this.age, pOrb.age);
        pOrb.discard();
    }

    private void setUnderwaterMovement() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x * (double) 0.99F, Math.min(vec3.y + (double) 5.0E-4F, 0.06F), vec3.z * (double) 0.99F);
    }

    protected void doWaterSplashEffect() {
    }

    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (this.level.isClientSide || this.isRemoved()) return false; //Forge: Fixes MC-53850
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else if (this.level.isClientSide) {
            return true;
        } else {
            this.markHurt();
            this.health = (int) ((float) this.health - pAmount);
            if (this.health <= 0) {
                this.discard();
            }

            return true;
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putShort("Health", (short) this.health);
        pCompound.putShort("Age", (short) this.age);
        pCompound.putShort("Value", (short) this.souls);
        pCompound.putInt("Count", this.count);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.health = pCompound.getShort("Health");
        this.age = pCompound.getShort("Age");
        this.souls = pCompound.getShort("Value");
        this.count = Math.max(pCompound.getInt("Count"), 1);
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void playerTouch(@NotNull Player pEntity) {
        if (!this.level.isClientSide) {
            InteractionHand hand;
            if (pEntity.getItemInHand(hand = InteractionHand.MAIN_HAND).is(ItemInit.SCYTHE.get()) || pEntity.getItemInHand(hand = InteractionHand.OFF_HAND).is(ItemInit.SCYTHE.get())) {
                InteractionHand finalHand = hand;
                pEntity.getCapability(PlayerSoulsProvider.PLAYER_SOULS).ifPresent(playerSouls -> {
                    playerSouls.addSouls(getSouls());
                    Packets.sendToPlayer(new ClientboundPlayerSoulsSyncPacket(playerSouls.getSouls(), playerSouls.getSavedSoulCapacity(), playerSouls.isUseCustomSoulCapacity()), (ServerPlayer) pEntity);
                    pEntity.level.playSound(null, pEntity.position().x, pEntity.position().y, pEntity.position().z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.5F);
                    float toMinus = (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.UNBREAKING, pEntity.getItemInHand(finalHand)) * 0.1f) + 0.1f;
                    pEntity.getItemInHand(finalHand).hurtAndBreak((int) (getSouls() * (3f / 2f) * (1f - toMinus)), pEntity, (p_43276_) -> {
                        p_43276_.broadcastBreakEvent(getSlot(finalHand));
                    });
                    this.discard();
                });
            }
        }
    }

    private EquipmentSlot getSlot(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    /**
     * Returns the Soul value of this Soul orb.
     */
    public int getSouls() {
        return this.souls;
    }

    /**
     * Get a fragment of the maximum experience points value for the supplied value of experience points value.
     */
    public static int getExperienceValue(int pExpValue) {
        if (pExpValue >= 2477) {
            return 2477;
        } else if (pExpValue >= 1237) {
            return 1237;
        } else if (pExpValue >= 617) {
            return 617;
        } else if (pExpValue >= 307) {
            return 307;
        } else if (pExpValue >= 149) {
            return 149;
        } else if (pExpValue >= 73) {
            return 73;
        } else if (pExpValue >= 37) {
            return 37;
        } else if (pExpValue >= 17) {
            return 17;
        } else if (pExpValue >= 7) {
            return 7;
        } else {
            return pExpValue >= 3 ? 3 : 1;
        }
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean isAttackable() {
        return false;
    }

    public @NotNull Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, getId());
    }

    public @NotNull SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }
}
