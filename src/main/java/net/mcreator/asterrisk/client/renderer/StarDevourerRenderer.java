package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.StarDevourerEntity;
import net.mcreator.asterrisk.models.StarDevourerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StarDevourerRenderer extends MobRenderer<StarDevourerEntity, StarDevourerModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/star_devourer.png");

    public StarDevourerRenderer(EntityRendererProvider.Context context) {
        super(context, new StarDevourerModel(context.bakeLayer(StarDevourerModel.LAYER_LOCATION)), 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(StarDevourerEntity entity) {
        return TEXTURE;
    }
    
    @Override
    protected void scale(StarDevourerEntity entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.5F, 1.5F, 1.5F);
    }
}
