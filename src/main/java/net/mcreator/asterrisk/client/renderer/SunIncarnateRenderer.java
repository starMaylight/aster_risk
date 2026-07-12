package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.SunIncarnateEntity;
import net.mcreator.asterrisk.models.SunIncarnateModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class SunIncarnateRenderer extends MobRenderer<SunIncarnateEntity, SunIncarnateModel> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "textures/entity/sun_incarnate.png");

    public SunIncarnateRenderer(EntityRendererProvider.Context context) {
        super(context, new SunIncarnateModel(context.bakeLayer(SunIncarnateModel.LAYER_LOCATION)), 1.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(SunIncarnateEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(SunIncarnateEntity entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.4F, 1.4F, 1.4F);
    }

    @Override
    protected int getBlockLightLevel(SunIncarnateEntity entity, BlockPos pos) {
        // 太陽の化身は常に最大光量で発光
        return 15;
    }
}
