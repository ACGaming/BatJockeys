package mod.acgaming.batjockeys.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler
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
            builder.push("Spawn Settings");

            batjockey_min = builder
                .defineInRange("Min Group Size", 1, 1, 100);

            batjockey_max = builder
                .defineInRange("Max Group Size", 2, 1, 100);

            batjockey_weight = builder
                .defineInRange("Spawn Weight", 40, 0, 200);

            builder.pop();
        }
    }
}