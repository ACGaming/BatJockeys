package mod.acgaming.jockeys.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.acgaming.jockeys.entity.VexBat;

@OnlyIn(Dist.CLIENT)
public class VexBatRenderer extends MobRenderer<VexBat, VexBatModel>
{
    private static final ResourceLocation VEX_BAT_LOCATION = new ResourceLocation("jockeys:textures/entity/vex_bat.png");

    public VexBatRenderer(EntityRendererProvider.Context p_173929_)
    {
        super(p_173929_, new VexBatModel(p_173929_.bakeLayer(ModelLayers.BAT)), 0.25F);
    }

    public ResourceLocation getTextureLocation(VexBat p_113876_)
    {
        return VEX_BAT_LOCATION;
    }

    protected void setupRotations(VexBat p_113882_, PoseStack p_113883_, float p_113884_, float p_113885_, float p_113886_)
    {
        p_113883_.translate(0.0D, Mth.cos(p_113884_ * 0.3F) * 0.05F, 0.0D);
        super.setupRotations(p_113882_, p_113883_, p_113884_, p_113885_, p_113886_);
    }

    protected void scale(VexBat p_113878_, PoseStack p_113879_, float p_113880_)
    {
        p_113879_.scale(0.5F, 0.5F, 0.5F);
    }
}