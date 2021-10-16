package mod.acgaming.batjockeys.init;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import mod.acgaming.batjockeys.BatJockeys;
import mod.acgaming.batjockeys.config.ConfigHandler;

@Mod.EventBusSubscriber(modid = BatJockeys.MOD_ID)
public class BatJockeysSpawning
{
    @SubscribeEvent
    public static void onBiomeLoad(final BiomeLoadingEvent event)
    {
        if (event.getName() == null)
        {
            return;
        }
        MobSpawnInfoBuilder spawns = event.getSpawns();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(BatJockeysRegistry.SKELETON_BAT.get(), ConfigHandler.SKELETON_BAT_SETTINGS.spawn_weight.get(), ConfigHandler.SKELETON_BAT_SETTINGS.min_group_size.get(), ConfigHandler.SKELETON_BAT_SETTINGS.max_group_size.get()));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(BatJockeysRegistry.VEX_BAT.get(), ConfigHandler.VEX_BAT_SETTINGS.spawn_weight.get(), ConfigHandler.VEX_BAT_SETTINGS.min_group_size.get(), ConfigHandler.VEX_BAT_SETTINGS.max_group_size.get()));
    }
}