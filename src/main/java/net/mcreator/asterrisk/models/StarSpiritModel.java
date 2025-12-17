package net.mcreator.asterrisk.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.StarSpiritEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class StarSpiritModel extends EntityModel<StarSpiritEntity> {
    
    public static final ModelLayerLocation LAYER_LOCATION = 
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "star_spirit"), "main");

    private final ModelPart body;
    private final ModelPart innerCore;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public StarSpiritModel(ModelPart root) {
        this.body = root.getChild("body");
        this.innerCore = root.getChild("inner_core");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
            PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("inner_core", CubeListBuilder.create()
            .texOffs(0, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F),
            PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create()
            .texOffs(24, 0).addBox(0.0F, -3.0F, 0.0F, 6.0F, 6.0F, 1.0F),
            PartPose.offset(4.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create()
            .texOffs(24, 0).addBox(-6.0F, -3.0F, 0.0F, 6.0F, 6.0F, 1.0F),
            PartPose.offset(-4.0F, 16.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 48, 32);
    }

    @Override
    public void setupAnim(StarSpiritEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float bobbing = Mth.sin(ageInTicks * 0.1F) * 0.5F;
        this.body.y = 16.0F + bobbing;
        this.innerCore.y = 16.0F + bobbing;
        this.leftWing.y = 16.0F + bobbing;
        this.rightWing.y = 16.0F + bobbing;
        this.leftWing.yRot = Mth.cos(ageInTicks * 0.3F) * 0.4F;
        this.rightWing.yRot = -Mth.cos(ageInTicks * 0.3F) * 0.4F;
        this.innerCore.yRot = ageInTicks * 0.1F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        innerCore.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
