package mod.acgaming.jockeys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JockeysTab extends CreativeTabs
{
    public static final List<ItemStack> EGGS = new ArrayList<>();

    public JockeysTab()
    {
        super(Jockeys.MODID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack(Blocks.SKULL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> list)
    {
        super.displayAllRelevantItems(list);
        list.addAll(EGGS);
    }
}