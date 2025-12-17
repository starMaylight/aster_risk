package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.EclipseMonarchEntity;
import net.mcreator.asterrisk.models.EclipseMonarchModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EclipseMonarchRenderer extends MobRenderer<EclipseMonarchEntity, EclipseMonarchModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/eclipse_monarch.png");

    public EclipseMonarchRenderer(EntityRendererProvider.Context context) {
        super(context, new EclipseMonarchModel(context.bakeLayer(EclipseMonarchModel.LAYER_LOCATION)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(EclipseMonarchEntity entity) {
        return TEXTURE;
    }
    
    @Override
    protected void scale(EclipseMonarchEntity entity, PoseStack poseStack, float partialTicks) {
        // ボスなので少し大きめ
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }
}
