package mod.acgaming.jockeys.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.config.ConfigHandler;
import mod.acgaming.jockeys.config.RegistryHelper;

public class SniperWitherSkeleton extends AbstractSniperSkeleton
{
    public SniperWitherSkeleton(EntityType<? extends SniperWitherSkeleton> p_34166_, Level p_34167_)
    {
        super(p_34166_, p_34167_);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
    }

    public boolean canBeAffected(MobEffectInstance p_34192_)
    {
        return p_34192_.getEffect() != MobEffects.WITHER && super.canBeAffected(p_34192_);
    }

    protected float getStandingEyeHeight(Pose p_34186_, EntityDimensions p_34187_)
    {
        return 2.1F;
    }

    protected void registerGoals()
    {
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
        super.registerGoals();
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance p_34172_)
    {
        if (Jockeys.isHalloween())
        {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
        }
        else
        {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockey_head.get())));
        }
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockey_chest.get())));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockey_legs.get())));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockey_feet.get())));
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockey_item_main.get())));
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(RegistryHelper.getItemValueFromName(ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.jockey_item_off.get())));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34178_, DifficultyInstance p_34179_, MobSpawnType p_34180_, @Nullable SpawnGroupData p_34181_, @Nullable CompoundTag p_34182_)
    {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34178_, p_34179_, p_34180_, p_34181_, p_34182_);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        this.reassessWeaponGoal();
        return spawngroupdata;
    }

    protected SoundEvent getStepSound()
    {
        return SoundEvents.WITHER_SKELETON_STEP;
    }

    protected AbstractArrow getArrow(ItemStack p_34189_, float p_34190_)
    {
        AbstractArrow abstractarrow = super.getArrow(p_34189_, p_34190_);
        abstractarrow.setSecondsOnFire(100);
        return abstractarrow;
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    protected void dropCustomDeathLoot(DamageSource p_34174_, int p_34175_, boolean p_34176_)
    {
        super.dropCustomDeathLoot(p_34174_, p_34175_, p_34176_);
        Entity entity = p_34174_.getEntity();
        if (entity instanceof Creeper)
        {
            Creeper creeper = (Creeper) entity;
            if (creeper.canDropMobsSkull())
            {
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
            }
        }
    }

    protected void populateDefaultEquipmentEnchantments(DifficultyInstance p_34184_)
    {
    }

    public boolean doHurtTarget(Entity target)
    {
        if (!super.doHurtTarget(target))
        {
            return false;
        }
        else
        {
            if (target instanceof LivingEntity)
            {
                ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
            }
            return true;
        }
    }

    protected SoundEvent getHurtSound(DamageSource p_34195_)
    {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }
}