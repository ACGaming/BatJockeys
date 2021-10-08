package mod.acgaming.batjockeys.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigurationHandler
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Spawn SPAWN = new Spawn(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class Spawn
    {
        public final ForgeConfigSpec.IntValue batjockey_min;
        public final ForgeConfigSpec.IntValue batjockey_max;
        public final ForgeConfigSpec.IntValue batjockey_weight;

        Spawn(ForgeConfigSpec.Builder builder)
        {
            builder.push("Spawn Chances");
            builder.comment("Configure spawn weight & min/max group size. Set weight to 0 to disable.");

            batjockey_min = builder.defineInRange("batjockey_min", 1, 1, 100);
            batjockey_max = builder.defineInRange("batjockey_max", 4, 1, 100);
            batjockey_weight = builder.defineInRange("batjockey_weight", 60, 0, 200);

            builder.pop();
        }
    }
}