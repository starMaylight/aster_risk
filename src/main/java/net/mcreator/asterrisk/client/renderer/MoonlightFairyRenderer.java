package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.MoonlightFairyEntity;
import net.mcreator.asterrisk.models.MoonlightFairyModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoonlightFairyRenderer extends MobRenderer<MoonlightFairyEntity, MoonlightFairyModel> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/moonlight_fairy.png");

    public MoonlightFairyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoonlightFairyModel(context.bakeLayer(MoonlightFairyModel.LAYER_LOCATION)), 0.25F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoonlightFairyEntity entity) {
        return TEXTURE;
    }
    
    @Override
    protected void scale(MoonlightFairyEntity entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(0.7F, 0.7F, 0.7F);
    }
}
