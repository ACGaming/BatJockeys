package mod.acgaming.batjockeys.entity;

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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class LargeBat extends FlyingMob implements Enemy
{
    public static boolean checkLargeBatSpawnRules(EntityType<LargeBat> largebat, ServerLevelAccessor level, MobSpawnType spawntype, BlockPos pos, Random random)
    {
        return level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && checkMobSpawnRules(largebat, level, spawntype, pos, random);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.16D).add(Attributes.FOLLOW_RANGE, 100.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    LargeBat.AttackPhase attackPhase = LargeBat.AttackPhase.SURROUND;

    public LargeBat(EntityType<? extends LargeBat> typeIn, Level levelIn)
    {
        super(typeIn, levelIn);
        this.xpReward = 5;
        this.moveControl = new LargeBatMoveControl(this);
        this.lookControl = new LargeBatLookControl(this);
    }

    public boolean shouldRenderAtSqrDistance(double p_33107_)
    {
        return true;
    }

    public SoundSource getSoundSource()
    {
        return SoundSource.HOSTILE;
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new LargeBatAttackStrategyGoal());
        this.goalSelector.addGoal(2, new LargeBatSweepAttackGoal());
        this.goalSelector.addGoal(3, new LargeBatCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new LargeBatAttackPlayerTargetGoal());
    }

    protected BodyRotationControl createBodyControl()
    {
        return new LargeBatBodyRotationControl(this);
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
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_LOOP, this.getSoundSource(), 0.5F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }
            this.level.addParticle(ParticleTypes.SMOKE, this.getX() - 1.0F, this.getY() + 1.0F, this.getZ(), 0.0D, 0.0D, 0.0D);
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
        if (level.getRandom().nextInt(100) == 0)
        {
            WitherSkeleton skeleton = EntityType.WITHER_SKELETON.create(this.level);
            skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            skeleton.finalizeSpawn(level, difficulty, spawntype, null, null);
            skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
            skeleton.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BONE));
            skeleton.startRiding(this);
        }
        else
        {
            Skeleton skeleton = EntityType.SKELETON.create(this.level);
            skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            skeleton.finalizeSpawn(level, difficulty, spawntype, null, null);
            skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
            skeleton.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BONE));
            skeleton.startRiding(this);
        }
        return spawndata;
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

    enum AttackPhase
    {
        SURROUND,
        ATTACK
    }

    static class LargeBatLookControl extends LookControl
    {
        public LargeBatLookControl(Mob p_33235_)
        {
            super(p_33235_);
        }

        public void tick()
        {
        }
    }

    class LargeBatAttackPlayerTargetGoal extends Goal
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
                List<Player> list = LargeBat.this.level.getNearbyPlayers(this.attackTargeting, LargeBat.this, LargeBat.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty())
                {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for (Player player : list)
                    {
                        if (LargeBat.this.canAttack(player, TargetingConditions.DEFAULT))
                        {
                            LargeBat.this.setTarget(player);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingentity = LargeBat.this.getTarget();
            return livingentity != null && LargeBat.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }
    }

    class LargeBatAttackStrategyGoal extends Goal
    {
        private int nextSweepTick;

        public boolean canUse()
        {
            LivingEntity livingentity = LargeBat.this.getTarget();
            return livingentity != null && LargeBat.this.canAttack(LargeBat.this.getTarget(), TargetingConditions.DEFAULT);
        }

        public void start()
        {
            this.nextSweepTick = 10;
            LargeBat.this.attackPhase = LargeBat.AttackPhase.SURROUND;
            this.setAnchorAboveTarget();
        }

        public void stop()
        {
            LargeBat.this.anchorPoint = LargeBat.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, LargeBat.this.anchorPoint).above(10 + LargeBat.this.random.nextInt(20));
        }

        public void tick()
        {
            if (LargeBat.this.attackPhase == LargeBat.AttackPhase.SURROUND)
            {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0)
                {
                    LargeBat.this.attackPhase = LargeBat.AttackPhase.ATTACK;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = (8 + LargeBat.this.random.nextInt(4)) * 20;
                    LargeBat.this.playSound(SoundEvents.BAT_TAKEOFF, 5.0F, 0.95F + LargeBat.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget()
        {
            LargeBat.this.anchorPoint = LargeBat.this.getTarget().blockPosition().above(20 + LargeBat.this.random.nextInt(20));
            if (LargeBat.this.anchorPoint.getY() < LargeBat.this.level.getSeaLevel())
            {
                LargeBat.this.anchorPoint = new BlockPos(LargeBat.this.anchorPoint.getX(), LargeBat.this.level.getSeaLevel() + 1, LargeBat.this.anchorPoint.getZ());
            }
        }
    }

    class LargeBatBodyRotationControl extends BodyRotationControl
    {
        public LargeBatBodyRotationControl(Mob p_33216_)
        {
            super(p_33216_);
        }

        public void clientTick()
        {
            LargeBat.this.yHeadRot = LargeBat.this.yBodyRot;
            LargeBat.this.yBodyRot = LargeBat.this.getYRot();
        }
    }

    class LargeBatCircleAroundAnchorGoal extends LargeBatMoveTargetGoal
    {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse()
        {
            return LargeBat.this.getTarget() == null || LargeBat.this.attackPhase == LargeBat.AttackPhase.SURROUND;
        }

        public void start()
        {
            this.distance = 5.0F + LargeBat.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + LargeBat.this.random.nextFloat() * 9.0F;
            this.clockwise = LargeBat.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick()
        {
            if (LargeBat.this.random.nextInt(350) == 0)
            {
                this.height = -4.0F + LargeBat.this.random.nextFloat() * 9.0F;
            }

            if (LargeBat.this.random.nextInt(250) == 0)
            {
                ++this.distance;
                if (this.distance > 15.0F)
                {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (LargeBat.this.random.nextInt(450) == 0)
            {
                this.angle = LargeBat.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget())
            {
                this.selectNext();
            }

            if (LargeBat.this.moveTargetPoint.y < LargeBat.this.getY() && !LargeBat.this.level.isEmptyBlock(LargeBat.this.blockPosition().below(1)))
            {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (LargeBat.this.moveTargetPoint.y > LargeBat.this.getY() && !LargeBat.this.level.isEmptyBlock(LargeBat.this.blockPosition().above(1)))
            {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext()
        {
            if (BlockPos.ZERO.equals(LargeBat.this.anchorPoint))
            {
                LargeBat.this.anchorPoint = LargeBat.this.blockPosition();
            }
            this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);
            LargeBat.this.moveTargetPoint = Vec3.atLowerCornerOf(LargeBat.this.anchorPoint).add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    class LargeBatMoveControl extends MoveControl
    {
        private float speed = 0.1F;

        public LargeBatMoveControl(Mob p_33241_)
        {
            super(p_33241_);
        }

        public void tick()
        {
            if (LargeBat.this.horizontalCollision)
            {
                LargeBat.this.setYRot(LargeBat.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }
            float f = (float) (LargeBat.this.moveTargetPoint.x - LargeBat.this.getX());
            float f1 = (float) (LargeBat.this.moveTargetPoint.y - LargeBat.this.getY());
            float f2 = (float) (LargeBat.this.moveTargetPoint.z - LargeBat.this.getZ());
            double d0 = Mth.sqrt(f * f + f2 * f2);
            if (Math.abs(d0) > (double) 1.0E-5F)
            {
                double d1 = 1.0D - (double) Mth.abs(f1 * 0.7F) / d0;
                f = (float) ((double) f * d1);
                f2 = (float) ((double) f2 * d1);
                d0 = Mth.sqrt(f * f + f2 * f2);
                double d2 = Mth.sqrt(f * f + f2 * f2 + f1 * f1);
                float f3 = LargeBat.this.getYRot();
                float f4 = (float) Mth.atan2(f2, f);
                float f5 = Mth.wrapDegrees(LargeBat.this.getYRot() + 90.0F);
                float f6 = Mth.wrapDegrees(f4 * (180F / (float) Math.PI));
                LargeBat.this.setYRot(Mth.approachDegrees(f5, f6, 4.0F) - 90.0F);
                LargeBat.this.yBodyRot = LargeBat.this.getYRot();
                if (Mth.degreesDifferenceAbs(f3, LargeBat.this.getYRot()) < 3.0F)
                {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                }
                else
                {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }
                float f7 = (float) (-(Mth.atan2(-f1, d0) * (double) (180F / (float) Math.PI)));
                LargeBat.this.setXRot(f7);
                float f8 = LargeBat.this.getYRot() + 90.0F;
                double d3 = (double) (this.speed * Mth.cos(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f / d2);
                double d4 = (double) (this.speed * Mth.sin(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f2 / d2);
                double d5 = (double) (this.speed * Mth.sin(f7 * ((float) Math.PI / 180F))) * Math.abs((double) f1 / d2);
                Vec3 vec3 = LargeBat.this.getDeltaMovement();
                LargeBat.this.setDeltaMovement(vec3.add((new Vec3(d3, d5, d4)).subtract(vec3).scale(0.2D)));
            }
        }
    }

    abstract class LargeBatMoveTargetGoal extends Goal
    {
        public LargeBatMoveTargetGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget()
        {
            return LargeBat.this.moveTargetPoint.distanceToSqr(LargeBat.this.getX(), LargeBat.this.getY(), LargeBat.this.getZ()) < 4.0D;
        }
    }

    class LargeBatSweepAttackGoal extends LargeBatMoveTargetGoal
    {
        public boolean canUse()
        {
            return LargeBat.this.getTarget() != null && LargeBat.this.attackPhase == LargeBat.AttackPhase.ATTACK;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingentity = LargeBat.this.getTarget();
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
                    if (LargeBat.this.tickCount % 20 == 0)
                    {
                        List<Cat> list = LargeBat.this.level.getEntitiesOfClass(Cat.class, LargeBat.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);
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
            LargeBat.this.setTarget(null);
            LargeBat.this.attackPhase = LargeBat.AttackPhase.SURROUND;
        }

        public void tick()
        {
            LivingEntity livingentity = LargeBat.this.getTarget();
            LargeBat.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
            if (LargeBat.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox()))
            {
                LargeBat.this.doHurtTarget(livingentity);
                LargeBat.this.attackPhase = LargeBat.AttackPhase.SURROUND;
                if (!LargeBat.this.isSilent())
                {
                    LargeBat.this.level.levelEvent(1039, LargeBat.this.blockPosition(), 0);
                }
            }
            else if (LargeBat.this.horizontalCollision || LargeBat.this.hurtTime > 0)
            {
                LargeBat.this.attackPhase = LargeBat.AttackPhase.SURROUND;
            }
        }
    }
}