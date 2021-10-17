package mod.acgaming.jockeys.client;

import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.client.renderer.entity.*;
import mod.acgaming.jockeys.init.JockeysRegistry;

@Mod.EventBusSubscriber(modid = Jockeys.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler
{
    public static ModelLayerLocation SKELETON_BAT_LAYER = new ModelLayerLocation(new ResourceLocation(Jockeys.MOD_ID, "skeleton_bat"), "skeleton_bat");
    public static ModelLayerLocation VEX_BAT_LAYER = new ModelLayerLocation(new ResourceLocation(Jockeys.MOD_ID, "vex_bat"), "vex_bat");
    public static ModelLayerLocation WITHER_SKELETON_GHAST_LAYER = new ModelLayerLocation(new ResourceLocation(Jockeys.MOD_ID, "wither_skeleton_ghast"), "wither_skeleton_ghast");
    public static ModelLayerLocation SNIPER_WITHER_SKELETON_LAYER = new ModelLayerLocation(new ResourceLocation(Jockeys.MOD_ID, "sniper_wither_skeleton"), "sniper_wither_skeleton");

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(JockeysRegistry.SKELETON_BAT.get(), SkeletonBatRenderer::new);
        event.registerEntityRenderer(JockeysRegistry.VEX_BAT.get(), VexBatRenderer::new);
        event.registerEntityRenderer(JockeysRegistry.WITHER_SKELETON_GHAST.get(), WitherSkeletonGhastRenderer::new);
        event.registerEntityRenderer(JockeysRegistry.SNIPER_WITHER_SKELETON.get(), SniperWitherSkeletonRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(final EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(SKELETON_BAT_LAYER, SkeletonBatModel::createBodyLayer);
        event.registerLayerDefinition(VEX_BAT_LAYER, VexBatModel::createBodyLayer);
        event.registerLayerDefinition(WITHER_SKELETON_GHAST_LAYER, GhastModel::createBodyLayer);
        event.registerLayerDefinition(SNIPER_WITHER_SKELETON_LAYER, SkeletonModel::createBodyLayer);
    }

    public static void init()
    {
    }
}