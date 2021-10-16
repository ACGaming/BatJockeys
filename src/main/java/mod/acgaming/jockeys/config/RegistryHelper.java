package mod.acgaming.jockeys.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHelper
{
    public static Item getItemValueFromName(String name)
    {
        if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)) != null)
        {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        }
        return null;
    }
}