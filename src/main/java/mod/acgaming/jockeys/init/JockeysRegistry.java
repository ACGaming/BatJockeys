package mod.acgaming.jockeys.init;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import net.minecraft.entity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.config.ConfigHandler;
import mod.acgaming.jockeys.entity.SkeletonBat;

@Mod.EventBusSubscriber(modid = Jockeys.MOD_ID)
public class JockeysRegistry
{
    private static final List<Biome> REGULAR_SPAWNING = Lists.newArrayList();
    private static final List<Biome> NETHER_SPAWNING = Lists.newArrayList();
    private static final List<Biome> END_SPAWNING = Lists.newArrayList();
    private static int id = 0;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
    {
        // REGISTRATION
        registerEntityWithSpawnEgg(SkeletonBat.class, "skeleton_bat", 4996656, 986895, EntityLiving.SpawnPlacementType.IN_AIR);

        // SPAWNING
        compileSpawningBiomes();
        if (ConfigHandler.SKELETON_BAT_SETTINGS.spawnWeight > 0) EntityRegistry.addSpawn(SkeletonBat.class, ConfigHandler.SKELETON_BAT_SETTINGS.spawnWeight, ConfigHandler.SKELETON_BAT_SETTINGS.minGroupSize, ConfigHandler.SKELETON_BAT_SETTINGS.maxGroupSize, EnumCreatureType.MONSTER, REGULAR_SPAWNING.toArray(new Biome[0]));
    }

    public static void compileSpawningBiomes()
    {
        for (Biome b : Biome.REGISTRY)
        {
            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
            if (types.contains(BiomeDictionary.Type.NETHER))
            {
                NETHER_SPAWNING.add(b);
            }
            else if (types.contains(BiomeDictionary.Type.END))
            {
                END_SPAWNING.add(b);
            }
            else if (!types.contains(BiomeDictionary.Type.WATER))
            {
                REGULAR_SPAWNING.add(b);
            }
        }
    }

    public static void registerEntityWithSpawnEgg(Class<? extends Entity> cls, String name, int primaryColor, int secondaryColor, EntityLiving.SpawnPlacementType placementType)
    {
        EntityEntry entityEntry = new EntityEntry(cls, name);
        ResourceLocation registryName = new ResourceLocation(Jockeys.MOD_ID, name);
        entityEntry.setRegistryName(registryName);
        entityEntry.setEgg(new EntityList.EntityEggInfo(registryName, primaryColor, secondaryColor));
        EntitySpawnPlacementRegistry.setPlacementType(cls, placementType);
        EntityRegistry.registerModEntity(registryName, cls, name, id++, Jockeys.instance, 64, 1, true, primaryColor, secondaryColor);
    }
}