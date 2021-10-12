package mod.acgaming.batjockeys;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import mod.acgaming.batjockeys.client.ClientHandler;
import mod.acgaming.batjockeys.config.ConfigHandler;
import mod.acgaming.batjockeys.init.BatJockeysEntities;
import mod.acgaming.batjockeys.init.BatJockeysRegistry;

@Mod(Reference.MOD_ID)
public class BatJockeys
{
    public BatJockeys()
    {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.spec);
        eventBus.register(ConfigHandler.class);
        eventBus.addListener(this::setup);
        BatJockeysRegistry.ENTITIES.register(eventBus);
        eventBus.addListener(BatJockeysEntities::registerEntityAttributes);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(ClientHandler::doClientStuff));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        BatJockeysEntities.initializeEntities();
    }
}