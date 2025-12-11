package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.block.entity.RitualPedestalBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * 儀式台座のアイテムレンダラー
 * 台座上にアイテムを浮遊表示する
 */
public class RitualPedestalRenderer implements BlockEntityRenderer<RitualPedestalBlockEntity> {

    private final ItemRenderer itemRenderer;

    public RitualPedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(RitualPedestalBlockEntity pedestal, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        ItemStack stack = pedestal.getItem();
        if (stack.isEmpty()) return;

        Level level = pedestal.getLevel();
        if (level == null) return;

        poseStack.pushPose();

        // アイテムの位置（台座の上、中央）
        poseStack.translate(0.5, 1.1, 0.5);

        // 浮遊アニメーション
        long gameTime = level.getGameTime();
        float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.1) * 0.05f;
        poseStack.translate(0, bobOffset, 0);

        // 回転アニメーション
        float rotation = (gameTime + partialTick) * 2.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // スケール調整
        poseStack.scale(0.5f, 0.5f, 0.5f);

        // ライティング計算
        BlockPos pos = pedestal.getBlockPos();
        int lightAbove = getLightLevel(level, pos.above());

        // アイテム描画
        itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            lightAbove,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            level,
            0
        );

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
