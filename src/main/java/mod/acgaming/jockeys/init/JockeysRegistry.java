package mod.acgaming.jockeys.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
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
    private static int id = 0;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
    {
        // REGISTRATION
        registerEntity(SkeletonBat.class, "skeleton_bat", 4996656, 986895, EntityLiving.SpawnPlacementType.IN_AIR);
        registerEntity(WitherSkeletonGhast.class, "wither_skeleton_ghast", 16382457, 12369084, EntityLiving.SpawnPlacementType.ON_GROUND);
        registerEntity(CandyBomb.class, "candy_bomb");

        // SPAWNING
        if (JockeysConfig.SKELETON_BAT_SETTINGS.spawnWeight > 0) EntityRegistry.addSpawn(SkeletonBat.class, JockeysConfig.SKELETON_BAT_SETTINGS.spawnWeight, JockeysConfig.SKELETON_BAT_SETTINGS.minGroupSize, JockeysConfig.SKELETON_BAT_SETTINGS.maxGroupSize, EnumCreatureType.MONSTER, getEntityBiomes(EntitySkeleton.class));
        if (JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.spawnWeight > 0) EntityRegistry.addSpawn(WitherSkeletonGhast.class, JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.spawnWeight, JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.minGroupSize, JockeysConfig.WITHER_SKELETON_GHAST_SETTINGS.maxGroupSize, EnumCreatureType.MONSTER, getEntityBiomes(EntityGhast.class));
    }

    public static Biome[] getEntityBiomes(Class<? extends Entity> spawn)
    {
        List<Biome> biomes = new ArrayList<>();
        for (Biome biome : Biome.REGISTRY)
        {
            List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.MONSTER);
            for (Biome.SpawnListEntry list : spawnList)
                if (list.entityClass == spawn)
                {
                    biomes.add(biome);
                    break;
                }
        }
        return biomes.toArray(new Biome[0]);
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
