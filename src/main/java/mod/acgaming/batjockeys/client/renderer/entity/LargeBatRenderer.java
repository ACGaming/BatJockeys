package mod.acgaming.batjockeys.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.acgaming.batjockeys.entity.LargeBat;

@OnlyIn(Dist.CLIENT)
public class LargeBatRenderer extends MobRenderer<LargeBat, LargeBatModel>
{
    private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

    public LargeBatRenderer(EntityRendererProvider.Context p_173929_)
    {
        super(p_173929_, new LargeBatModel(p_173929_.bakeLayer(ModelLayers.BAT)), 0.25F);
    }

    public ResourceLocation getTextureLocation(LargeBat p_113876_)
    {
        return BAT_LOCATION;
    }

    protected void setupRotations(LargeBat p_113882_, PoseStack p_113883_, float p_113884_, float p_113885_, float p_113886_)
    {
        p_113883_.translate(0.0D, Mth.cos(p_113884_ * 0.3F) * 0.1F, 0.0D);
        super.setupRotations(p_113882_, p_113883_, p_113884_, p_113885_, p_113886_);
    }

    protected void scale(LargeBat p_113878_, PoseStack p_113879_, float p_113880_)
    {
        p_113879_.scale(1.05F, 1.05F, 1.05F);
    }
}