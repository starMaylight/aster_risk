package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.block.entity.FocusChamberCoreBlockEntity;
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

import java.util.List;

/**
 * 集光チャンバーコアのアイテムレンダラー
 * チャンバー内のアイテムを円形に配置して表示
 */
public class FocusChamberCoreRenderer implements BlockEntityRenderer<FocusChamberCoreBlockEntity> {

    private final ItemRenderer itemRenderer;

    public FocusChamberCoreRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(FocusChamberCoreBlockEntity chamber, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        List<ItemStack> items = chamber.getStoredItems();
        if (items.isEmpty()) return;

        Level level = chamber.getLevel();
        if (level == null) return;

        // 構造が有効な場合のみ表示
        if (!chamber.isStructureValid()) return;

        long gameTime = level.getGameTime();
        BlockPos pos = chamber.getBlockPos();
        int light = getLightLevel(level, pos);

        poseStack.pushPose();

        // チャンバー中央に移動
        poseStack.translate(0.5, 0.5, 0.5);

        // アイテム数に応じて円形に配置
        int itemCount = items.size();
        float baseAngle = (gameTime + partialTick) * 1.5f; // 全体の回転

        for (int i = 0; i < itemCount; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            poseStack.pushPose();

            // 円形配置の角度
            float itemAngle = baseAngle + (360.0f / itemCount) * i;
            float radius = itemCount == 1 ? 0 : 0.3f; // 1個の場合は中央

            // 位置計算
            double x = Math.cos(Math.toRadians(itemAngle)) * radius;
            double z = Math.sin(Math.toRadians(itemAngle)) * radius;
            
            // 浮遊アニメーション（各アイテムで位相をずらす）
            float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.15 + i * 0.5) * 0.05f;
            
            poseStack.translate(x, bobOffset, z);

            // アイテム自体も回転
            poseStack.mulPose(Axis.YP.rotationDegrees(-itemAngle + 90));

            // スケール（処理中は少し大きく）
            float scale = chamber.isProcessing() ? 0.4f : 0.35f;
            poseStack.scale(scale, scale, scale);

            // アイテム描画
            itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                light,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                level,
                0
            );

            poseStack.popPose();
        }

        // 処理中のエフェクト：中央に結果アイテムのゴースト表示
        if (chamber.isProcessing()) {
            renderProcessingEffect(chamber, partialTick, poseStack, buffer, light, level, gameTime);
        }

        poseStack.popPose();
    }

    /**
     * 処理中の中央エフェクト
     */
    private void renderProcessingEffect(FocusChamberCoreBlockEntity chamber, float partialTick,
                                        PoseStack poseStack, MultiBufferSource buffer, 
                                        int light, Level level, long gameTime) {
        // 進捗に応じた透明度（後で実装可能）
        float progress = (float) chamber.getProcessProgress() / chamber.getProcessTime();
        
        poseStack.pushPose();
        
        // 上下に脈動
        float pulse = (float) Math.sin((gameTime + partialTick) * 0.3) * 0.1f * progress;
        poseStack.translate(0, pulse + 0.2, 0);
        
        // 高速回転
        poseStack.mulPose(Axis.YP.rotationDegrees((gameTime + partialTick) * 5.0f));
        
        poseStack.scale(0.3f * progress, 0.3f * progress, 0.3f * progress);
        
        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
