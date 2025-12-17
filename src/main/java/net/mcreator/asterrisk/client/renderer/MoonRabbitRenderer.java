package net.mcreator.asterrisk.client.renderer;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.MoonRabbitEntity;
import net.mcreator.asterrisk.models.MoonRabbitModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoonRabbitRenderer extends MobRenderer<MoonRabbitEntity, MoonRabbitModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/moon_rabbit.png");

    public MoonRabbitRenderer(EntityRendererProvider.Context context) {
        super(context, new MoonRabbitModel(context.bakeLayer(MoonRabbitModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoonRabbitEntity entity) {
        return TEXTURE;
    }
}
