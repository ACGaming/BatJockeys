package mod.acgaming.jockeys.entity;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.config.JockeysConfig;
import mod.acgaming.jockeys.util.JockeysHelper;

public class SkeletonBat extends EntityMob
{
    public static final DataParameter<Byte> SKELETON_BAT_FLAGS = EntityDataManager.createKey(SkeletonBat.class, DataSerializers.BYTE);
    public EntityLiving rider;

    public SkeletonBat(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.5F);
        this.moveHelper = new AIMoveControl(this);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.setNoGravity(true);

        if (this.world.isRemote)
        {
            if (this.world.rand.nextInt(20) == 0)
            {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_BAT_LOOP, SoundCategory.HOSTILE, 0.4F + this.world.rand.nextFloat() * 0.05F, 0.95F + this.world.rand.nextFloat() * 0.05F, false);
            }
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 1.5F, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(0.2D, -0.01D, 0.2D), EntitySelectors.getTeamCollisionPredicate(this));

        if (!list.isEmpty())
        {
            for (Entity entity : list)
            {
                if (!entity.isPassenger(this))
                {
                    if (!this.world.isRemote && !entity.isRiding() && entity instanceof EntityMob) entity.startRiding(this);
                    else this.applyEntityCollision(entity);
                }
            }
        }

        if (Jockeys.isSpookySeason(this.world) && this.rand.nextInt(1000) == 0 && !JockeysHelper.dropList.isEmpty())
        {
            if (this.world.isRemote)
            {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_BURP, this.getSoundCategory(), 0.5F + this.rand.nextFloat() * 0.05F, 0.95F + this.rand.nextFloat() * 0.05F, false);
            }
            else
            {
                this.dropItemWithOffset(JockeysHelper.getRandomHalloweenDrop(this.world), 1, 0.5F);
            }
        }
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_BAT_HURT;
    }

    @Override
    public SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_BAT_DEATH;
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return super.getCanSpawnHere() && this.world.canSeeSky(this.getPosition());
    }

    @Override
    public void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(JockeysConfig.SKELETON_BAT_SETTINGS.maxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(JockeysConfig.SKELETON_BAT_SETTINGS.followRange);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(JockeysConfig.SKELETON_BAT_SETTINGS.attackDamage);
    }

    @Override
    public void fall(float distance, float damageMultiplier)
    {
    }

    @Override
    public float getSoundVolume()
    {
        return 0.1F;
    }

    @Override
    public float getSoundPitch()
    {
        return super.getSoundPitch() * 0.6F;
    }

    public boolean isCharging()
    {
        return this.getSkeletonBatFlag(1);
    }

    public void setCharging(boolean charging)
    {
        this.setSkeletonBatFlag(1, charging);
    }

    @Override
    public void move(MoverType type, double x, double y, double z)
    {
        super.move(type, x, y, z);
        this.doBlockCollisions();
    }

    @Override
    public double getMountedYOffset()
    {
        return this.height * 1.1D;
    }

    @Override
    public void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new AIChargeAttack());
        this.tasks.addTask(8, new AIMoveRandom());
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F, 1.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, SkeletonBat.class));
        this.targetTasks.addTask(2, new AICopyOwnerTarget(this));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(SKELETON_BAT_FLAGS, (byte) 0);
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound()
    {
        return this.rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
    }

    @Override
    @Nullable
    public ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_BAT;
    }

    @Override
    public void updateAITasks()
    {
        super.updateAITasks();
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingData)
    {
        livingData = super.onInitialSpawn(difficulty, livingData);

        EntitySkeleton skeleton = new EntitySkeleton(this.world);
        skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        skeleton.onInitialSpawn(difficulty, null);
        skeleton.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(JockeysConfig.SKELETON_BAT_SETTINGS.followRange);
        if (Jockeys.isSpookySeason(this.world)) skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Blocks.LIT_PUMPKIN));
        else skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(JockeysHelper.getItemValueFromName(JockeysConfig.SKELETON_BAT_SETTINGS.jockeyHead)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(JockeysHelper.getItemValueFromName(JockeysConfig.SKELETON_BAT_SETTINGS.jockeyChest)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(JockeysHelper.getItemValueFromName(JockeysConfig.SKELETON_BAT_SETTINGS.jockeyLegs)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(JockeysHelper.getItemValueFromName(JockeysConfig.SKELETON_BAT_SETTINGS.jockeyFeet)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(JockeysHelper.getItemValueFromName(JockeysConfig.SKELETON_BAT_SETTINGS.jockeyItemMainhand)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(JockeysHelper.getItemValueFromName(JockeysConfig.SKELETON_BAT_SETTINGS.jockeyItemOffhand)));
        this.world.spawnEntity(skeleton);
        skeleton.startRiding(this);
        this.rider = skeleton;

        return livingData;
    }

    @Override
    public boolean canPassengerSteer()
    {
        return false;
    }

    public boolean getSkeletonBatFlag(int mask)
    {
        int i = this.dataManager.get(SKELETON_BAT_FLAGS);
        return (i & mask) != 0;
    }

    public void setSkeletonBatFlag(int mask, boolean value)
    {
        int i = this.dataManager.get(SKELETON_BAT_FLAGS);

        if (value)
        {
            i = i | mask;
        }
        else
        {
            i = i & ~mask;
        }

        this.dataManager.set(SKELETON_BAT_FLAGS, (byte) (i & 255));
    }

    class AIChargeAttack extends EntityAIBase
    {
        public AIChargeAttack()
        {
            this.setMutexBits(1);
        }

        public boolean shouldExecute()
        {
            if (SkeletonBat.this.getAttackTarget() != null && !SkeletonBat.this.getMoveHelper().isUpdating() && SkeletonBat.this.rand.nextInt(7) == 0)
            {
                return SkeletonBat.this.getDistanceSq(SkeletonBat.this.getAttackTarget()) > 8.0D;
            }
            else
            {
                return false;
            }
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return SkeletonBat.this.getMoveHelper().isUpdating() && SkeletonBat.this.isCharging() && SkeletonBat.this.getAttackTarget() != null && SkeletonBat.this.getAttackTarget().isEntityAlive();
        }

        @Override
        public void startExecuting()
        {
            EntityLivingBase entitylivingbase = SkeletonBat.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
            SkeletonBat.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 3.0D);
            SkeletonBat.this.setCharging(true);
            SkeletonBat.this.playSound(SoundEvents.ENTITY_BAT_TAKEOFF, 1.0F, SkeletonBat.this.getSoundPitch());
        }

        @Override
        public void resetTask()
        {
            SkeletonBat.this.setCharging(false);
        }

        @Override
        public void updateTask()
        {
            EntityLivingBase entitylivingbase = SkeletonBat.this.getAttackTarget();

            if (SkeletonBat.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox()))
            {
                SkeletonBat.this.attackEntityAsMob(entitylivingbase);
                SkeletonBat.this.setCharging(false);
            }
            else
            {
                double distanceSq = SkeletonBat.this.getDistanceSq(entitylivingbase);

                if (distanceSq < SkeletonBat.this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue())
                {
                    Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                    SkeletonBat.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 2.0D);
                }
            }
        }
    }

    class AIMoveControl extends EntityMoveHelper
    {
        public AIMoveControl(SkeletonBat skeletonBat)
        {
            super(skeletonBat);
        }

        @Override
        public void onUpdateMoveHelper()
        {
            if (this.action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = this.posX - SkeletonBat.this.posX;
                double d1 = this.posY - SkeletonBat.this.posY;
                double d2 = this.posZ - SkeletonBat.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = MathHelper.sqrt(d3);

                if (d3 < SkeletonBat.this.getEntityBoundingBox().getAverageEdgeLength())
                {
                    this.action = EntityMoveHelper.Action.WAIT;
                    SkeletonBat.this.motionX *= 0.5D;
                    SkeletonBat.this.motionY *= 0.5D;
                    SkeletonBat.this.motionZ *= 0.5D;
                }
                else
                {
                    SkeletonBat.this.motionX += d0 / d3 * 0.05D * this.speed;
                    SkeletonBat.this.motionY += d1 / d3 * 0.05D * this.speed;
                    SkeletonBat.this.motionZ += d2 / d3 * 0.05D * this.speed;

                    if (SkeletonBat.this.getAttackTarget() == null)
                    {
                        SkeletonBat.this.rotationYaw = -((float) MathHelper.atan2(SkeletonBat.this.motionX, SkeletonBat.this.motionZ)) * (180F / (float) Math.PI);
                    }
                    else
                    {
                        double d4 = SkeletonBat.this.getAttackTarget().posX - SkeletonBat.this.posX;
                        double d5 = SkeletonBat.this.getAttackTarget().posZ - SkeletonBat.this.posZ;
                        SkeletonBat.this.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
                    }
                    SkeletonBat.this.renderYawOffset = SkeletonBat.this.rotationYaw;
                }
            }
        }
    }

    class AIMoveRandom extends EntityAIBase
    {
        public AIMoveRandom()
        {
            this.setMutexBits(1);
        }

        public boolean shouldExecute()
        {
            return !SkeletonBat.this.getMoveHelper().isUpdating() && SkeletonBat.this.rand.nextInt(4) == 0;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return false;
        }

        @Override
        public void updateTask()
        {
            BlockPos blockpos = new BlockPos(SkeletonBat.this);
            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(SkeletonBat.this.rand.nextInt(30) - 7, SkeletonBat.this.rand.nextInt(11) - 5, SkeletonBat.this.rand.nextInt(30) - 7);
                if (SkeletonBat.this.world.isAirBlock(blockpos1))
                {
                    SkeletonBat.this.moveHelper.setMoveTo(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 1.0D);
                    if (SkeletonBat.this.getAttackTarget() == null)
                    {
                        SkeletonBat.this.getLookHelper().setLookPosition(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }

    class AICopyOwnerTarget extends EntityAITarget
    {
        public AICopyOwnerTarget(EntityCreature creature)
        {
            super(creature, false);
        }

        public boolean shouldExecute()
        {
            return SkeletonBat.this.rider != null && SkeletonBat.this.rider.getAttackTarget() != null && this.isSuitableTarget(SkeletonBat.this.rider.getAttackTarget(), false);
        }

        @Override
        public void startExecuting()
        {
            SkeletonBat.this.setAttackTarget(SkeletonBat.this.rider.getAttackTarget());
            super.startExecuting();
        }
    }
}