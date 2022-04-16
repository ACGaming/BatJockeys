package mod.acgaming.jockeys.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;

import mod.acgaming.jockeys.client.renderer.entity.SkeletonBatRenderer;
import mod.acgaming.jockeys.entity.SkeletonBat;

public class ClientHandler
{
    public static void initModels()
    {
        RenderingRegistry.registerEntityRenderingHandler(SkeletonBat.class, SkeletonBatRenderer.FACTORY);
    }
}