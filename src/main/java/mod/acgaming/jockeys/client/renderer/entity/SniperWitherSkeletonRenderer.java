package mod.acgaming.jockeys.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.acgaming.jockeys.entity.AbstractSniperSkeleton;

@OnlyIn(Dist.CLIENT)
public class SniperWitherSkeletonRenderer extends SniperSkeletonRenderer
{
    private static final ResourceLocation WITHER_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

    public SniperWitherSkeletonRenderer(EntityRendererProvider.Context p_174447_)
    {
        super(p_174447_, ModelLayers.WITHER_SKELETON, ModelLayers.WITHER_SKELETON_INNER_ARMOR, ModelLayers.WITHER_SKELETON_OUTER_ARMOR);
    }

    public ResourceLocation getTextureLocation(AbstractSniperSkeleton p_116458_)
    {
        return WITHER_SKELETON_LOCATION;
    }

    protected void scale(AbstractSniperSkeleton p_116460_, PoseStack p_116461_, float p_116462_)
    {
        p_116461_.scale(1.2F, 1.2F, 1.2F);
    }
}