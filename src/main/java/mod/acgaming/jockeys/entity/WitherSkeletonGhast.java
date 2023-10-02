package mod.acgaming.jockeys.entity;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.config.ConfigHandler;
import mod.acgaming.jockeys.config.RegistryHelper;

public class WitherSkeletonGhast extends EntityFlying implements IMob
{
    public static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(WitherSkeletonGhast.class, DataSerializers.BOOLEAN);

    public WitherSkeletonGhast(World worldIn)
    {
        super(worldIn);
        this.setSize(4.0F, 4.0F);
        this.isImmuneToFire = true;
        this.experienceValue = 5;
        this.moveHelper = new WitherSkeletonGhast.GhastMoveHelper(this);
    }

    @Override
    public void initEntityAI()
    {
        this.tasks.addTask(5, new WitherSkeletonGhast.AIRandomFly(this));
        this.tasks.addTask(7, new WitherSkeletonGhast.AILookAround(this));
        if (Jockeys.isSpookySeason(this.world)) this.tasks.addTask(7, new AICandyAttack(this));
        this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
    }

    @Override
    public void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.maxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.followRange);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(ATTACKING, Boolean.FALSE);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL)
        {
            this.setDead();
        }
    }

    @Override
    public SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_GHAST_AMBIENT;
    }

    @Override
    @Nullable
    public ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_GHAST;
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingData)
    {
        livingData = super.onInitialSpawn(difficulty, livingData);

        EntityWitherSkeleton skeleton = new EntityWitherSkeleton(this.world);
        skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        skeleton.onInitialSpawn(difficulty, null);
        skeleton.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.followRange);
        if (Jockeys.isSpookySeason(this.world)) skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Blocks.LIT_PUMPKIN));
        else skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockeyHead)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockeyChest)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockeyLegs)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockeyFeet)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockeyItemMainhand)));
        skeleton.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockeyItemOffhand)));
        this.world.spawnEntity(skeleton);
        skeleton.startRiding(this);

        return livingData;
    }

    @SideOnly(Side.CLIENT)
    public boolean isAttacking()
    {
        return this.dataManager.get(ATTACKING);
    }

    public void setAttacking(boolean attacking)
    {
        this.dataManager.set(ATTACKING, attacking);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (source.getImmediateSource() instanceof CandyBomb && source.getTrueSource() instanceof EntityPlayer)
        {
            super.attackEntityFrom(source, 1000.0F);
            return true;
        }
        else
        {
            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_GHAST_HURT;
    }

    @Override
    public SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_GHAST_DEATH;
    }

    @Override
    public float getSoundVolume()
    {
        return 10.0F;
    }

    @Override
    public double getMountedYOffset()
    {
        return super.getMountedYOffset() + 0.9D;
    }

    @Override
    public float getEyeHeight()
    {
        return 2.6F;
    }

    @Override
    public SoundCategory getSoundCategory()
    {
        return SoundCategory.HOSTILE;
    }

    static class AICandyAttack extends EntityAIBase
    {
        public final WitherSkeletonGhast parentEntity;
        public int attackTimer;

        public AICandyAttack(WitherSkeletonGhast ghast)
        {
            this.parentEntity = ghast;
        }

        public boolean shouldExecute()
        {
            return this.parentEntity.getAttackTarget() != null;
        }

        @Override
        public void startExecuting()
        {
            this.attackTimer = 0;
        }

        @Override
        public void resetTask()
        {
            this.parentEntity.setAttacking(false);
        }

        @Override
        public void updateTask()
        {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();


            if (entitylivingbase != null && entitylivingbase.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(entitylivingbase))
            {
                World world = this.parentEntity.world;
                ++this.attackTimer;

                if (this.attackTimer == 10)
                {
                    world.playEvent(null, 1015, new BlockPos(this.parentEntity), 0);
                }

                if (this.attackTimer == 20)
                {
                    Vec3d vec3d = this.parentEntity.getLook(1.0F);
                    double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.x * 4.0D);
                    double d3 = entitylivingbase.getEntityBoundingBox().minY + (entitylivingbase.height / 2.0F) - (0.5D + this.parentEntity.posY + (this.parentEntity.height / 2.0F));
                    double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.z * 4.0D);
                    world.playEvent(null, 1016, new BlockPos(this.parentEntity), 0);
                    CandyBomb candyBomb = new CandyBomb(world, this.parentEntity, d2, d3, d4);
                    candyBomb.posX = this.parentEntity.posX + vec3d.x * 4.0D;
                    candyBomb.posY = this.parentEntity.posY + (this.parentEntity.height / 2.0F) + 0.5D;
                    candyBomb.posZ = this.parentEntity.posZ + vec3d.z * 4.0D;
                    world.spawnEntity(candyBomb);
                    this.attackTimer = -40;
                }
            }
            else if (this.attackTimer > 0)
            {
                --this.attackTimer;
            }

            this.parentEntity.setAttacking(this.attackTimer > 10);
        }
    }

    static class AILookAround extends EntityAIBase
    {
        public final WitherSkeletonGhast parentEntity;

        public AILookAround(WitherSkeletonGhast ghast)
        {
            this.parentEntity = ghast;
            this.setMutexBits(2);
        }

        public boolean shouldExecute()
        {
            return true;
        }

        @Override
        public void updateTask()
        {
            if (this.parentEntity.getAttackTarget() == null)
            {
                this.parentEntity.rotationYaw = -((float) MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ)) * (180F / (float) Math.PI);
                this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
            }
            else
            {
                EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();

                if (entitylivingbase.getDistanceSq(this.parentEntity) < 4096.0D)
                {
                    double d1 = entitylivingbase.posX - this.parentEntity.posX;
                    double d2 = entitylivingbase.posZ - this.parentEntity.posZ;
                    this.parentEntity.rotationYaw = -((float) MathHelper.atan2(d1, d2)) * (180F / (float) Math.PI);
                    this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
                }
            }
        }
    }

    static class AIRandomFly extends EntityAIBase
    {
        public final WitherSkeletonGhast parentEntity;

        public AIRandomFly(WitherSkeletonGhast ghast)
        {
            this.parentEntity = ghast;
            this.setMutexBits(1);
        }

        public boolean shouldExecute()
        {
            EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();

            if (!entitymovehelper.isUpdating())
            {
                return true;
            }
            else
            {
                double d0 = entitymovehelper.getX() - this.parentEntity.posX;
                double d1 = entitymovehelper.getY() - this.parentEntity.posY;
                double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return false;
        }

        @Override
        public void startExecuting()
        {
            Random random = this.parentEntity.getRNG();
            double d0 = this.parentEntity.posX + ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.parentEntity.posY + ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.parentEntity.posZ + ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
        }
    }

    static class GhastMoveHelper extends EntityMoveHelper
    {
        public final WitherSkeletonGhast parentEntity;
        public int courseChangeCooldown;

        public GhastMoveHelper(WitherSkeletonGhast ghast)
        {
            super(ghast);
            this.parentEntity = ghast;
        }

        @Override
        public void onUpdateMoveHelper()
        {
            if (this.action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = this.posX - this.parentEntity.posX;
                double d1 = this.posY - this.parentEntity.posY;
                double d2 = this.posZ - this.parentEntity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (this.courseChangeCooldown-- <= 0)
                {
                    this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
                    d3 = MathHelper.sqrt(d3);

                    if (this.isNotColliding(this.posX, this.posY, this.posZ, d3))
                    {
                        this.parentEntity.motionX += d0 / d3 * 0.1D;
                        this.parentEntity.motionY += d1 / d3 * 0.1D;
                        this.parentEntity.motionZ += d2 / d3 * 0.1D;
                    }
                    else
                    {
                        this.action = EntityMoveHelper.Action.WAIT;
                    }
                }
            }
        }

        public boolean isNotColliding(double x, double y, double z, double p_179926_7_)
        {
            double d0 = (x - this.parentEntity.posX) / p_179926_7_;
            double d1 = (y - this.parentEntity.posY) / p_179926_7_;
            double d2 = (z - this.parentEntity.posZ) / p_179926_7_;
            AxisAlignedBB axisalignedbb = this.parentEntity.getEntityBoundingBox();

            for (int i = 1; i < p_179926_7_; ++i)
            {
                axisalignedbb = axisalignedbb.offset(d0, d1, d2);

                if (!this.parentEntity.world.getCollisionBoxes(this.parentEntity, axisalignedbb).isEmpty())
                {
                    return false;
                }
            }

            return true;
        }
    }
}