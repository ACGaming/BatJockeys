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
import mod.acgaming.jockeys.config.JockeysConfig;
import mod.acgaming.jockeys.entity.CandyBomb;
import mod.acgaming.jockeys.entity.SkeletonBat;
import mod.acgaming.jockeys.entity.WitherSkeletonGhast;

@Mod.EventBusSubscriber(modid = Jockeys.MOD_ID)
public class JockeysRegistry
{
    public static final List<Biome> REGULAR_SPAWNING = Lists.newArrayList();
    public static final List<Biome> NETHER_SPAWNING = Lists.newArrayList();
    public static final List<Biome> END_SPAWNING = Lists.newArrayList();
    public static int id = 0;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
    {
        // REGISTRATION
        registerEntity(SkeletonBat.class, "skeleton_bat", 4996656, 986895, EntityLiving.SpawnPlacementType.IN_AIR);
        registerEntity(WitherSkeletonGhast.class, "wither_skeleton_ghast", 16382457, 12369084, EntityLiving.SpawnPlacementType.ON_GROUND);
        registerEntity(CandyBomb.class, "candy_bomb");

        // SPAWNING
        compileSpawningBiomes();
        if (JockeysConfig.SKELETON_BAT_SETTINGS.spawnWeight > 0) EntityRegistry.addSpawn(SkeletonBat.class, JockeysConfig.SKELETON_BAT_SETTINGS.spawnWeight, JockeysConfig.SKELETON_BAT_SETTINGS.minGroupSize, JockeysConfig.SKELETON_BAT_SETTINGS.maxGroupSize, EnumCreatureType.MONSTER, REGULAR_SPAWNING.toArray(new Biome[0]));
        if (JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.spawnWeight > 0) EntityRegistry.addSpawn(WitherSkeletonGhast.class, JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.spawnWeight, JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.minGroupSize, JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.maxGroupSize, EnumCreatureType.MONSTER, NETHER_SPAWNING.toArray(new Biome[0]));
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

    public static void registerEntity(Class<? extends Entity> cls, String name, int primaryColor, int secondaryColor, EntityLiving.SpawnPlacementType placementType)
    {
        EntityEntry entityEntry = new EntityEntry(cls, name);
        ResourceLocation registryName = new ResourceLocation(Jockeys.MOD_ID, name);
        entityEntry.setRegistryName(registryName);
        entityEntry.setEgg(new EntityList.EntityEggInfo(registryName, primaryColor, secondaryColor));
        EntitySpawnPlacementRegistry.setPlacementType(cls, placementType);
        EntityRegistry.registerModEntity(registryName, cls, name, id++, Jockeys.instance, 64, 1, true, primaryColor, secondaryColor);
    }

    public static void registerEntity(Class<? extends Entity> cls, String name)
    {
        EntityEntry entityEntry = new EntityEntry(cls, name);
        ResourceLocation registryName = new ResourceLocation(Jockeys.MOD_ID, name);
        entityEntry.setRegistryName(registryName);
        EntityRegistry.registerModEntity(registryName, cls, name, id++, Jockeys.instance, 64, 1, true);
    }
}
