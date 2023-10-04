package mod.acgaming.jockeys;

import java.util.Calendar;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mod.acgaming.jockeys.client.ClientHandler;
import mod.acgaming.jockeys.config.JockeysConfig;
import mod.acgaming.jockeys.util.JockeysHelper;

@Mod(modid = Jockeys.MOD_ID, version = Jockeys.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class Jockeys
{
    public static final String MOD_ID = "jockeys";
    public static final String VERSION = "1.0.0";

    @Instance
    public static Jockeys instance;

    public static boolean isSpookySeason(World world)
    {
        if (JockeysConfig.GENERAL_SETTINGS.alwaysSpookySeason) return true;
        Calendar calendar = world.getCurrentDate();
        return calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) >= 20 || calendar.get(Calendar.MONTH) + 1 == 11 && calendar.get(Calendar.DATE) <= 3;
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void preInitClient(FMLPreInitializationEvent event)
    {
        ClientHandler.initModels();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        JockeysHelper.initHalloweenDropList();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}