package mod.acgaming.jockeys.entity;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.config.RegistryHelper;
import mod.acgaming.jockeys.init.JockeysRegistry;

public class WitherSkeletonGhast extends Monster
{
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(WitherSkeletonGhast.class, EntityDataSerializers.BOOLEAN);

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 100.0D);
    }

    public static boolean checkSpawnRules(EntityType<WitherSkeletonGhast> p_32735_, LevelAccessor p_32736_, MobSpawnType p_32737_, BlockPos p_32738_, Random p_32739_)
    {
        return p_32736_.getDifficulty() != Difficulty.PEACEFUL && p_32739_.nextInt(20) == 0 && checkMobSpawnRules(p_32735_, p_32736_, p_32737_, p_32738_, p_32739_);
    }

    private int explosionPower = 1;

    public WitherSkeletonGhast(EntityType<? extends WitherSkeletonGhast> p_32725_, Level p_32726_)
    {
        super(p_32725_, p_32726_);
        this.moveControl = new WitherSkeletonGhast.GhastMoveControl(this);
        this.xpReward = 5;
    }

    public boolean isCharging()
    {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean p_32759_)
    {
        this.entityData.set(DATA_IS_CHARGING, p_32759_);
    }

    public int getExplosionPower()
    {
        return this.explosionPower;
    }

    public SoundSource getSoundSource()
    {
        return SoundSource.HOSTILE;
    }

    protected boolean shouldDespawnInPeaceful()
    {
        return true;
    }

    protected SoundEvent getHurtSound(DamageSource p_32750_)
    {
        return SoundEvents.GHAST_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.GHAST_DEATH;
    }

    protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_)
    {
    }

    public boolean hurt(DamageSource p_32730_, float p_32731_)
    {
        if (this.isInvulnerableTo(p_32730_))
        {
            return false;
        }
        else if (p_32730_.getDirectEntity() instanceof LargeFireball && p_32730_.getEntity() instanceof Player)
        {
            super.hurt(p_32730_, 1000.0F);
            return true;
        }
        else
        {
            return super.hurt(p_32730_, p_32731_);
        }
    }

    public boolean onClimbable()
    {
        return false;
    }

    public boolean causeFallDamage(float p_147105_, float p_147106_, DamageSource p_147107_)
    {
        return false;
    }

    protected float getSoundVolume()
    {
        return 5.0F;
    }

    public void travel(Vec3 p_20818_)
    {
        if (this.isInWater())
        {
            this.moveRelative(0.02F, p_20818_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
        }
        else if (this.isInLava())
        {
            this.moveRelative(0.02F, p_20818_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
        }
        else
        {
            BlockPos ground = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
            float f = 0.91F;
            if (this.onGround)
            {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround)
            {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, p_20818_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(f));
        }

        this.calculateEntityAnimation(this, false);
    }

    protected float getStandingEyeHeight(Pose p_32741_, EntityDimensions p_32742_)
    {
        return 2.6F;
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(5, new WitherSkeletonGhast.RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(7, new WitherSkeletonGhast.GhastLookGoal(this));
        //this.goalSelector.addGoal(7, new WitherSkeletonGhast.GhastShootFireballGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (p_32755_) -> Math.abs(p_32755_.getY() - this.getY()) <= 4.0D));
    }

    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING, false);
    }

    public void tick()
    {
        super.tick();
        if (Jockeys.trickortreat && Jockeys.isHalloween())
        {
            if (this.random.nextInt(10000) == 0)
            {
                if (this.level.isClientSide)
                {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, this.getSoundSource(), 0.5F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
                }
                this.spawnAtLocation(RegistryHelper.getItemValueFromName("trickortreat:ghast_goodie_bag"));
            }
        }
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.GHAST_AMBIENT;
    }

    public void addAdditionalSaveData(CompoundTag p_32744_)
    {
        super.addAdditionalSaveData(p_32744_);
        p_32744_.putByte("ExplosionPower", (byte) this.explosionPower);
    }

    public void readAdditionalSaveData(CompoundTag p_32733_)
    {
        super.readAdditionalSaveData(p_32733_);
        if (p_32733_.contains("ExplosionPower", 99))
        {
            this.explosionPower = p_32733_.getByte("ExplosionPower");
        }
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance p_34172_)
    {
        if (Jockeys.isHalloween())
        {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawntype, @Nullable SpawnGroupData spawndata, @Nullable CompoundTag compoundtag)
    {
        spawndata = super.finalizeSpawn(level, difficulty, spawntype, spawndata, compoundtag);
        SniperWitherSkeleton wither_skeleton = JockeysRegistry.SNIPER_WITHER_SKELETON.get().create(this.level);
        if (wither_skeleton != null)
        {
            wither_skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            wither_skeleton.finalizeSpawn(level, difficulty, spawntype, null, null);
            wither_skeleton.startRiding(this);
        }
        return spawndata;
    }

    static class GhastLookGoal extends Goal
    {
        private final WitherSkeletonGhast ghast;

        public GhastLookGoal(WitherSkeletonGhast p_32762_)
        {
            this.ghast = p_32762_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse()
        {
            return true;
        }

        public void tick()
        {
            if (this.ghast.getTarget() == null)
            {
                Vec3 vec3 = this.ghast.getDeltaMovement();
                this.ghast.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI));
                this.ghast.yBodyRot = this.ghast.getYRot();
            }
            else
            {
                LivingEntity livingentity = this.ghast.getTarget();
                if (livingentity.distanceToSqr(this.ghast) < 4096.0D)
                {
                    double d1 = livingentity.getX() - this.ghast.getX();
                    double d2 = livingentity.getZ() - this.ghast.getZ();
                    this.ghast.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    this.ghast.yBodyRot = this.ghast.getYRot();
                }
            }

        }
    }

    static class GhastMoveControl extends MoveControl
    {
        private final WitherSkeletonGhast ghast;
        private int floatDuration;

        public GhastMoveControl(WitherSkeletonGhast p_32768_)
        {
            super(p_32768_);
            this.ghast = p_32768_;
        }

        public void tick()
        {
            if (this.operation == MoveControl.Operation.MOVE_TO)
            {
                if (this.floatDuration-- <= 0)
                {
                    this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
                    double d0 = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(d0)))
                    {
                        this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vec3.scale(0.2D)));
                    }
                    else
                    {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }
            }
        }

        private boolean canReach(Vec3 p_32771_, int p_32772_)
        {
            AABB aabb = this.ghast.getBoundingBox();

            for (int i = 1; i < p_32772_; ++i)
            {
                aabb = aabb.move(p_32771_);
                if (!this.ghast.level.noCollision(this.ghast, aabb))
                {
                    return false;
                }
            }
            return true;
        }
    }

    static class GhastShootFireballGoal extends Goal
    {
        private final WitherSkeletonGhast ghast;
        public int chargeTime;

        public GhastShootFireballGoal(WitherSkeletonGhast p_32776_)
        {
            this.ghast = p_32776_;
        }

        public boolean canUse()
        {
            return this.ghast.getTarget() != null;
        }

        public void start()
        {
            this.chargeTime = 0;
        }

        public void stop()
        {
            this.ghast.setCharging(false);
        }

        public void tick()
        {
            LivingEntity livingentity = this.ghast.getTarget();
            if (livingentity.distanceToSqr(this.ghast) < 4096.0D && this.ghast.hasLineOfSight(livingentity))
            {
                Level level = this.ghast.level;
                ++this.chargeTime;
                if (this.chargeTime == 10 && !this.ghast.isSilent())
                {
                    level.levelEvent(null, 1015, this.ghast.blockPosition(), 0);
                }
                if (this.chargeTime == 20)
                {
                    Vec3 vec3 = this.ghast.getViewVector(1.0F);
                    double d2 = livingentity.getX() - (this.ghast.getX() + vec3.x * 4.0D);
                    double d3 = livingentity.getY(0.5D) - (0.5D + this.ghast.getY(0.5D));
                    double d4 = livingentity.getZ() - (this.ghast.getZ() + vec3.z * 4.0D);
                    if (!this.ghast.isSilent())
                    {
                        level.levelEvent(null, 1016, this.ghast.blockPosition(), 0);
                    }
                    LargeFireball largefireball = new LargeFireball(level, this.ghast, d2, d3, d4, this.ghast.getExplosionPower());
                    largefireball.setPos(this.ghast.getX() + vec3.x * 4.0D, this.ghast.getY(0.5D) + 0.5D, largefireball.getZ() + vec3.z * 4.0D);
                    level.addFreshEntity(largefireball);
                    this.chargeTime = -40;
                }
            }
            else if (this.chargeTime > 0)
            {
                --this.chargeTime;
            }
            this.ghast.setCharging(this.chargeTime > 10);
        }
    }

    static class RandomFloatAroundGoal extends Goal
    {
        private final WitherSkeletonGhast ghast;

        public RandomFloatAroundGoal(WitherSkeletonGhast p_32783_)
        {
            this.ghast = p_32783_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse()
        {
            MoveControl movecontrol = this.ghast.getMoveControl();
            if (!movecontrol.hasWanted())
            {
                return true;
            }
            else
            {
                double d0 = movecontrol.getWantedX() - this.ghast.getX();
                double d1 = movecontrol.getWantedY() - this.ghast.getY();
                double d2 = movecontrol.getWantedZ() - this.ghast.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean canContinueToUse()
        {
            return false;
        }

        public void start()
        {
            Random random = this.ghast.getRandom();
            double d0 = this.ghast.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.ghast.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.ghast.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.ghast.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
        }
    }
}