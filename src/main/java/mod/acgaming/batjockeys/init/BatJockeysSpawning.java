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
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(BatJockeysRegistry.LARGE_BAT.get(), ConfigHandler.SPAWNING.batjockey_weight.get(), ConfigHandler.SPAWNING.batjockey_min.get(), ConfigHandler.SPAWNING.batjockey_max.get()));
    }
}