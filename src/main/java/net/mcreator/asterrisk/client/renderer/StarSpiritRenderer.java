package net.mcreator.asterrisk.client.renderer;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.StarSpiritEntity;
import net.mcreator.asterrisk.models.StarSpiritModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StarSpiritRenderer extends MobRenderer<StarSpiritEntity, StarSpiritModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/star_spirit.png");

    public StarSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new StarSpiritModel(context.bakeLayer(StarSpiritModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(StarSpiritEntity entity) {
        return TEXTURE;
    }
}
