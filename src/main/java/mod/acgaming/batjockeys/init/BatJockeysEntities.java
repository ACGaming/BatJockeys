package mod.acgaming.batjockeys.init;

import java.util.List;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import mod.acgaming.batjockeys.Reference;
import mod.acgaming.batjockeys.config.ConfigHandler;
import mod.acgaming.batjockeys.entity.LargeBatEntity;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class BatJockeysEntities
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addSpawn(BiomeLoadingEvent event)
    {
        Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());
        if (biome != null)
        {
            MobSpawnInfo info = biome.getMobSettings();
            List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.MONSTER);

            for (Spawners entry : info.getMobs(EntityClassification.MONSTER))
            {
                registerSpawn(spawns, entry, ConfigHandler.SPAWN.batjockey_weight.get(), ConfigHandler.SPAWN.batjockey_min.get(), ConfigHandler.SPAWN.batjockey_max.get(), EntityType.SKELETON, BatJockeysRegistry.LARGE_BAT.get());
            }
        }
    }

    public static void initializeEntities()
    {
        EntitySpawnPlacementRegistry.register(BatJockeysRegistry.LARGE_BAT.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LargeBatEntity::canSpawn);
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(BatJockeysRegistry.LARGE_BAT.get(), LargeBatEntity.createAttributes().build());
    }

    public static void registerSpawn(List<Spawners> spawns, Spawners entry, Integer weight, Integer min, Integer max, EntityType<? extends LivingEntity> oldEntity, EntityType<? extends LivingEntity> newEntity)
    {
        if (entry.type == oldEntity)
        {
            spawns.add(new MobSpawnInfo.Spawners(newEntity, weight, min, max));
        }
    }
}