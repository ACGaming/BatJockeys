package mod.acgaming.batjockeys.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import mod.acgaming.batjockeys.client.renderer.entity.LargeBatRenderer;
import mod.acgaming.batjockeys.init.BatJockeysRegistry;

public class ClientHandler
{
    public static void doClientStuff(final FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(BatJockeysRegistry.LARGE_BAT.get(), LargeBatRenderer::new);
    }
}