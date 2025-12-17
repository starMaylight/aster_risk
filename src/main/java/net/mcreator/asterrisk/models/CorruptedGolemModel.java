package net.mcreator.asterrisk.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.CorruptedGolemEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 堕落した月光石ゴーレム - 大型の敵対Mob
 */
public class CorruptedGolemModel extends EntityModel<CorruptedGolemEntity> {
    
    public static final ModelLayerLocation LAYER_LOCATION = 
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "corrupted_golem"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public CorruptedGolemModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 体（大きな胴体）
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-7.0F, -16.0F, -5.0F, 14.0F, 16.0F, 10.0F),
            PartPose.offset(0.0F, 7.0F, 0.0F));

        // 頭（立方体）
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 26).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
            PartPose.offset(0.0F, -9.0F, 0.0F));

        // 左腕（太い）
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(48, 0).addBox(0.0F, -2.0F, -3.0F, 6.0F, 18.0F, 6.0F),
            PartPose.offset(7.0F, -7.0F, 0.0F));

        // 右腕（太い）
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(48, 0).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 18.0F, 6.0F),
            PartPose.offset(-7.0F, -7.0F, 0.0F));

        // 左足
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
            .texOffs(32, 26).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F),
            PartPose.offset(4.0F, 8.0F, 0.0F));

        // 右足
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
            .texOffs(32, 26).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F),
            PartPose.offset(-4.0F, 8.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(CorruptedGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        
        // 歩行アニメーション（ゆっくり）
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.4F) * 0.8F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.4F + (float)Math.PI) * 0.8F * limbSwingAmount;
        
        // 腕の揺れ
        this.leftArm.xRot = Mth.cos(limbSwing * 0.4F + (float)Math.PI) * 0.6F * limbSwingAmount;
        this.rightArm.xRot = Mth.cos(limbSwing * 0.4F) * 0.6F * limbSwingAmount;
        
        // 腕の常時揺れ
        this.leftArm.zRot = Mth.sin(ageInTicks * 0.05F) * 0.05F;
        this.rightArm.zRot = -Mth.sin(ageInTicks * 0.05F) * 0.05F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
