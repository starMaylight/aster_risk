package net.mcreator.asterrisk.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.EclipseMonarchEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 月蝕の王 - 大型ボスモデル
 */
public class EclipseMonarchModel extends EntityModel<EclipseMonarchEntity> {
    
    public static final ModelLayerLocation LAYER_LOCATION = 
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "eclipse_monarch"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart crown;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart cloak;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public EclipseMonarchModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.crown = this.head.getChild("crown");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.cloak = root.getChild("cloak");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 体（ローブのような形）
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-6.0F, -12.0F, -4.0F, 12.0F, 18.0F, 8.0F),
            PartPose.offset(0.0F, 6.0F, 0.0F));

        // 頭
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 26).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F),
            PartPose.offset(0.0F, -6.0F, 0.0F));

        // 王冠
        head.addOrReplaceChild("crown", CubeListBuilder.create()
            .texOffs(40, 0).addBox(-6.0F, -4.0F, -6.0F, 12.0F, 4.0F, 12.0F),
            PartPose.offset(0.0F, -10.0F, 0.0F));

        // 左腕
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(40, 16).addBox(0.0F, -2.0F, -2.0F, 4.0F, 16.0F, 4.0F),
            PartPose.offset(6.0F, -4.0F, 0.0F));

        // 右腕
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(40, 16).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 16.0F, 4.0F),
            PartPose.offset(-6.0F, -4.0F, 0.0F));

        // マント
        partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create()
            .texOffs(0, 46).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 16.0F, 2.0F),
            PartPose.offset(0.0F, 6.0F, 4.0F));

        // 左翼
        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create()
            .texOffs(56, 36).addBox(0.0F, -8.0F, 0.0F, 16.0F, 20.0F, 2.0F),
            PartPose.offset(6.0F, -2.0F, 4.0F));

        // 右翼
        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create()
            .texOffs(56, 36).addBox(-16.0F, -8.0F, 0.0F, 16.0F, 20.0F, 2.0F),
            PartPose.offset(-6.0F, -2.0F, 4.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(EclipseMonarchEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        
        // 浮遊アニメーション
        float bobbing = Mth.sin(ageInTicks * 0.08F) * 0.5F;
        this.body.y = 6.0F + bobbing;
        this.head.y = -6.0F + bobbing;
        this.leftArm.y = -4.0F + bobbing;
        this.rightArm.y = -4.0F + bobbing;
        this.cloak.y = 6.0F + bobbing;
        this.leftWing.y = -2.0F + bobbing;
        this.rightWing.y = -2.0F + bobbing;
        
        // 腕のアニメーション
        this.leftArm.xRot = Mth.sin(ageInTicks * 0.1F) * 0.2F;
        this.rightArm.xRot = -Mth.sin(ageInTicks * 0.1F) * 0.2F;
        this.leftArm.zRot = Mth.cos(ageInTicks * 0.08F) * 0.1F - 0.2F;
        this.rightArm.zRot = -Mth.cos(ageInTicks * 0.08F) * 0.1F + 0.2F;
        
        // 翼のアニメーション
        float wingFlap = Mth.sin(ageInTicks * 0.15F) * 0.3F;
        this.leftWing.yRot = wingFlap + 0.3F;
        this.rightWing.yRot = -wingFlap - 0.3F;
        
        // マントの揺れ
        this.cloak.xRot = Mth.sin(ageInTicks * 0.06F) * 0.1F + 0.1F;
        
        // フェーズに応じた動き
        int phase = entity.getPhase();
        if (phase >= 2) {
            this.leftArm.xRot += Mth.sin(ageInTicks * 0.2F) * 0.1F;
            this.rightArm.xRot -= Mth.sin(ageInTicks * 0.2F) * 0.1F;
        }
        if (phase >= 3) {
            this.leftWing.yRot += Mth.sin(ageInTicks * 0.3F) * 0.2F;
            this.rightWing.yRot -= Mth.sin(ageInTicks * 0.3F) * 0.2F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        cloak.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
