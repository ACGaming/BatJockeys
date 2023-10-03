package mod.acgaming.jockeys.client.renderer.entity;

import mod.acgaming.jockeys.Jockeys;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mod.acgaming.jockeys.entity.WitherSkeletonGhast;

@SideOnly(Side.CLIENT)
public class WitherSkeletonGhastRenderer extends RenderLiving<WitherSkeletonGhast>
{
    public static final WitherSkeletonGhastRenderer.Factory FACTORY = new WitherSkeletonGhastRenderer.Factory();
    public static final ResourceLocation GHAST_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast.png");
    public static final ResourceLocation GHAST_TEXTURES_SPOOKY = new ResourceLocation(Jockeys.MOD_ID,"textures/entity/pumpkin_ghast.png");
    public static final ResourceLocation GHAST_SHOOTING_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");
    public static final ResourceLocation GHAST_SHOOTING_TEXTURES_SPOOKY = new ResourceLocation(Jockeys.MOD_ID,"textures/entity/pumpkin_ghast_shooting.png");

    public WitherSkeletonGhastRenderer(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelGhast(), 0.5F);
    }

    public ResourceLocation getEntityTexture(WitherSkeletonGhast entity)
    {
        if (Jockeys.isSpookySeason(entity.world)) return entity.isAttacking() ? GHAST_SHOOTING_TEXTURES_SPOOKY : GHAST_TEXTURES_SPOOKY;
        return entity.isAttacking() ? GHAST_SHOOTING_TEXTURES : GHAST_TEXTURES;
    }

    public void preRenderCallback(WitherSkeletonGhast entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(4.5F, 4.5F, 4.5F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static class Factory implements IRenderFactory<WitherSkeletonGhast>
    {
        @Override
        public Render<? super WitherSkeletonGhast> createRenderFor(RenderManager manager)
        {
            return new WitherSkeletonGhastRenderer(manager);
        }
    }
}