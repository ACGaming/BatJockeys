package mod.acgaming.jockeys.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.client.renderer.entity.model.CandyBombModel;
import mod.acgaming.jockeys.entity.CandyBomb;

@SideOnly(Side.CLIENT)
public class CandyBombRenderer extends Render<CandyBomb>
{
    public static final CandyBombRenderer.Factory FACTORY = new CandyBombRenderer.Factory();
    public final CandyBombModel candyBombModel = new CandyBombModel();

    public CandyBombRenderer(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    public float getRenderYaw(float p_82400_1_, float p_82400_2_, float p_82400_3_)
    {
        float f;

        for (f = p_82400_2_ - p_82400_1_; f < -180.0F; f += 360.0F)
        {
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return p_82400_1_ + p_82400_3_ * f;
    }

    public void doRender(CandyBomb entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        float f = this.getRenderYaw(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
        float f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        this.bindEntityTexture(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.candyBombModel.render(entity, 0.0F, 0.0F, 0.0F, f, f1, 0.0625F);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public ResourceLocation getEntityTexture(CandyBomb entity)
    {
        return new ResourceLocation(Jockeys.MOD_ID, "textures/entity/candy_bomb.png");
    }

    public static class Factory implements IRenderFactory<CandyBomb>
    {
        @Override
        public Render<? super CandyBomb> createRenderFor(RenderManager manager)
        {
            return new CandyBombRenderer(manager);
        }
    }
}