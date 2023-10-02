package mod.acgaming.jockeys.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;

import mod.acgaming.jockeys.client.renderer.entity.CandyBombRenderer;
import mod.acgaming.jockeys.client.renderer.entity.SkeletonBatRenderer;
import mod.acgaming.jockeys.client.renderer.entity.WitherSkeletonGhastRenderer;
import mod.acgaming.jockeys.entity.CandyBomb;
import mod.acgaming.jockeys.entity.SkeletonBat;
import mod.acgaming.jockeys.entity.WitherSkeletonGhast;

public class ClientHandler
{
    public static void initModels()
    {
        RenderingRegistry.registerEntityRenderingHandler(SkeletonBat.class, SkeletonBatRenderer.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(WitherSkeletonGhast.class, WitherSkeletonGhastRenderer.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(CandyBomb.class, CandyBombRenderer.FACTORY);
    }
}