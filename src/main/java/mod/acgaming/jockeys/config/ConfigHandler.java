package mod.acgaming.jockeys.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import mod.acgaming.jockeys.Jockeys;

@Config(modid = Jockeys.MODID, name = "Jockeys")
public class ConfigHandler
{
    @Config.Comment("Skeleton Bat Settings")
    public static SkeletonBatSettings skeleton_bat_settings = new SkeletonBatSettings();

    public static class SkeletonBatSettings
    {
        @Config.Name("Skeleton Bat Min Group Size")
        @Config.Comment("Minimum amount per spawn")
        public int min_group_size = 1;

        @Config.Name("Skeleton Bat Max Group Size")
        @Config.Comment("Maximum amount per spawn")
        public int max_group_size = 2;

        @Config.Name("Skeleton Bat Spawn Weight")
        @Config.Comment("Chance to spawn")
        public int spawn_weight = 10;

        @Config.Name("Skeleton Bat Jockey Head")
        @Config.Comment("Head armor for jockeys")
        public String jockey_head = "minecraft:leather_helmet";

        @Config.Name("Skeleton Bat Jockey Chest")
        @Config.Comment("Chest armor for jockeys")
        public String jockey_chest = "";

        @Config.Name("Skeleton Bat Jockey Legs")
        @Config.Comment("Legs armor for jockeys")
        public String jockey_legs = "";

        @Config.Name("Skeleton Bat Jockey Feet")
        @Config.Comment("Feet armor for jockeys")
        public String jockey_feet = "";

        @Config.Name("Skeleton Bat Jockey Main Item")
        @Config.Comment("Main item for jockeys")
        public String jockey_item_main = "minecraft:bow";

        @Config.Name("Skeleton Bat Jockey Off Item")
        @Config.Comment("Offhand item for jockeys")
        public String jockey_item_off = "minecraft:bone";
    }

    @Mod.EventBusSubscriber(modid = Jockeys.MODID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(Jockeys.MODID))
            {
                ConfigManager.sync(Jockeys.MODID, Config.Type.INSTANCE);
            }
        }
    }
}