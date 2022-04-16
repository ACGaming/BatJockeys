package mod.acgaming.jockeys.init;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.JockeysTab;
import mod.acgaming.jockeys.config.ConfigHandler;
import mod.acgaming.jockeys.entity.SkeletonBat;

@Mod.EventBusSubscriber(modid = Jockeys.MODID)
public class JockeysRegistry
{
    public static int id;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
    {
        List<Biome> regularSpawning = Lists.newArrayList();
        for (Biome b : Biome.REGISTRY)
        {
            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
            if (!types.contains(BiomeDictionary.Type.WATER) || !types.contains(BiomeDictionary.Type.NETHER) || !types.contains(BiomeDictionary.Type.END))
            {
                regularSpawning.add(b);
            }
        }

        // REGISTRATION
        registerEntityWithSpawnEgg(SkeletonBat.class, "skeleton_bat", 4996656, 986895);

        // SPAWNING
        if (ConfigHandler.skeleton_bat_settings.spawn_weight > 0) EntityRegistry.addSpawn(SkeletonBat.class, ConfigHandler.skeleton_bat_settings.spawn_weight, ConfigHandler.skeleton_bat_settings.min_group_size, ConfigHandler.skeleton_bat_settings.max_group_size, EnumCreatureType.MONSTER, regularSpawning.toArray(new Biome[0]));
    }

    public static void registerEntityWithSpawnEgg(Class clazz, String entityName, int primary, int secondary)
    {
        ResourceLocation registryName = new ResourceLocation(Jockeys.MODID, entityName);
        EntityRegistry.registerModEntity(registryName, clazz, entityName, id++, Jockeys.instance, 64, 1, true, primary, secondary);
        JockeysTab.eggs.add(getSpawnEgg(entityName));
    }

    public static ItemStack getSpawnEgg(String entityName)
    {
        ItemStack stack = new ItemStack(Items.SPAWN_EGG);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("id", Jockeys.MODID + ":" + entityName);
        NBTTagCompound nbt2 = new NBTTagCompound();
        nbt2.setTag("EntityTag", nbt);
        stack.setTagCompound(nbt2);
        return stack;
    }
}