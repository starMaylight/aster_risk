package net.mcreator.asterrisk.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.SunIncarnateEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 陽の化身 - 燃え盛る太陽の巨人
 */
public class SunIncarnateModel extends EntityModel<SunIncarnateEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "sun_incarnate"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart corona;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart halo;

    public SunIncarnateModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.corona = this.head.getChild("corona");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.halo = root.getChild("halo");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 灼熱の胴体
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-9.0F, -14.0F, -6.0F, 18.0F, 18.0F, 12.0F),
            PartPose.offset(0.0F, 2.0F, 0.0F));

        // 頭（太陽核）
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 30).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F),
            PartPose.offset(0.0F, -12.0F, 0.0F));

        // コロナ（頭部から放射する炎の飾り）
        head.addOrReplaceChild("corona", CubeListBuilder.create()
            .texOffs(48, 30).addBox(-9.0F, -15.0F, 0.0F, 18.0F, 18.0F, 0.0F),
            PartPose.offset(0.0F, -3.0F, 0.0F));

        // 左腕（燃える巨腕）
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(60, 0).addBox(0.0F, -2.0F, -3.5F, 7.0F, 22.0F, 7.0F),
            PartPose.offset(9.0F, -10.0F, 0.0F));

        // 右腕
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(60, 0).addBox(-7.0F, -2.0F, -3.5F, 7.0F, 22.0F, 7.0F),
            PartPose.offset(-9.0F, -10.0F, 0.0F));

        // 左脚
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
            .texOffs(0, 54).addBox(-3.5F, 0.0F, -3.5F, 7.0F, 18.0F, 7.0F),
            PartPose.offset(5.0F, 6.0F, 0.0F));

        // 右脚
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
            .texOffs(0, 54).addBox(-3.5F, 0.0F, -3.5F, 7.0F, 18.0F, 7.0F),
            PartPose.offset(-5.0F, 6.0F, 0.0F));

        // 背後の光輪
        partdefinition.addOrReplaceChild("halo", CubeListBuilder.create()
            .texOffs(28, 54).addBox(-11.0F, -11.0F, 0.0F, 22.0F, 22.0F, 0.0F),
            PartPose.offset(0.0F, -14.0F, 5.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SunIncarnateEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        // 歩行
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.5F) * 0.7F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.5F + (float) Math.PI) * 0.7F * limbSwingAmount;

        // 腕の揺れ
        this.leftArm.xRot = Mth.cos(limbSwing * 0.5F + (float) Math.PI) * 0.5F * limbSwingAmount;
        this.rightArm.xRot = Mth.cos(limbSwing * 0.5F) * 0.5F * limbSwingAmount;
        this.leftArm.zRot = Mth.sin(ageInTicks * 0.06F) * 0.08F - 0.15F;
        this.rightArm.zRot = -Mth.sin(ageInTicks * 0.06F) * 0.08F + 0.15F;

        // コロナと光輪の脈動・回転
        this.corona.zRot = ageInTicks * 0.02F;
        this.halo.zRot = -ageInTicks * 0.015F;

        // 突進中は両腕を前へ
        if (entity.isCharging()) {
            this.leftArm.xRot = -1.4F;
            this.rightArm.xRot = -1.4F;
            this.leftArm.zRot = 0.3F;
            this.rightArm.zRot = -0.3F;
        }

        // 浮遊感のある上下動
        this.body.y = 2.0F + Mth.sin(ageInTicks * 0.08F) * 0.4F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        halo.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
