package mod.acgaming.batjockeys.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Spawn SPAWNING = new Spawn(BUILDER);
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static class General
    {
        public final ForgeConfigSpec.ConfigValue<String> jockey_head;
        public final ForgeConfigSpec.ConfigValue<String> jockey_chest;
        public final ForgeConfigSpec.ConfigValue<String> jockey_legs;
        public final ForgeConfigSpec.ConfigValue<String> jockey_feet;
        public final ForgeConfigSpec.ConfigValue<String> jockey_item_main;
        public final ForgeConfigSpec.ConfigValue<String> jockey_item_off;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.push("General Settings");

            jockey_head = builder
                .comment("Head armor for the jockey")
                .define("Jockey Head", "minecraft:carved_pumpkin");

            jockey_chest = builder
                .comment("Chest armor for the jockey")
                .define("Jockey Chest", "");

            jockey_legs = builder
                .comment("Legs armor for the jockey")
                .define("Jockey Legs", "");

            jockey_feet = builder
                .comment("Feet armor for the jockey")
                .define("Jockey Feet", "");

            jockey_item_main = builder
                .comment("Main item for the jockey")
                .define("Jockey Main Item", "minecraft:bow");

            jockey_item_off = builder
                .comment("Offhand item for the jockey")
                .define("Jockey Off Item", "minecraft:bone");

            builder.pop();
        }
    }

    public static class Spawn
    {
        public final ForgeConfigSpec.IntValue batjockey_min;
        public final ForgeConfigSpec.IntValue batjockey_max;
        public final ForgeConfigSpec.IntValue batjockey_weight;

        Spawn(ForgeConfigSpec.Builder builder)
        {
            builder.push("Spawn Settings");

            batjockey_min = builder
                .comment("Minimum amount per spawn")
                .defineInRange("Min Group Size", 1, 1, 100);

            batjockey_max = builder
                .comment("Maximum amount per spawn")
                .defineInRange("Max Group Size", 2, 1, 100);

            batjockey_weight = builder
                .comment("Chance to spawn")
                .defineInRange("Spawn Weight", 40, 0, 200);

            builder.pop();
        }
    }
}