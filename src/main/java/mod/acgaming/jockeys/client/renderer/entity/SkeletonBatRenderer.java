package mod.acgaming.jockeys.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mod.acgaming.jockeys.client.renderer.entity.model.SkeletonBatModel;
import mod.acgaming.jockeys.entity.SkeletonBat;

@SideOnly(Side.CLIENT)
public class SkeletonBatRenderer extends RenderLiving<SkeletonBat>
{
    public static final SkeletonBatRenderer.Factory FACTORY = new SkeletonBatRenderer.Factory();
    public static final ResourceLocation BAT_TEXTURES = new ResourceLocation("textures/entity/bat.png");

    public SkeletonBatRenderer(RenderManager renderManager)
    {
        super(renderManager, new SkeletonBatModel(), 0.25F);
    }

    public ResourceLocation getEntityTexture(SkeletonBat entity)
    {
        return BAT_TEXTURES;
    }

    public void applyRotations(SkeletonBat entityLiving, float f1, float rotationYaw, float partialTicks)
    {
        GlStateManager.translate(0.0F, MathHelper.cos(f1 * 0.15F) * 0.1F, 0.0F);
        super.applyRotations(entityLiving, f1, rotationYaw, partialTicks);
    }

    public void preRenderCallback(SkeletonBat entity, float partialTickTime)
    {
        GlStateManager.scale(1.05F, 1.05F, 1.05F);
    }

    public static class Factory implements IRenderFactory<SkeletonBat>
    {
        @Override
        public Render<? super SkeletonBat> createRenderFor(RenderManager manager)
        {
            return new SkeletonBatRenderer(manager);
        }
    }
}
