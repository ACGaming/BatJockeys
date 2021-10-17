package mod.acgaming.jockeys.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import mod.acgaming.jockeys.config.ConfigHandler;

public abstract class AbstractSniperSkeleton extends Monster implements RangedAttackMob
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.FOLLOW_RANGE, ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.attack_range.get());
    }

    private final RangedBowAttackGoal<AbstractSniperSkeleton> bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 40, 100.0F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false)
    {
        public void start()
        {
            super.start();
            AbstractSniperSkeleton.this.setAggressive(true);
        }

        public void stop()
        {
            super.stop();
            AbstractSniperSkeleton.this.setAggressive(false);
        }
    };

    protected AbstractSniperSkeleton(EntityType<? extends AbstractSniperSkeleton> p_32133_, Level p_32134_)
    {
        super(p_32133_, p_32134_);
        this.reassessWeaponGoal();
    }

    public MobType getMobType()
    {
        return MobType.UNDEAD;
    }

    public void rideTick()
    {
        super.rideTick();
        if (this.getVehicle() instanceof PathfinderMob pathfindermob)
        {
            this.yBodyRot = pathfindermob.yBodyRot;
        }
    }

    protected float getStandingEyeHeight(Pose p_32154_, EntityDimensions p_32155_)
    {
        return 1.74F;
    }

    public void aiStep()
    {
        boolean flag = this.isSunBurnTick();
        if (flag)
        {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty())
            {
                if (itemstack.isDamageableItem())
                {
                    itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage())
                    {
                        this.broadcastBreakEvent(EquipmentSlot.HEAD);
                        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag)
            {
                this.setSecondsOnFire(8);
            }
        }
        super.aiStep();
    }

    public void reassessWeaponGoal()
    {
        if (!this.level.isClientSide)
        {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem));
            if (itemstack.is(Items.BOW))
            {
                int i = 40;
                if (this.level.getDifficulty() != Difficulty.HARD)
                {
                    i = 80;
                }

                this.bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowGoal);
            }
            else
            {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }

        }
    }

    public void performRangedAttack(LivingEntity p_32141_, float p_32142_)
    {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow abstractarrow = this.getArrow(itemstack, p_32142_);
        if (this.getMainHandItem().getItem() instanceof BowItem)
            abstractarrow = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrow);
        double d0 = p_32141_.getX() - this.getX();
        double d1 = p_32141_.getY(0.3333333333333333D) - abstractarrow.getY();
        double d2 = p_32141_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(abstractarrow);
    }

    public boolean isShaking()
    {
        return this.isFullyFrozen();
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 100.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_32144_)
    {
        return p_32144_ == Items.BOW;
    }

    public void readAdditionalSaveData(CompoundTag p_32152_)
    {
        super.readAdditionalSaveData(p_32152_);
        this.reassessWeaponGoal();
    }

    public void setItemSlot(EquipmentSlot p_32138_, ItemStack p_32139_)
    {
        super.setItemSlot(p_32138_, p_32139_);
        if (!this.level.isClientSide)
        {
            this.reassessWeaponGoal();
        }
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance p_32136_)
    {
        super.populateDefaultEquipmentSlots(p_32136_);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_32146_, DifficultyInstance p_32147_, MobSpawnType p_32148_, @Nullable SpawnGroupData p_32149_, @Nullable CompoundTag p_32150_)
    {
        p_32149_ = super.finalizeSpawn(p_32146_, p_32147_, p_32148_, p_32149_, p_32150_);
        this.populateDefaultEquipmentSlots(p_32147_);
        this.populateDefaultEquipmentEnchantments(p_32147_);
        this.reassessWeaponGoal();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * p_32147_.getSpecialMultiplier());
        return p_32149_;
    }

    protected void playStepSound(BlockPos p_32159_, BlockState p_32160_)
    {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public double getMyRidingOffset()
    {
        return 0.0D;
    }

    protected abstract SoundEvent getStepSound();

    protected AbstractArrow getArrow(ItemStack p_32156_, float p_32157_)
    {
        return ProjectileUtil.getMobArrow(this, p_32156_, p_32157_);
    }
}