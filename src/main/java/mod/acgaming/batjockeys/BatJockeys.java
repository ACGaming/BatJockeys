package mod.acgaming.batjockeys;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import mod.acgaming.batjockeys.client.ClientHandler;
import mod.acgaming.batjockeys.config.ConfigHandler;
import mod.acgaming.batjockeys.init.BatJockeysRegistry;

@Mod(BatJockeys.MOD_ID)
public class BatJockeys
{
    public static final String MOD_ID = "batjockeys";
    public static final Logger LOGGER = LogManager.getLogger();

    public static void register(IEventBus modBus)
    {
        BatJockeysRegistry.ENTITIES.register(modBus);
        BatJockeysRegistry.ITEMS.register(modBus);
    }

    public BatJockeys()
    {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.SPEC);
        eventBus.register(ConfigHandler.class);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::setupClient);

        register(eventBus);
    }

    public void setup(final FMLCommonSetupEvent event)
    {

    }

    public void setupClient(final FMLClientSetupEvent event)
    {
        ClientHandler.init();
    }
}