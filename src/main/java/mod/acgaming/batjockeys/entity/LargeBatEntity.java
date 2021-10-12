package mod.acgaming.batjockeys.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LargeBatEntity extends FlyingEntity implements IMob
{
    public static boolean canSpawn(EntityType<LargeBatEntity> largebatIn, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn)
    {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && MonsterEntity.isDarkEnoughToSpawn(worldIn, pos, randomIn) && checkMobSpawnRules(largebatIn, worldIn, reason, pos, randomIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes()
    {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.16D).add(Attributes.FOLLOW_RANGE, 100.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    private Vector3d moveTargetPoint = Vector3d.ZERO;
    private BlockPos anchorPoint = BlockPos.ZERO;
    private LargeBatEntity.AttackPhase attackPhase = LargeBatEntity.AttackPhase.SURROUND;

    public LargeBatEntity(EntityType<? extends LargeBatEntity> p_i50200_1_, World p_i50200_2_)
    {
        super(p_i50200_1_, p_i50200_2_);
        this.xpReward = 5;
        this.moveControl = new LargeBatEntity.MoveHelperController(this);
        this.lookControl = new LookHelperController(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double p_70112_1_)
    {
        return true;
    }

    public SoundCategory getSoundSource()
    {
        return SoundCategory.HOSTILE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.BAT_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BAT_DEATH;
    }

    public CreatureAttribute getMobType()
    {
        return CreatureAttribute.UNDEAD;
    }

    protected float getSoundVolume()
    {
        return 0.1F;
    }

    protected float getVoicePitch()
    {
        return super.getVoicePitch() * 0.95F;
    }

    public void onSyncedDataUpdated(DataParameter<?> p_184206_1_)
    {
        super.onSyncedDataUpdated(p_184206_1_);
    }

    public EntitySize getDimensions(Pose p_213305_1_)
    {
        EntitySize entitysize = super.getDimensions(p_213305_1_);
        float f = (entitysize.width + 0.2F) / entitysize.width;
        return entitysize.scale(f);
    }

    protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_)
    {
        return p_213348_2_.height * 0.35F;
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new LargeBatEntity.PickAttackGoal());
        this.goalSelector.addGoal(2, new LargeBatEntity.SweepAttackGoal());
        this.goalSelector.addGoal(3, new LargeBatEntity.OrbitPointGoal());
        this.targetSelector.addGoal(1, new LargeBatEntity.AttackPlayerGoal());
    }

    protected BodyController createBodyControl()
    {
        return new LargeBatEntity.BodyHelperController(this);
    }

    public boolean canAttackType(EntityType<?> p_213358_1_)
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

    public void addAdditionalSaveData(CompoundNBT p_213281_1_)
    {
        super.addAdditionalSaveData(p_213281_1_);
        p_213281_1_.putInt("AX", this.anchorPoint.getX());
        p_213281_1_.putInt("AY", this.anchorPoint.getY());
        p_213281_1_.putInt("AZ", this.anchorPoint.getZ());
    }

    public void readAdditionalSaveData(CompoundNBT p_70037_1_)
    {
        super.readAdditionalSaveData(p_70037_1_);
        if (p_70037_1_.contains("AX"))
        {
            this.anchorPoint = new BlockPos(p_70037_1_.getInt("AX"), p_70037_1_.getInt("AY"), p_70037_1_.getInt("AZ"));
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
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        this.anchorPoint = this.blockPosition().above(5);
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (level.getRandom().nextInt(100) == 0)
        {
            WitherSkeletonEntity skeletonentity = EntityType.WITHER_SKELETON.create(this.level);
            if (skeletonentity != null)
            {
                skeletonentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
                skeletonentity.finalizeSpawn(worldIn, difficultyIn, reason, null, null);
                skeletonentity.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
                skeletonentity.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.BONE));
                skeletonentity.startRiding(this);
            }
        }
        else
        {
            SkeletonEntity skeletonentity = EntityType.SKELETON.create(this.level);
            if (skeletonentity != null)
            {
                skeletonentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
                skeletonentity.finalizeSpawn(worldIn, difficultyIn, reason, null, null);
                skeletonentity.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
                skeletonentity.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.BONE));
                skeletonentity.startRiding(this);
            }
        }
        return spawnDataIn;
    }

    enum AttackPhase
    {
        SURROUND,
        ATTACK
    }

    static class LookHelperController extends LookController
    {
        public LookHelperController(MobEntity p_i48802_2_)
        {
            super(p_i48802_2_);
        }

        public void tick()
        {
        }
    }

    class AttackPlayerGoal extends Goal
    {
        private final EntityPredicate attackTargeting = (new EntityPredicate()).range(64.0D);
        private int nextScanTick = 20;

        private AttackPlayerGoal()
        {
        }

        public boolean canUse()
        {
            if (this.nextScanTick > 0)
            {
                --this.nextScanTick;
            }
            else
            {
                this.nextScanTick = 60;
                List<PlayerEntity> list = LargeBatEntity.this.level.getNearbyPlayers(this.attackTargeting, LargeBatEntity.this, LargeBatEntity.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty())
                {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for (PlayerEntity playerentity : list)
                    {
                        if (LargeBatEntity.this.canAttack(playerentity, EntityPredicate.DEFAULT))
                        {
                            LargeBatEntity.this.setTarget(playerentity);
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingentity = LargeBatEntity.this.getTarget();
            return livingentity != null && LargeBatEntity.this.canAttack(livingentity, EntityPredicate.DEFAULT);
        }
    }

    class BodyHelperController extends BodyController
    {
        public BodyHelperController(MobEntity p_i49925_2_)
        {
            super(p_i49925_2_);
        }

        public void clientTick()
        {
            LargeBatEntity.this.yHeadRot = LargeBatEntity.this.yBodyRot;
            LargeBatEntity.this.yBodyRot = LargeBatEntity.this.yRot;
        }
    }

    abstract class MoveGoal extends Goal
    {
        public MoveGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget()
        {
            return LargeBatEntity.this.moveTargetPoint.distanceToSqr(LargeBatEntity.this.getX(), LargeBatEntity.this.getY(), LargeBatEntity.this.getZ()) < 4.0D;
        }
    }

    class MoveHelperController extends MovementController
    {
        private float speed = 0.1F;

        public MoveHelperController(MobEntity p_i48801_2_)
        {
            super(p_i48801_2_);
        }

        public void tick()
        {
            if (LargeBatEntity.this.horizontalCollision)
            {
                LargeBatEntity.this.yRot += 180.0F;
                this.speed = 0.1F;
            }

            float f = (float) (LargeBatEntity.this.moveTargetPoint.x - LargeBatEntity.this.getX());
            float f1 = (float) (LargeBatEntity.this.moveTargetPoint.y - LargeBatEntity.this.getY());
            float f2 = (float) (LargeBatEntity.this.moveTargetPoint.z - LargeBatEntity.this.getZ());
            double d0 = MathHelper.sqrt(f * f + f2 * f2);
            double d1 = 1.0D - (double) MathHelper.abs(f1 * 0.7F) / d0;
            f = (float) ((double) f * d1);
            f2 = (float) ((double) f2 * d1);
            d0 = MathHelper.sqrt(f * f + f2 * f2);
            double d2 = MathHelper.sqrt(f * f + f2 * f2 + f1 * f1);
            float f3 = LargeBatEntity.this.yRot;
            float f4 = (float) MathHelper.atan2(f2, f);
            float f5 = MathHelper.wrapDegrees(LargeBatEntity.this.yRot + 90.0F);
            float f6 = MathHelper.wrapDegrees(f4 * (180F / (float) Math.PI));
            LargeBatEntity.this.yRot = MathHelper.approachDegrees(f5, f6, 4.0F) - 90.0F;
            LargeBatEntity.this.yBodyRot = LargeBatEntity.this.yRot;
            if (MathHelper.degreesDifferenceAbs(f3, LargeBatEntity.this.yRot) < 3.0F)
            {
                this.speed = MathHelper.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
            }
            else
            {
                this.speed = MathHelper.approach(this.speed, 0.2F, 0.025F);
            }

            float f7 = (float) (-(MathHelper.atan2(-f1, d0) * (double) (180F / (float) Math.PI)));
            LargeBatEntity.this.xRot = f7;
            float f8 = LargeBatEntity.this.yRot + 90.0F;
            double d3 = (double) (this.speed * MathHelper.cos(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f / d2);
            double d4 = (double) (this.speed * MathHelper.sin(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f2 / d2);
            double d5 = (double) (this.speed * MathHelper.sin(f7 * ((float) Math.PI / 180F))) * Math.abs((double) f1 / d2);
            Vector3d vector3d = LargeBatEntity.this.getDeltaMovement();
            LargeBatEntity.this.setDeltaMovement(vector3d.add((new Vector3d(d3, d5, d4)).subtract(vector3d).scale(0.2D)));
        }
    }

    class OrbitPointGoal extends LargeBatEntity.MoveGoal
    {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        private OrbitPointGoal()
        {
        }

        public boolean canUse()
        {
            return LargeBatEntity.this.getTarget() == null || LargeBatEntity.this.attackPhase == LargeBatEntity.AttackPhase.SURROUND;
        }

        public void start()
        {
            this.distance = 5.0F + LargeBatEntity.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + LargeBatEntity.this.random.nextFloat() * 9.0F;
            this.clockwise = LargeBatEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick()
        {
            if (LargeBatEntity.this.random.nextInt(350) == 0)
            {
                this.height = -4.0F + LargeBatEntity.this.random.nextFloat() * 9.0F;
            }

            if (LargeBatEntity.this.random.nextInt(250) == 0)
            {
                ++this.distance;
                if (this.distance > 15.0F)
                {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (LargeBatEntity.this.random.nextInt(450) == 0)
            {
                this.angle = LargeBatEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget())
            {
                this.selectNext();
            }

            if (LargeBatEntity.this.moveTargetPoint.y < LargeBatEntity.this.getY() && !LargeBatEntity.this.level.isEmptyBlock(LargeBatEntity.this.blockPosition().below(1)))
            {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (LargeBatEntity.this.moveTargetPoint.y > LargeBatEntity.this.getY() && !LargeBatEntity.this.level.isEmptyBlock(LargeBatEntity.this.blockPosition().above(1)))
            {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext()
        {
            if (BlockPos.ZERO.equals(LargeBatEntity.this.anchorPoint))
            {
                LargeBatEntity.this.anchorPoint = LargeBatEntity.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);
            LargeBatEntity.this.moveTargetPoint = Vector3d.atLowerCornerOf(LargeBatEntity.this.anchorPoint).add(this.distance * MathHelper.cos(this.angle), -4.0F + this.height, this.distance * MathHelper.sin(this.angle));
        }
    }

    class PickAttackGoal extends Goal
    {
        private int nextSweepTick;

        private PickAttackGoal()
        {
        }

        public boolean canUse()
        {
            LivingEntity livingentity = LargeBatEntity.this.getTarget();
            return livingentity != null && LargeBatEntity.this.canAttack(LargeBatEntity.this.getTarget(), EntityPredicate.DEFAULT);
        }

        public void start()
        {
            this.nextSweepTick = 10;
            LargeBatEntity.this.attackPhase = LargeBatEntity.AttackPhase.SURROUND;
            this.setAnchorAboveTarget();
        }

        public void stop()
        {
            LargeBatEntity.this.anchorPoint = LargeBatEntity.this.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, LargeBatEntity.this.anchorPoint).above(10 + LargeBatEntity.this.random.nextInt(20));
        }

        public void tick()
        {
            if (LargeBatEntity.this.attackPhase == LargeBatEntity.AttackPhase.SURROUND)
            {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0)
                {
                    LargeBatEntity.this.attackPhase = LargeBatEntity.AttackPhase.ATTACK;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = (8 + LargeBatEntity.this.random.nextInt(4)) * 20;
                    LargeBatEntity.this.playSound(SoundEvents.BAT_TAKEOFF, 5.0F, 0.95F + LargeBatEntity.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget()
        {
            LargeBatEntity.this.anchorPoint = LargeBatEntity.this.getTarget().blockPosition().above(20 + LargeBatEntity.this.random.nextInt(20));
            if (LargeBatEntity.this.anchorPoint.getY() < LargeBatEntity.this.level.getSeaLevel())
            {
                LargeBatEntity.this.anchorPoint = new BlockPos(LargeBatEntity.this.anchorPoint.getX(), LargeBatEntity.this.level.getSeaLevel() + 1, LargeBatEntity.this.anchorPoint.getZ());
            }

        }
    }

    class SweepAttackGoal extends LargeBatEntity.MoveGoal
    {
        private SweepAttackGoal()
        {
        }

        public boolean canUse()
        {
            return LargeBatEntity.this.getTarget() != null && LargeBatEntity.this.attackPhase == LargeBatEntity.AttackPhase.ATTACK;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingentity = LargeBatEntity.this.getTarget();
            if (livingentity == null)
            {
                return false;
            }
            else if (!livingentity.isAlive())
            {
                return false;
            }
            else if (!(livingentity instanceof PlayerEntity) || !livingentity.isSpectator() && !((PlayerEntity) livingentity).isCreative())
            {
                if (!this.canUse())
                {
                    return false;
                }
                else
                {
                    if (LargeBatEntity.this.tickCount % 20 == 0)
                    {
                        List<CatEntity> list = LargeBatEntity.this.level.getEntitiesOfClass(CatEntity.class, LargeBatEntity.this.getBoundingBox().inflate(16.0D), EntityPredicates.ENTITY_STILL_ALIVE);
                        if (!list.isEmpty())
                        {
                            for (CatEntity catentity : list)
                            {
                                catentity.hiss();
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
            LargeBatEntity.this.setTarget(null);
            LargeBatEntity.this.attackPhase = LargeBatEntity.AttackPhase.SURROUND;
        }

        public void tick()
        {
            LivingEntity livingentity = LargeBatEntity.this.getTarget();
            LargeBatEntity.this.moveTargetPoint = new Vector3d(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
            if (LargeBatEntity.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox()))
            {
                LargeBatEntity.this.doHurtTarget(livingentity);
                LargeBatEntity.this.attackPhase = LargeBatEntity.AttackPhase.SURROUND;
                if (!LargeBatEntity.this.isSilent())
                {
                    LargeBatEntity.this.level.levelEvent(1039, LargeBatEntity.this.blockPosition(), 0);
                }
            }
            else if (LargeBatEntity.this.horizontalCollision || LargeBatEntity.this.hurtTime > 0)
            {
                LargeBatEntity.this.attackPhase = LargeBatEntity.AttackPhase.SURROUND;
            }
        }
    }
}