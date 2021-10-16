package mod.acgaming.jockeys.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final SkeletonBatSettings SKELETON_BAT_SETTINGS = new SkeletonBatSettings(BUILDER);
    public static final VexBatSettings VEX_BAT_SETTINGS = new VexBatSettings(BUILDER);
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
                .defineInRange("Skeleton Bat Min Group Size", 1, 1, 100);

            max_group_size = builder
                .comment("Maximum amount per spawn")
                .defineInRange("Skeleton Bat Max Group Size", 2, 1, 100);

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
                .defineInRange("Vex Bat Min Group Size", 1, 1, 100);

            max_group_size = builder
                .comment("Maximum amount per spawn")
                .defineInRange("Vex Bat Max Group Size", 2, 1, 100);

            spawn_weight = builder
                .comment("Chance to spawn")
                .defineInRange("Vex Bat Spawn Weight", 20, 0, 200);

            builder.pop();
        }
    }
}