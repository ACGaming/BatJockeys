package mod.acgaming.jockeys.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final SkeletonBatSettings SKELETON_BAT_SETTINGS = new SkeletonBatSettings(BUILDER);
    public static final VexBatSettings VEX_BAT_SETTINGS = new VexBatSettings(BUILDER);
    public static final WitherSkeletonGhastSettings WITHER_SKELETON_GHAST_SETTINGS = new WitherSkeletonGhastSettings(BUILDER);
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static class SkeletonBatSettings
    {
        public final ForgeConfigSpec.IntValue min_group_size;
        public final ForgeConfigSpec.IntValue max_group_size;
        public final ForgeConfigSpec.IntValue spawn_weight;
        public final ForgeConfigSpec.ConfigValue<String> jockey_head;
        public final ForgeConfigSpec.ConfigValue<String> jockey_chest;
        public final ForgeConfigSpec.ConfigValue<String> jockey_legs;
        public final ForgeConfigSpec.ConfigValue<String> jockey_feet;
        public final ForgeConfigSpec.ConfigValue<String> jockey_item_main;
        public final ForgeConfigSpec.ConfigValue<String> jockey_item_off;

        SkeletonBatSettings(ForgeConfigSpec.Builder builder)
        {
            builder.push("Skeleton Bat Settings");

            min_group_size = builder
                .comment("Minimum amount per spawn")
                .defineInRange("Skeleton Bat Min Group Size", 1, 1, 10);

            max_group_size = builder
                .comment("Maximum amount per spawn")
                .defineInRange("Skeleton Bat Max Group Size", 1, 1, 10);

            spawn_weight = builder
                .comment("Chance to spawn")
                .defineInRange("Skeleton Bat Spawn Weight", 40, 0, 200);

            jockey_head = builder
                .comment("Head armor for jockeys")
                .define("Skeleton Bat Jockey Head", "");

            jockey_chest = builder
                .comment("Chest armor for jockeys")
                .define("Skeleton Bat Jockey Chest", "");

            jockey_legs = builder
                .comment("Legs armor for jockeys")
                .define("Skeleton Bat Jockey Legs", "");

            jockey_feet = builder
                .comment("Feet armor for jockeys")
                .define("Skeleton Bat Jockey Feet", "");

            jockey_item_main = builder
                .comment("Main item for jockeys")
                .define("Skeleton Bat Jockey Main Item", "minecraft:bow");

            jockey_item_off = builder
                .comment("Offhand item for jockeys")
                .define("Skeleton Bat Jockey Off Item", "minecraft:bone");

            builder.pop();
        }
    }

    public static class VexBatSettings
    {
        public final ForgeConfigSpec.IntValue min_group_size;
        public final ForgeConfigSpec.IntValue max_group_size;
        public final ForgeConfigSpec.IntValue spawn_weight;

        VexBatSettings(ForgeConfigSpec.Builder builder)
        {
            builder.push("Vex Bat Settings");

            min_group_size = builder
                .comment("Minimum amount per spawn")
                .defineInRange("Vex Bat Min Group Size", 1, 1, 10);

            max_group_size = builder
                .comment("Maximum amount per spawn")
                .defineInRange("Vex Bat Max Group Size", 1, 1, 10);

            spawn_weight = builder
                .comment("Chance to spawn")
                .defineInRange("Vex Bat Spawn Weight", 20, 0, 200);

            builder.pop();
        }
    }

    public static class WitherSkeletonGhastSettings
    {
        public final ForgeConfigSpec.IntValue min_group_size;
        public final ForgeConfigSpec.IntValue max_group_size;
        public final ForgeConfigSpec.IntValue spawn_weight;
        public final ForgeConfigSpec.ConfigValue<String> jockey_head;
        public final ForgeConfigSpec.ConfigValue<String> jockey_chest;
        public final ForgeConfigSpec.ConfigValue<String> jockey_legs;
        public final ForgeConfigSpec.ConfigValue<String> jockey_feet;
        public final ForgeConfigSpec.ConfigValue<String> jockey_item_main;
        public final ForgeConfigSpec.ConfigValue<String> jockey_item_off;
        public final ForgeConfigSpec.DoubleValue attack_range;
        public final ForgeConfigSpec.IntValue attack_interval;

        WitherSkeletonGhastSettings(ForgeConfigSpec.Builder builder)
        {
            builder.push("Wither Skeleton Ghast Settings");

            min_group_size = builder
                .comment("Minimum amount per spawn")
                .defineInRange("Wither Skeleton Ghast Min Group Size", 1, 1, 10);

            max_group_size = builder
                .comment("Maximum amount per spawn")
                .defineInRange("Wither Skeleton Ghast Max Group Size", 1, 1, 10);

            spawn_weight = builder
                .comment("Chance to spawn")
                .defineInRange("Wither Skeleton Ghast Spawn Weight", 30, 0, 200);

            jockey_head = builder
                .comment("Head armor for jockeys")
                .define("Wither Skeleton Ghast Jockey Head", "");

            jockey_chest = builder
                .comment("Chest armor for jockeys")
                .define("Wither Skeleton Ghast Jockey Chest", "");

            jockey_legs = builder
                .comment("Legs armor for jockeys")
                .define("Wither Skeleton Ghast Jockey Legs", "");

            jockey_feet = builder
                .comment("Feet armor for jockeys")
                .define("Wither Skeleton Ghast Jockey Feet", "");

            jockey_item_main = builder
                .comment("Main item for jockeys")
                .define("Wither Skeleton Ghast Jockey Main Item", "minecraft:bow");

            jockey_item_off = builder
                .comment("Offhand item for jockeys")
                .define("Wither Skeleton Ghast Jockey Off Item", "minecraft:spyglass");

            attack_range = builder
                .comment("Attack range for jockeys")
                .defineInRange("Wither Skeleton Ghast Jockey Attack Range", 100.0D, 1.0D, 100.0D);

            attack_interval = builder
                .comment("Attack interval for jockeys")
                .defineInRange("Wither Skeleton Ghast Jockey Attack Interval", 40, 10, 1000);

            builder.pop();
        }
    }
}