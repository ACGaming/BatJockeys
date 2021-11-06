package mod.acgaming.jockeys.entity;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import mod.acgaming.jockeys.Jockeys;

public class VexBat extends Monster
{
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(VexBat.class, EntityDataSerializers.BYTE);

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    public static boolean checkSpawnRules(EntityType<VexBat> p_32735_, LevelAccessor p_32736_, MobSpawnType p_32737_, BlockPos p_32738_, Random p_32739_)
    {
        return p_32736_.getDifficulty() != Difficulty.PEACEFUL && p_32739_.nextInt(20) == 0 && checkMobSpawnRules(p_32735_, p_32736_, p_32737_, p_32738_, p_32739_);
    }

    @Nullable
    private BlockPos boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    public VexBat(EntityType<? extends VexBat> p_33984_, Level p_33985_)
    {
        super(p_33984_, p_33985_);
        this.moveControl = new VexBatMoveControl(this);
        this.xpReward = 3;
    }

    public void move(MoverType p_33997_, Vec3 p_33998_)
    {
        super.move(p_33997_, p_33998_);
        this.checkInsideBlocks();
    }

    public float getBrightness()
    {
        return 1.0F;
    }

    @Nullable
    public BlockPos getBoundOrigin()
    {
        return this.boundOrigin;
    }

    public boolean isCharging()
    {
        return this.getVexBatFlag(1);
    }

    public void setIsCharging(boolean p_34043_)
    {
        this.setVexBatFlag(1, p_34043_);
    }

    public void setLimitedLife(int p_33988_)
    {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = p_33988_;
    }

    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new VexBatChargeAttackGoal());
        this.goalSelector.addGoal(8, new VexBatRandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    public void tick()
    {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0)
        {
            this.limitedLifeTicks = 20;
            this.hurt(DamageSource.STARVE, 1.0F);
        }
        if (this.level.isClientSide)
        {
            if (this.random.nextInt(20) == 0)
            {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_LOOP, this.getSoundSource(), 0.4F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }
            this.level.addParticle(ParticleTypes.WARPED_SPORE, this.getX(), this.getY() + 1.0F, this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.BAT_AMBIENT;
    }

    public void addAdditionalSaveData(CompoundTag p_34015_)
    {
        super.addAdditionalSaveData(p_34015_);
        if (this.boundOrigin != null)
        {
            p_34015_.putInt("BoundX", this.boundOrigin.getX());
            p_34015_.putInt("BoundY", this.boundOrigin.getY());
            p_34015_.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.hasLimitedLife)
        {
            p_34015_.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }

    public void readAdditionalSaveData(CompoundTag p_34008_)
    {
        super.readAdditionalSaveData(p_34008_);
        if (p_34008_.contains("BoundX"))
        {
            this.boundOrigin = new BlockPos(p_34008_.getInt("BoundX"), p_34008_.getInt("BoundY"), p_34008_.getInt("BoundZ"));
        }

        if (p_34008_.contains("LifeTicks"))
        {
            this.setLimitedLife(p_34008_.getInt("LifeTicks"));
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
        Vex vex = EntityType.VEX.create(this.level);
        if (vex != null)
        {
            vex.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            vex.finalizeSpawn(level, difficulty, spawntype, null, null);
            if (Jockeys.isHalloween())
            {
                vex.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
            }
            vex.startRiding(this);
        }
        return spawndata;
    }

    protected SoundEvent getHurtSound(DamageSource p_34023_)
    {
        return SoundEvents.BAT_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BAT_DEATH;
    }

    protected float getSoundVolume()
    {
        return 0.1F;
    }

    public float getVoicePitch()
    {
        return super.getVoicePitch() * 0.95F;
    }

    private boolean getVexBatFlag(int p_34011_)
    {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & p_34011_) != 0;
    }

    private void setVexBatFlag(int p_33990_, boolean p_33991_)
    {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (p_33991_)
        {
            i = i | p_33990_;
        }
        else
        {
            i = i & ~p_33990_;
        }
        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    class VexBatChargeAttackGoal extends Goal
    {
        public VexBatChargeAttackGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse()
        {
            if (VexBat.this.getTarget() != null && !VexBat.this.getMoveControl().hasWanted() && VexBat.this.random.nextInt(7) == 0)
            {
                return VexBat.this.distanceToSqr(VexBat.this.getTarget()) > 4.0D;
            }
            else
            {
                return false;
            }
        }

        public boolean canContinueToUse()
        {
            return VexBat.this.getMoveControl().hasWanted() && VexBat.this.isCharging() && VexBat.this.getTarget() != null && VexBat.this.getTarget().isAlive();
        }

        public void start()
        {
            LivingEntity livingentity = VexBat.this.getTarget();
            Vec3 vec3 = livingentity.getEyePosition();
            VexBat.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
            VexBat.this.setIsCharging(true);
            VexBat.this.playSound(SoundEvents.BAT_TAKEOFF, 1.0F, 1.0F);
        }

        public void stop()
        {
            VexBat.this.setIsCharging(false);
        }

        public void tick()
        {
            LivingEntity livingentity = VexBat.this.getTarget();
            if (VexBat.this.getBoundingBox().intersects(livingentity.getBoundingBox()))
            {
                VexBat.this.doHurtTarget(livingentity);
                VexBat.this.setIsCharging(false);
            }
            else
            {
                double d0 = VexBat.this.distanceToSqr(livingentity);
                if (d0 < 9.0D)
                {
                    Vec3 vec3 = livingentity.getEyePosition();
                    VexBat.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
                }
            }

        }
    }

    class VexBatMoveControl extends MoveControl
    {
        public VexBatMoveControl(VexBat p_34062_)
        {
            super(p_34062_);
        }

        public void tick()
        {
            if (this.operation == MoveControl.Operation.MOVE_TO)
            {
                Vec3 vec3 = new Vec3(this.wantedX - VexBat.this.getX(), this.wantedY - VexBat.this.getY(), this.wantedZ - VexBat.this.getZ());
                double d0 = vec3.length();
                if (d0 < VexBat.this.getBoundingBox().getSize())
                {
                    this.operation = MoveControl.Operation.WAIT;
                    VexBat.this.setDeltaMovement(VexBat.this.getDeltaMovement().scale(0.5D));
                }
                else
                {
                    VexBat.this.setDeltaMovement(VexBat.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
                    if (VexBat.this.getTarget() == null)
                    {
                        Vec3 vec31 = VexBat.this.getDeltaMovement();
                        VexBat.this.setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float) Math.PI));
                    }
                    else
                    {
                        double d2 = VexBat.this.getTarget().getX() - VexBat.this.getX();
                        double d1 = VexBat.this.getTarget().getZ() - VexBat.this.getZ();
                        VexBat.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                    }
                    VexBat.this.yBodyRot = VexBat.this.getYRot();
                }
            }
        }
    }

    class VexBatRandomMoveGoal extends Goal
    {
        public VexBatRandomMoveGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse()
        {
            return !VexBat.this.getMoveControl().hasWanted() && VexBat.this.random.nextInt(7) == 0;
        }

        public boolean canContinueToUse()
        {
            return false;
        }

        public void tick()
        {
            BlockPos blockpos = VexBat.this.getBoundOrigin();
            if (blockpos == null)
            {
                blockpos = VexBat.this.blockPosition();
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.offset(VexBat.this.random.nextInt(15) - 7, VexBat.this.random.nextInt(11) - 5, VexBat.this.random.nextInt(15) - 7);
                if (VexBat.this.level.isEmptyBlock(blockpos1))
                {
                    VexBat.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (VexBat.this.getTarget() == null)
                    {
                        VexBat.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }
}