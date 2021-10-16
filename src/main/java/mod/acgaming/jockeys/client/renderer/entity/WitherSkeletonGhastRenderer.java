package mod.acgaming.jockeys.client.renderer.entity;

import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.acgaming.jockeys.entity.WitherSkeletonGhast;

@OnlyIn(Dist.CLIENT)
public class WitherSkeletonGhastRenderer extends MobRenderer<WitherSkeletonGhast, GhastModel<WitherSkeletonGhast>>
{
    private static final ResourceLocation GHAST_LOCATION = new ResourceLocation("textures/entity/ghast/ghast.png");
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

    public WitherSkeletonGhastRenderer(EntityRendererProvider.Context p_174129_)
    {
        super(p_174129_, new GhastModel<>(p_174129_.bakeLayer(ModelLayers.GHAST)), 1.5F);
    }

    public ResourceLocation getTextureLocation(WitherSkeletonGhast p_114755_)
    {
        return p_114755_.isCharging() ? GHAST_SHOOTING_LOCATION : GHAST_LOCATION;
    }

    protected void scale(WitherSkeletonGhast p_114757_, PoseStack p_114758_, float p_114759_)
    {
        p_114758_.scale(2.5F, 2.5F, 2.5F);
    }
}