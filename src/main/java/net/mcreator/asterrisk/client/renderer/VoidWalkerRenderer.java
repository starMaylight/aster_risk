package net.mcreator.asterrisk.client.renderer;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.VoidWalkerEntity;
import net.mcreator.asterrisk.models.VoidWalkerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class VoidWalkerRenderer extends MobRenderer<VoidWalkerEntity, VoidWalkerModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/void_walker.png");

    public VoidWalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidWalkerModel(context.bakeLayer(VoidWalkerModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidWalkerEntity entity) {
        return TEXTURE;
    }
}
