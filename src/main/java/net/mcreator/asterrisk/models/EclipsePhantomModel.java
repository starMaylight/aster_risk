package net.mcreator.asterrisk.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.EclipsePhantomEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class EclipsePhantomModel extends EntityModel<EclipsePhantomEntity> {
    
    public static final ModelLayerLocation LAYER_LOCATION = 
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "eclipse_phantom"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart tail;

    public EclipsePhantomModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 体（ローブのような形）
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F),
            PartPose.offset(0.0F, 12.0F, 0.0F));

        // 頭
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
            PartPose.offset(0.0F, 6.0F, 0.0F));

        // 左腕
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(24, 0).addBox(0.0F, -1.0F, -1.0F, 2.0F, 10.0F, 2.0F),
            PartPose.offset(4.0F, 7.0F, 0.0F));

        // 右腕
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(24, 0).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 10.0F, 2.0F),
            PartPose.offset(-4.0F, 7.0F, 0.0F));

        // 尻尾（幽霊の下部）
        partdefinition.addOrReplaceChild("tail", CubeListBuilder.create()
            .texOffs(32, 0).addBox(-3.0F, 0.0F, -1.5F, 6.0F, 8.0F, 3.0F),
            PartPose.offset(0.0F, 18.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(EclipsePhantomEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        
        // 浮遊アニメーション
        float bobbing = Mth.sin(ageInTicks * 0.1F) * 0.3F;
        this.body.y = 12.0F + bobbing;
        this.head.y = 6.0F + bobbing;
        this.leftArm.y = 7.0F + bobbing;
        this.rightArm.y = 7.0F + bobbing;
        this.tail.y = 18.0F + bobbing;
        
        // 腕の揺れ
        this.leftArm.xRot = Mth.sin(ageInTicks * 0.15F) * 0.3F;
        this.rightArm.xRot = -Mth.sin(ageInTicks * 0.15F) * 0.3F;
        this.leftArm.zRot = Mth.cos(ageInTicks * 0.1F) * 0.1F + 0.1F;
        this.rightArm.zRot = -Mth.cos(ageInTicks * 0.1F) * 0.1F - 0.1F;
        
        // 尻尾の揺れ
        this.tail.xRot = Mth.sin(ageInTicks * 0.08F) * 0.2F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
