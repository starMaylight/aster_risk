package net.mcreator.asterrisk.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.MoonlightFairyEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MoonlightFairyModel extends EntityModel<MoonlightFairyEntity> {
    
    public static final ModelLayerLocation LAYER_LOCATION = 
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "moonlight_fairy"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public MoonlightFairyModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 8).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 5.0F, 2.0F),
            PartPose.offset(0.0F, 20.0F, 0.0F));

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F),
            PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create()
            .texOffs(16, 0).addBox(0.0F, -3.0F, 0.0F, 4.0F, 5.0F, 1.0F),
            PartPose.offset(2.0F, 18.0F, 1.0F));

        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create()
            .texOffs(16, 0).addBox(-4.0F, -3.0F, 0.0F, 4.0F, 5.0F, 1.0F),
            PartPose.offset(-2.0F, 18.0F, 1.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(12, 8).addBox(0.0F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F),
            PartPose.offset(2.0F, 16.5F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(12, 8).addBox(-1.0F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F),
            PartPose.offset(-2.0F, 16.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    @Override
    public void setupAnim(MoonlightFairyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        
        float bobbing = Mth.sin(ageInTicks * 0.15F) * 0.3F;
        this.body.y = 20.0F + bobbing;
        this.head.y = 16.0F + bobbing;
        this.leftWing.y = 18.0F + bobbing;
        this.rightWing.y = 18.0F + bobbing;
        this.leftArm.y = 16.5F + bobbing;
        this.rightArm.y = 16.5F + bobbing;
        
        this.leftWing.yRot = Mth.cos(ageInTicks * 0.5F) * 0.6F + 0.2F;
        this.rightWing.yRot = -Mth.cos(ageInTicks * 0.5F) * 0.6F - 0.2F;
        this.leftArm.xRot = Mth.sin(ageInTicks * 0.1F) * 0.2F;
        this.rightArm.xRot = -Mth.sin(ageInTicks * 0.1F) * 0.2F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
