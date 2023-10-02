package mod.acgaming.jockeys.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import mod.acgaming.jockeys.Jockeys;

@Config(modid = Jockeys.MOD_ID, name = "Jockeys")
public class ConfigHandler
{
    @Config.Comment("General Settings")
    public static final GeneralSettings GENERAL_SETTINGS = new GeneralSettings();

    @Config.Comment("Skeleton Bat Settings")
    public static final SkeletonBatSettings SKELETON_BAT_SETTINGS = new SkeletonBatSettings();

    @Config.Comment("Wither Skeleton Ghast Settings")
    public static final WitherSkeletonGhastSettings WITHER_SKELETON_GHAST_SETTINGS = new WitherSkeletonGhastSettings();

    public static class GeneralSettings
    {
        @Config.Name("Always Spooky Season")
        @Config.Comment("Halloween all year round")
        public boolean alwaysSpookySeason = false;

        @Config.Name("Halloween Drops")
        @Config.Comment("Occasional goodie drops during Halloween")
        public String[] halloweenDrops = new String[]
            {
                "minecraft:cookie",
                "harvestcraft:jellybeansitem",
                "harvestcraft:peppermintitem",
                "harvestcraft:gummybearsitem",
                "harvestcraft:marzipanitem",
                "harvestcraft:slimegummiesitem",
                "harvestcraft:marshmellowsitem",
                "harvestcraft:chocolatebaritem"
            };
    }

    public static class SkeletonBatSettings
    {
        @Config.Name("Skeleton Bat Min Group Size")
        @Config.Comment("Minimum amount per spawn")
        public int minGroupSize = 1;

        @Config.Name("Skeleton Bat Max Group Size")
        @Config.Comment("Maximum amount per spawn")
        public int maxGroupSize = 2;

        @Config.Name("Skeleton Bat Spawn Weight")
        @Config.Comment("Chance to spawn")
        public int spawnWeight = 10;

        @Config.Name("Skeleton Bat Max Health")
        public double maxHealth = 10.0D;

        @Config.Name("Skeleton Bat Follow Range")
        public double followRange = 32.0D;

        @Config.Name("Skeleton Bat Attack Damage")
        public double attackDamage = 1.0D;

        @Config.Name("Skeleton Bat Jockey Head")
        @Config.Comment("Head armor for jockeys")
        public String jockeyHead = "minecraft:leather_helmet";

        @Config.Name("Skeleton Bat Jockey Chest")
        @Config.Comment("Chest armor for jockeys")
        public String jockeyChest = "";

        @Config.Name("Skeleton Bat Jockey Legs")
        @Config.Comment("Legs armor for jockeys")
        public String jockeyLegs = "";

        @Config.Name("Skeleton Bat Jockey Feet")
        @Config.Comment("Feet armor for jockeys")
        public String jockeyFeet = "";

        @Config.Name("Skeleton Bat Jockey Mainhand Item")
        @Config.Comment("Main item for jockeys")
        public String jockeyItemMainhand = "minecraft:bow";

        @Config.Name("Skeleton Bat Jockey Offhand Item")
        @Config.Comment("Offhand item for jockeys")
        public String jockeyItemOffhand = "minecraft:bone";
    }

    public static class WitherSkeletonGhastSettings
    {
        @Config.Name("Wither Skeleton Ghast Min Group Size")
        @Config.Comment("Minimum amount per spawn")
        public int minGroupSize = 1;

        @Config.Name("Wither Skeleton Ghast Max Group Size")
        @Config.Comment("Maximum amount per spawn")
        public int maxGroupSize = 2;

        @Config.Name("Wither Skeleton Ghast Spawn Weight")
        @Config.Comment("Chance to spawn")
        public int spawnWeight = 10;

        @Config.Name("Wither Skeleton Ghast Max Health")
        public double maxHealth = 10.0D;

        @Config.Name("Wither Skeleton Ghast Follow Range")
        public double followRange = 100.0D;

        @Config.Name("Wither Skeleton Ghast Jockey Head")
        @Config.Comment("Head armor for jockeys")
        public String jockeyHead = "minecraft:leather_helmet";

        @Config.Name("Wither Skeleton Ghast Jockey Chest")
        @Config.Comment("Chest armor for jockeys")
        public String jockeyChest = "";

        @Config.Name("Wither Skeleton Ghast Jockey Legs")
        @Config.Comment("Legs armor for jockeys")
        public String jockeyLegs = "";

        @Config.Name("Wither Skeleton Ghast Jockey Feet")
        @Config.Comment("Feet armor for jockeys")
        public String jockeyFeet = "";

        @Config.Name("Wither Skeleton Ghast Jockey Mainhand Item")
        @Config.Comment("Main item for jockeys")
        public String jockeyItemMainhand = "minecraft:bow";

        @Config.Name("Wither Skeleton Ghast Jockey Offhand Item")
        @Config.Comment("Offhand item for jockeys")
        public String jockeyItemOffhand = "minecraft:bone";
    }

    @Mod.EventBusSubscriber(modid = Jockeys.MOD_ID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(Jockeys.MOD_ID))
            {
                ConfigManager.sync(Jockeys.MOD_ID, Config.Type.INSTANCE);
                RegistryHelper.initHalloweenDropList();
            }
        }
    }
}