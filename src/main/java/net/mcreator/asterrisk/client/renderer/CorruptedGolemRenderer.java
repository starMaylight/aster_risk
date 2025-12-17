package net.mcreator.asterrisk.client.renderer;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.CorruptedGolemEntity;
import net.mcreator.asterrisk.models.CorruptedGolemModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CorruptedGolemRenderer extends MobRenderer<CorruptedGolemEntity, CorruptedGolemModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/corrupted_golem.png");

    public CorruptedGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedGolemModel(context.bakeLayer(CorruptedGolemModel.LAYER_LOCATION)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(CorruptedGolemEntity entity) {
        return TEXTURE;
    }
}
