package mod.acgaming.jockeys.config;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RegistryHelper
{
    public static Item getItemValueFromName(String name)
    {
        if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)) != null)
        {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        }
        return Items.AIR;
    }
}