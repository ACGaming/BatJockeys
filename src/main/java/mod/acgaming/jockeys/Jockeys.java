package mod.acgaming.jockeys;


import java.time.LocalDate;
import java.time.temporal.ChronoField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import mod.acgaming.jockeys.client.ClientHandler;
import mod.acgaming.jockeys.config.ConfigHandler;
import mod.acgaming.jockeys.init.JockeysRegistry;

@Mod(Jockeys.MOD_ID)
public class Jockeys
{
    public static final String MOD_ID = "jockeys";
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean trickortreat;

    public static void register(IEventBus modBus)
    {
        JockeysRegistry.ENTITIES.register(modBus);
        JockeysRegistry.ITEMS.register(modBus);
    }

    public static boolean isHalloween()
    {
        LocalDate localdate = LocalDate.now();
        int day = localdate.get(ChronoField.DAY_OF_MONTH);
        int month = localdate.get(ChronoField.MONTH_OF_YEAR);
        return month == 10 && day >= 1 || month == 11 && day <= 1;
    }

    public Jockeys()
    {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.SPEC);
        eventBus.register(ConfigHandler.class);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::setupClient);

        register(eventBus);

        if (ModList.get().isLoaded("trickortreat"))
        {
            trickortreat = true;
        }
    }

    public void setup(final FMLCommonSetupEvent event)
    {

    }

    public void setupClient(final FMLClientSetupEvent event)
    {
        ClientHandler.init();
    }
}