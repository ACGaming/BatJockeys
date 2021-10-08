package mod.acgaming.batjockeys.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import mod.acgaming.batjockeys.BatJockeys;
import mod.acgaming.batjockeys.client.renderer.entity.LargeBatModel;
import mod.acgaming.batjockeys.client.renderer.entity.LargeBatRenderer;
import mod.acgaming.batjockeys.init.BatJockeysRegistry;

@Mod.EventBusSubscriber(modid = BatJockeys.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler
{
    public static ModelLayerLocation LARGE_BAT_LAYER = new ModelLayerLocation(new ResourceLocation(BatJockeys.MOD_ID, "large_bat"), "large_bat");

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(BatJockeysRegistry.LARGE_BAT.get(), LargeBatRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(LARGE_BAT_LAYER, LargeBatModel::createBodyLayer);
    }

    public static void init()
    {
    }
}