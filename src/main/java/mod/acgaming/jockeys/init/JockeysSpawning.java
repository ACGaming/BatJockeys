package mod.acgaming.jockeys.init;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.config.ConfigHandler;

@Mod.EventBusSubscriber(modid = Jockeys.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JockeysSpawning
{
    @SubscribeEvent
    public static void onBiomeLoad(final BiomeLoadingEvent event)
    {
        MobSpawnSettings.SpawnerData skeleton_bat_data = new MobSpawnSettings.SpawnerData(JockeysRegistry.SKELETON_BAT.get(), ConfigHandler.SKELETON_BAT_SETTINGS.spawn_weight.get(), ConfigHandler.SKELETON_BAT_SETTINGS.min_group_size.get(), ConfigHandler.SKELETON_BAT_SETTINGS.max_group_size.get());
        MobSpawnSettings.SpawnerData vex_bat_data = new MobSpawnSettings.SpawnerData(JockeysRegistry.VEX_BAT.get(), ConfigHandler.VEX_BAT_SETTINGS.spawn_weight.get(), ConfigHandler.VEX_BAT_SETTINGS.min_group_size.get(), ConfigHandler.VEX_BAT_SETTINGS.max_group_size.get());
        MobSpawnSettings.SpawnerData wither_skeleton_ghast_data = new MobSpawnSettings.SpawnerData(JockeysRegistry.WITHER_SKELETON_GHAST.get(), ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.spawn_weight.get(), ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.min_group_size.get(), ConfigHandler.WITHER_SKELETON_GHAST_SETTINGS.max_group_size.get());

        if (event.getCategory() == Biome.BiomeCategory.NETHER)
        {
            event.getSpawns().addSpawn(MobCategory.MONSTER, wither_skeleton_ghast_data);
        }

        if (!(event.getCategory() == Biome.BiomeCategory.NETHER) && !(event.getCategory() == Biome.BiomeCategory.THEEND))
        {
            event.getSpawns().addSpawn(MobCategory.MONSTER, skeleton_bat_data);
            event.getSpawns().addSpawn(MobCategory.MONSTER, vex_bat_data);
        }
    }
}