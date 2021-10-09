package mod.acgaming.batjockeys.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.acgaming.batjockeys.entity.LargeBatEntity;

@OnlyIn(Dist.CLIENT)
public class LargeBatRenderer extends MobRenderer<LargeBatEntity, LargeBatModel>
{
    private static final ResourceLocation BAT_TEXTURES = new ResourceLocation("textures/entity/bat.png");

    public LargeBatRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new LargeBatModel(), 0.5F);
    }

    public ResourceLocation getTextureLocation(LargeBatEntity entity)
    {
        return BAT_TEXTURES;
    }

    protected void setupRotations(LargeBatEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        matrixStackIn.translate(0.0D, MathHelper.cos(ageInTicks * 0.3F) * 0.1F, 0.0D);
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    protected void scale(LargeBatEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
    {
        matrixStackIn.scale(1.05F, 1.05F, 1.05F);
    }
}