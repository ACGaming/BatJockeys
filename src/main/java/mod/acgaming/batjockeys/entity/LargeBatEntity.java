package mod.acgaming.batjockeys.entity;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class LargeBatEntity extends PhantomEntity implements IMob
{
    public static boolean canSpawn(EntityType<LargeBatEntity> largebatIn, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn)
    {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && MonsterEntity.isValidLightLevel(worldIn, pos, randomIn) && canSpawnOn(largebatIn, worldIn, reason, pos, randomIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes()
    {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D).createMutableAttribute(Attributes.MAX_HEALTH, 12.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0D);
    }

    public LargeBatEntity(EntityType<? extends LargeBatEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        SkeletonEntity skeletonentity = EntityType.SKELETON.create(this.world);
        if (skeletonentity != null)
        {
            skeletonentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0F);
            skeletonentity.onInitialSpawn(worldIn, difficultyIn, reason, null, null);
            skeletonentity.startRiding(this);
        }
        return spawnDataIn;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_BAT_DEATH;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.1F;
    }

    @Override
    protected float getSoundPitch()
    {
        return super.getSoundPitch() * 0.95F;
    }
}