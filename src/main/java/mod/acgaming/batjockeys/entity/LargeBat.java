package mod.acgaming.batjockeys.entity;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class LargeBat extends Phantom implements Enemy
{
    public static boolean checkLargeBatSpawnRules(EntityType<LargeBat> largebat, ServerLevelAccessor level, MobSpawnType spawntype, BlockPos pos, Random random)
    {
        return level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && checkMobSpawnRules(largebat, level, spawntype, pos, random);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.16D).add(Attributes.FOLLOW_RANGE, 100.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public LargeBat(EntityType<? extends LargeBat> typeIn, Level levelIn)
    {
        super(typeIn, levelIn);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawntype, @Nullable SpawnGroupData groupdata, @Nullable CompoundTag compoundtag)
    {
        groupdata = super.finalizeSpawn(level, difficulty, spawntype, groupdata, compoundtag);
        Skeleton skeleton = EntityType.SKELETON.create(this.level);
        if (skeleton != null)
        {
            skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            skeleton.finalizeSpawn(level, difficulty, spawntype, null, null);
            skeleton.startRiding(this);
        }
        return groupdata;
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound()
    {
        return this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_27451_)
    {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.1F;
    }

    @Override
    public float getVoicePitch()
    {
        return super.getVoicePitch() * 0.95F;
    }
}