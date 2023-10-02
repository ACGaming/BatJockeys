package mod.acgaming.jockeys;

import java.time.LocalDate;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mod.acgaming.jockeys.client.ClientHandler;

@Mod(modid = Jockeys.MOD_ID, version = Jockeys.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class Jockeys
{
    public static final String MOD_ID = "jockeys";
    public static final String VERSION = "1.0.0";

    @Instance
    public static Jockeys instance;

    public static boolean isSpookySeason()
    {
        LocalDate localdate = LocalDate.now();
        int month = localdate.getMonth().getValue();
        int day = localdate.getDayOfMonth();
        return month == 10 || month == 11 && day == 1;
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
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}