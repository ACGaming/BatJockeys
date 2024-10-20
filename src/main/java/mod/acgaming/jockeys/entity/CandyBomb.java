package mod.acgaming.jockeys.entity;

import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.util.JockeysHelper;

public class CandyBomb extends EntityFireball
{
    private Item ammo;

    public CandyBomb(World world)
    {
        super(world);
    }

    public CandyBomb(World world, EntityLivingBase shooter, double accelX, double accelY, double accelZ)
    {
        super(world, shooter, accelX, accelY, accelZ);
        this.ammo = JockeysHelper.getRandomHalloweenDrop(world);
    }

    @SideOnly(Side.CLIENT)
    public void spawnParticles(World world, BlockPos pos)
    {
        ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
        particleManager.addEffect(new ParticleFirework.Starter(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, particleManager, generateTag()));
    }

    public NBTTagCompound generateTag()
    {
        NBTTagCompound fireworkTag = new NBTTagCompound();
        NBTTagCompound fireworkItemTag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        List<Integer> list = Lists.newArrayList();
        list.add(ItemDye.DYE_COLORS[1]);
        list.add(ItemDye.DYE_COLORS[4]);
        list.add(ItemDye.DYE_COLORS[11]);
        for (int i = 0; i < rand.nextInt(3) + 3; i++) list.add(ItemDye.DYE_COLORS[rand.nextInt(15)]);
        int[] colours = new int[list.size()];
        for (int j = 0; j < colours.length; j++) colours[j] = list.get(j);
        fireworkTag.setIntArray("Colors", colours);
        fireworkTag.setBoolean("Flicker", true);
        fireworkTag.setByte("Type", (byte) (4));
        nbttaglist.appendTag(fireworkTag);
        fireworkItemTag.setTag("Explosions", nbttaglist);
        return fireworkItemTag;
    }

    @Override
    public boolean isFireballFiery()
    {
        return false;
    }

    @Override
    public void onImpact(RayTraceResult result)
    {
        if (!this.world.isRemote)
        {
            if (result.entityHit != null)
            {
                result.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 2.0F);
                this.applyEnchantments(this.shootingEntity, result.entityHit);
            }
            this.setDead();
            if (Jockeys.isSpookySeason(this.world) && this.rand.nextBoolean() && !JockeysHelper.dropList.isEmpty())
            {
                this.dropItemWithOffset(JockeysHelper.getRandomHalloweenDrop(this.world), 1, 0.5F);
            }
        }
        else if (FMLLaunchHandler.side().isClient() && (result.sideHit == EnumFacing.UP || result.entityHit != null)) spawnParticles(world, this.getPosition());
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        player.swingArm(hand);
        this.dropItem(this.ammo, 1);
        this.world.playSound(null, this.getPosition(), SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.PLAYERS, 2.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        if (FMLLaunchHandler.side().isClient()) spawnParticles(world, this.getPosition());
        this.setDead();
        return true;
    }
}
