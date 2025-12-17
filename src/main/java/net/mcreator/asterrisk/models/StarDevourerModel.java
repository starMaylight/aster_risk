package net.mcreator.asterrisk.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.StarDevourerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 星喰らい - 巨大なコズミックホラー風ボス
 */
public class StarDevourerModel extends EntityModel<StarDevourerEntity> {
    
    public static final ModelLayerLocation LAYER_LOCATION = 
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "star_devourer"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart tail;
    private final ModelPart spines;

    public StarDevourerModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.tail = root.getChild("tail");
        this.spines = root.getChild("spines");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 巨大な体
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-10.0F, -15.0F, -8.0F, 20.0F, 20.0F, 16.0F),
            PartPose.offset(0.0F, 4.0F, 0.0F));

        // 頭（大きな口を持つ）
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 36).addBox(-8.0F, -10.0F, -12.0F, 16.0F, 12.0F, 14.0F),
            PartPose.offset(0.0F, -8.0F, -6.0F));

        // 顎（動く）
        head.addOrReplaceChild("jaw", CubeListBuilder.create()
            .texOffs(60, 36).addBox(-7.0F, 0.0F, -10.0F, 14.0F, 4.0F, 12.0F),
            PartPose.offset(0.0F, 2.0F, -2.0F));

        // 左腕（太くて長い）
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(72, 0).addBox(0.0F, -3.0F, -4.0F, 8.0F, 24.0F, 8.0F),
            PartPose.offset(10.0F, -6.0F, 0.0F));

        // 右腕
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(72, 0).addBox(-8.0F, -3.0F, -4.0F, 8.0F, 24.0F, 8.0F),
            PartPose.offset(-10.0F, -6.0F, 0.0F));

        // 左足
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
            .texOffs(0, 62).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 20.0F, 8.0F),
            PartPose.offset(6.0F, 4.0F, 0.0F));

        // 右足
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
            .texOffs(0, 62).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 20.0F, 8.0F),
            PartPose.offset(-6.0F, 4.0F, 0.0F));

        // 尻尾
        partdefinition.addOrReplaceChild("tail", CubeListBuilder.create()
            .texOffs(56, 52).addBox(-4.0F, -2.0F, 0.0F, 8.0F, 6.0F, 16.0F),
            PartPose.offset(0.0F, 2.0F, 8.0F));

        // 背中のトゲ
        partdefinition.addOrReplaceChild("spines", CubeListBuilder.create()
            .texOffs(104, 0).addBox(-3.0F, -8.0F, -6.0F, 6.0F, 8.0F, 12.0F),
            PartPose.offset(0.0F, -11.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(StarDevourerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        
        // 歩行アニメーション
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.4F) * 0.8F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.4F + (float)Math.PI) * 0.8F * limbSwingAmount;
        
        // 腕の揺れ
        this.leftArm.xRot = Mth.cos(limbSwing * 0.4F + (float)Math.PI) * 0.5F * limbSwingAmount;
        this.rightArm.xRot = Mth.cos(limbSwing * 0.4F) * 0.5F * limbSwingAmount;
        this.leftArm.zRot = Mth.sin(ageInTicks * 0.05F) * 0.1F - 0.2F;
        this.rightArm.zRot = -Mth.sin(ageInTicks * 0.05F) * 0.1F + 0.2F;
        
        // 顎の動き
        this.jaw.xRot = Mth.sin(ageInTicks * 0.1F) * 0.15F + 0.1F;
        
        // 尻尾の揺れ
        this.tail.yRot = Mth.sin(ageInTicks * 0.08F) * 0.3F;
        this.tail.xRot = Mth.cos(ageInTicks * 0.06F) * 0.1F - 0.1F;
        
        // トゲの微動
        this.spines.xRot = Mth.sin(ageInTicks * 0.03F) * 0.05F;
        
        // チャージ中の特別アニメーション
        if (entity.isCharging()) {
            this.leftArm.xRot = -1.5F;
            this.rightArm.xRot = -1.5F;
            this.leftArm.zRot = 0.5F;
            this.rightArm.zRot = -0.5F;
            this.jaw.xRot = 0.8F;
        }
        
        // フェーズに応じた追加動き
        int phase = entity.getPhase();
        if (phase >= 2) {
            this.spines.xRot += Mth.sin(ageInTicks * 0.1F) * 0.1F;
        }
        if (phase >= 3) {
            this.body.y = 4.0F + Mth.sin(ageInTicks * 0.15F) * 0.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        spines.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
