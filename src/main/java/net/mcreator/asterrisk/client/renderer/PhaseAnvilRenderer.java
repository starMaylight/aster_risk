package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.block.entity.PhaseAnvilBlockEntity;
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
 * 月相の金床のアイテムレンダラー
 * 金床の上に装備と刻印を表示
 */
public class PhaseAnvilRenderer implements BlockEntityRenderer<PhaseAnvilBlockEntity> {

    private final ItemRenderer itemRenderer;

    public PhaseAnvilRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(PhaseAnvilBlockEntity anvil, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        Level level = anvil.getLevel();
        if (level == null) return;

        long gameTime = level.getGameTime();
        BlockPos pos = anvil.getBlockPos();
        int lightLevel = getLightLevel(level, pos.above());

        ItemStack equipment = anvil.getEquipment();
        ItemStack sigil = anvil.getSigil();

        // 装備アイテムの描画（金床の上、左側）
        if (!equipment.isEmpty()) {
            poseStack.pushPose();

            // 金床の上部
            poseStack.translate(0.35, 0.95, 0.5);

            // 処理中は浮遊＆回転
            if (anvil.isProcessing()) {
                float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.2) * 0.05f;
                poseStack.translate(0, bobOffset + 0.1, 0);
                float rotation = (gameTime + partialTick) * 3.0f;
                poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            } else {
                // 静止時は横向き
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            }

            float scale = 0.5f;
            poseStack.scale(scale, scale, scale);

            itemRenderer.renderStatic(
                equipment,
                ItemDisplayContext.FIXED,
                lightLevel,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                level,
                0
            );

            poseStack.popPose();
        }

        // 刻印アイテムの描画（金床の上、右側）
        if (!sigil.isEmpty()) {
            poseStack.pushPose();

            // 金床の上部、右側
            poseStack.translate(0.65, 0.95, 0.5);

            // 浮遊アニメーション
            float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.15) * 0.03f;
            poseStack.translate(0, bobOffset, 0);

            // ゆっくり回転
            float rotation = (gameTime + partialTick) * 2.0f;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            // 処理中は脈動
            float scale = 0.4f;
            if (anvil.isProcessing()) {
                float pulse = (float) Math.sin((gameTime + partialTick) * 0.5) * 0.08f;
                scale += pulse;
            }
            poseStack.scale(scale, scale, scale);

            itemRenderer.renderStatic(
                sigil,
                ItemDisplayContext.FIXED,
                lightLevel,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                level,
                0
            );

            poseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
