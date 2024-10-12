package mod.acgaming.jockeys.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import mod.acgaming.jockeys.config.JockeysConfig;

public class JockeysHelper
{
    public static List<Item> dropList = new ArrayList<>();

    public static Item getItemValueFromName(String name)
    {
        if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)) != null)
        {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        }
        return Items.AIR;
    }

    public static void initHalloweenDropList()
    {
        for (String name : JockeysConfig.GENERAL_SETTINGS.halloweenDrops)
        {
            Item item = getItemValueFromName(name);
            if (item != Items.AIR) dropList.add(item);
        }
    }

    public static Item getRandomHalloweenDrop(World world)
    {
        if (dropList.isEmpty()) return Items.AIR;
        return dropList.get(world.rand.nextInt(dropList.size()));
    }
}
