package mod.acgaming.jockeys.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.config.ConfigHandler;
import mod.acgaming.jockeys.config.RegistryHelper;

public class SkeletonBat extends FlyingMob implements Enemy
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public static boolean checkSpawnRules(EntityType<SkeletonBat> p_32735_, LevelAccessor p_32736_, MobSpawnType p_32737_, BlockPos p_32738_, Random p_32739_)
    {
        return p_32736_.getDifficulty() != Difficulty.PEACEFUL && p_32739_.nextInt(20) == 0 && checkMobSpawnRules(p_32735_, p_32736_, p_32737_, p_32738_, p_32739_);
    }

    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    SkeletonBat.AttackPhase attackPhase = SkeletonBat.AttackPhase.SURROUND;

    public SkeletonBat(EntityType<? extends SkeletonBat> typeIn, Level levelIn)
    {
        super(typeIn, levelIn);
        this.moveControl = new SkeletonBatMoveControl(this);
        this.lookControl = new SkeletonBatLookControl(this);
        this.xpReward = 5;
        if (Jockeys.isHalloween())
        {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
        }
    }

    public boolean shouldRenderAtSqrDistance(double p_33107_)
    {
        return true;
    }

    public SoundSource getSoundSource()
    {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getHurtSound(DamageSource p_27451_)
    {
        return SoundEvents.BAT_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BAT_DEATH;
    }

    public MobType getMobType()
    {
        return MobType.UNDEAD;
    }

    protected float getSoundVolume()
    {
        return 0.1F;
    }

    public float getVoicePitch()
    {
        return super.getVoicePitch() * 0.95F;
    }

    public EntityDimensions getDimensions(Pose p_33113_)
    {
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + 0.2F) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    protected float getStandingEyeHeight(Pose p_33136_, EntityDimensions p_33137_)
    {
        return p_33137_.height * 0.35F;
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new SkeletonBatAttackStrategyGoal());
        this.goalSelector.addGoal(2, new SkeletonBatSweepAttackGoal());
        this.goalSelector.addGoal(3, new SkeletonBatCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new SkeletonBatAttackPlayerTargetGoal());
    }

    protected BodyRotationControl createBodyControl()
    {
        return new SkeletonBatBodyRotationControl(this);
    }

    public boolean canAttackType(EntityType<?> p_33111_)
    {
        return true;
    }

    public void tick()
    {
        super.tick();
        if (this.level.isClientSide)
        {
            if (this.random.nextInt(20) == 0)
            {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_LOOP, this.getSoundSource(), 0.4F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }
            this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0F, this.getZ(), 0.0D, 0.0D, 0.0D);
        }

        if (Jockeys.trickortreat)
        {
            if (this.random.nextInt(10000) == 0)
            {
                if (this.level.isClientSide)
                {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, this.getSoundSource(), 0.5F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
                }
                if (Jockeys.isHalloween())
                {
                    this.spawnAtLocation(RegistryHelper.getItemValueFromName("trickortreat:skeleton_goodie_bag"));
                }
            }
        }
    }

    @Nullable
    public SoundEvent getAmbientSound()
    {
        return this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
    }

    public void addAdditionalSaveData(CompoundTag p_33141_)
    {
        super.addAdditionalSaveData(p_33141_);
        p_33141_.putInt("AX", this.anchorPoint.getX());
        p_33141_.putInt("AY", this.anchorPoint.getY());
        p_33141_.putInt("AZ", this.anchorPoint.getZ());
    }

    public void readAdditionalSaveData(CompoundTag p_33132_)
    {
        super.readAdditionalSaveData(p_33132_);
        if (p_33132_.contains("AX"))
        {
            this.anchorPoint = new BlockPos(p_33132_.getInt("AX"), p_33132_.getInt("AY"), p_33132_.getInt("AZ"));
        }
    }

    public void aiStep()
    {
        if (this.isAlive() && this.isSunBurnTick())
        {
            this.setSecondsOnFire(8);
        }
        super.aiStep();
    }

    protected boolean shouldDespawnInPeaceful()
    {
        return true;
    }

    protected void customServerAiStep()
    {
        super.customServerAiStep();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawntype, @Nullable SpawnGroupData spawndata, @Nullable CompoundTag compoundtag)
    {
        this.anchorPoint = this.blockPosition().above(5);
        spawndata = super.finalizeSpawn(level, difficulty, spawntype, spawndata, compoundtag);
        Skeleton skeleton = EntityType.SKELETON.create(this.level);
        if (skeleton != null)
        {
            skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            skeleton.finalizeSpawn(level, difficulty, spawntype, null, null);

            if (Jockeys.isHalloween())
            {
                skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
            }
            else
            {
                skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.SKELETON_BAT_SETTINGS.jockey_head.get())));
            }
            skeleton.setItemSlot(EquipmentSlot.CHEST, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.SKELETON_BAT_SETTINGS.jockey_chest.get())));
            skeleton.setItemSlot(EquipmentSlot.LEGS, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.SKELETON_BAT_SETTINGS.jockey_legs.get())));
            skeleton.setItemSlot(EquipmentSlot.FEET, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.SKELETON_BAT_SETTINGS.jockey_feet.get())));
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.SKELETON_BAT_SETTINGS.jockey_item_main.get())));
            skeleton.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.SKELETON_BAT_SETTINGS.jockey_item_off.get())));

            skeleton.startRiding(this);
        }
        return spawndata;
    }

    enum AttackPhase
    {
        SURROUND,
        ATTACK
    }

    static class SkeletonBatLookControl extends LookControl
    {
        public SkeletonBatLookControl(Mob p_33235_)
        {
            super(p_33235_);
        }

        public void tick()
        {
        }
    }

    class SkeletonBatAttackPlayerTargetGoal extends Goal
    {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D);
        private int nextScanTick = 20;

        public boolean canUse()
        {
            if (this.nextScanTick > 0)
            {
                --this.nextScanTick;
            }
            else
            {
                this.nextScanTick = 60;
                List<Player> list = SkeletonBat.this.level.getNearbyPlayers(this.attackTargeting, SkeletonBat.this, SkeletonBat.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty())
                {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for (Player player : list)
                    {
                        if (SkeletonBat.this.canAttack(player, TargetingConditions.DEFAULT))
                        {
                            SkeletonBat.this.setTarget(player);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingentity = SkeletonBat.this.getTarget();
            return livingentity != null && SkeletonBat.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }
    }

    class SkeletonBatAttackStrategyGoal extends Goal
    {
        private int nextSweepTick;

        public boolean canUse()
        {
            LivingEntity livingentity = SkeletonBat.this.getTarget();
            return livingentity != null && SkeletonBat.this.canAttack(SkeletonBat.this.getTarget(), TargetingConditions.DEFAULT);
        }

        public void start()
        {
            this.nextSweepTick = 10;
            SkeletonBat.this.attackPhase = SkeletonBat.AttackPhase.SURROUND;
            this.setAnchorAboveTarget();
        }

        public void stop()
        {
            SkeletonBat.this.anchorPoint = SkeletonBat.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, SkeletonBat.this.anchorPoint).above(10 + SkeletonBat.this.random.nextInt(20));
        }

        public void tick()
        {
            if (SkeletonBat.this.attackPhase == SkeletonBat.AttackPhase.SURROUND)
            {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0)
                {
                    SkeletonBat.this.attackPhase = SkeletonBat.AttackPhase.ATTACK;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = (8 + SkeletonBat.this.random.nextInt(4)) * 20;
                    SkeletonBat.this.playSound(SoundEvents.BAT_TAKEOFF, 5.0F, 0.95F + SkeletonBat.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget()
        {
            SkeletonBat.this.anchorPoint = SkeletonBat.this.getTarget().blockPosition().above(20 + SkeletonBat.this.random.nextInt(20));
            if (SkeletonBat.this.anchorPoint.getY() < SkeletonBat.this.level.getSeaLevel())
            {
                SkeletonBat.this.anchorPoint = new BlockPos(SkeletonBat.this.anchorPoint.getX(), SkeletonBat.this.level.getSeaLevel() + 1, SkeletonBat.this.anchorPoint.getZ());
            }
        }
    }

    class SkeletonBatBodyRotationControl extends BodyRotationControl
    {
        public SkeletonBatBodyRotationControl(Mob p_33216_)
        {
            super(p_33216_);
        }

        public void clientTick()
        {
            SkeletonBat.this.yHeadRot = SkeletonBat.this.yBodyRot;
            SkeletonBat.this.yBodyRot = SkeletonBat.this.getYRot();
        }
    }

    class SkeletonBatCircleAroundAnchorGoal extends SkeletonBatMoveTargetGoal
    {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse()
        {
            return SkeletonBat.this.getTarget() == null || SkeletonBat.this.attackPhase == SkeletonBat.AttackPhase.SURROUND;
        }

        public void start()
        {
            this.distance = 5.0F + SkeletonBat.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + SkeletonBat.this.random.nextFloat() * 9.0F;
            this.clockwise = SkeletonBat.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick()
        {
            if (SkeletonBat.this.random.nextInt(350) == 0)
            {
                this.height = -4.0F + SkeletonBat.this.random.nextFloat() * 9.0F;
            }

            if (SkeletonBat.this.random.nextInt(250) == 0)
            {
                ++this.distance;
                if (this.distance > 15.0F)
                {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (SkeletonBat.this.random.nextInt(450) == 0)
            {
                this.angle = SkeletonBat.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget())
            {
                this.selectNext();
            }

            if (SkeletonBat.this.moveTargetPoint.y < SkeletonBat.this.getY() && !SkeletonBat.this.level.isEmptyBlock(SkeletonBat.this.blockPosition().below(1)))
            {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (SkeletonBat.this.moveTargetPoint.y > SkeletonBat.this.getY() && !SkeletonBat.this.level.isEmptyBlock(SkeletonBat.this.blockPosition().above(1)))
            {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }
        }

        private void selectNext()
        {
            if (BlockPos.ZERO.equals(SkeletonBat.this.anchorPoint))
            {
                SkeletonBat.this.anchorPoint = SkeletonBat.this.blockPosition();
            }
            this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);
            SkeletonBat.this.moveTargetPoint = Vec3.atLowerCornerOf(SkeletonBat.this.anchorPoint).add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    class SkeletonBatMoveControl extends MoveControl
    {
        private float speed = 0.1F;

        public SkeletonBatMoveControl(Mob p_33241_)
        {
            super(p_33241_);
        }

        public void tick()
        {
            if (SkeletonBat.this.horizontalCollision)
            {
                SkeletonBat.this.setYRot(SkeletonBat.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }
            float f = (float) (SkeletonBat.this.moveTargetPoint.x - SkeletonBat.this.getX());
            float f1 = (float) (SkeletonBat.this.moveTargetPoint.y - SkeletonBat.this.getY());
            float f2 = (float) (SkeletonBat.this.moveTargetPoint.z - SkeletonBat.this.getZ());
            double d0 = Mth.sqrt(f * f + f2 * f2);
            if (Math.abs(d0) > (double) 1.0E-5F)
            {
                double d1 = 1.0D - (double) Mth.abs(f1 * 0.7F) / d0;
                f = (float) ((double) f * d1);
                f2 = (float) ((double) f2 * d1);
                d0 = Mth.sqrt(f * f + f2 * f2);
                double d2 = Mth.sqrt(f * f + f2 * f2 + f1 * f1);
                float f3 = SkeletonBat.this.getYRot();
                float f4 = (float) Mth.atan2(f2, f);
                float f5 = Mth.wrapDegrees(SkeletonBat.this.getYRot() + 90.0F);
                float f6 = Mth.wrapDegrees(f4 * (180F / (float) Math.PI));
                SkeletonBat.this.setYRot(Mth.approachDegrees(f5, f6, 4.0F) - 90.0F);
                SkeletonBat.this.yBodyRot = SkeletonBat.this.getYRot();
                if (Mth.degreesDifferenceAbs(f3, SkeletonBat.this.getYRot()) < 3.0F)
                {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                }
                else
                {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }
                float f7 = (float) (-(Mth.atan2(-f1, d0) * (double) (180F / (float) Math.PI)));
                SkeletonBat.this.setXRot(f7);
                float f8 = SkeletonBat.this.getYRot() + 90.0F;
                double d3 = (double) (this.speed * Mth.cos(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f / d2);
                double d4 = (double) (this.speed * Mth.sin(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f2 / d2);
                double d5 = (double) (this.speed * Mth.sin(f7 * ((float) Math.PI / 180F))) * Math.abs((double) f1 / d2);
                Vec3 vec3 = SkeletonBat.this.getDeltaMovement();
                SkeletonBat.this.setDeltaMovement(vec3.add((new Vec3(d3, d5, d4)).subtract(vec3).scale(0.2D)));
            }
        }
    }

    abstract class SkeletonBatMoveTargetGoal extends Goal
    {
        public SkeletonBatMoveTargetGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget()
        {
            return SkeletonBat.this.moveTargetPoint.distanceToSqr(SkeletonBat.this.getX(), SkeletonBat.this.getY(), SkeletonBat.this.getZ()) < 4.0D;
        }
    }

    class SkeletonBatSweepAttackGoal extends SkeletonBatMoveTargetGoal
    {
        public boolean canUse()
        {
            return SkeletonBat.this.getTarget() != null && SkeletonBat.this.attackPhase == SkeletonBat.AttackPhase.ATTACK;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingentity = SkeletonBat.this.getTarget();
            if (livingentity == null)
            {
                return false;
            }
            else if (!livingentity.isAlive())
            {
                return false;
            }
            else if (!(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative())
            {
                if (!this.canUse())
                {
                    return false;
                }
                else
                {
                    if (SkeletonBat.this.tickCount % 20 == 0)
                    {
                        List<Cat> list = SkeletonBat.this.level.getEntitiesOfClass(Cat.class, SkeletonBat.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);
                        if (!list.isEmpty())
                        {
                            for (Cat cat : list)
                            {
                                cat.hiss();
                            }
                            return false;
                        }
                    }
                    return true;
                }
            }
            else
            {
                return false;
            }
        }

        public void start()
        {
        }

        public void stop()
        {
            SkeletonBat.this.setTarget(null);
            SkeletonBat.this.attackPhase = SkeletonBat.AttackPhase.SURROUND;
        }

        public void tick()
        {
            LivingEntity livingentity = SkeletonBat.this.getTarget();
            SkeletonBat.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
            if (SkeletonBat.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox()))
            {
                SkeletonBat.this.doHurtTarget(livingentity);
                SkeletonBat.this.attackPhase = SkeletonBat.AttackPhase.SURROUND;
                if (!SkeletonBat.this.isSilent())
                {
                    SkeletonBat.this.level.levelEvent(1039, SkeletonBat.this.blockPosition(), 0);
                }
            }
            else if (SkeletonBat.this.horizontalCollision || SkeletonBat.this.hurtTime > 0)
            {
                SkeletonBat.this.attackPhase = SkeletonBat.AttackPhase.SURROUND;
            }
        }
    }
}