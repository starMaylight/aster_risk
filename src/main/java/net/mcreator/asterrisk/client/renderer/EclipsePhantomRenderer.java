package net.mcreator.asterrisk.client.renderer;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.EclipsePhantomEntity;
import net.mcreator.asterrisk.models.EclipsePhantomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EclipsePhantomRenderer extends MobRenderer<EclipsePhantomEntity, EclipsePhantomModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/eclipse_phantom.png");

    public EclipsePhantomRenderer(EntityRendererProvider.Context context) {
        super(context, new EclipsePhantomModel(context.bakeLayer(EclipsePhantomModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(EclipsePhantomEntity entity) {
        return TEXTURE;
    }
}
