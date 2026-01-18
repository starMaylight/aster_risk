package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.block.entity.CelestialEnchantingTableBlockEntity;
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
 * 天体エンチャント台のアイテムレンダラー
 * テーブル上にアイテムを浮遊表示、エンチャント中は特殊エフェクト
 */
public class CelestialEnchantingTableRenderer implements BlockEntityRenderer<CelestialEnchantingTableBlockEntity> {

    private final ItemRenderer itemRenderer;

    public CelestialEnchantingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(CelestialEnchantingTableBlockEntity table, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        ItemStack stack = table.getItem();
        if (stack.isEmpty()) return;

        Level level = table.getLevel();
        if (level == null) return;

        long gameTime = level.getGameTime();
        BlockPos pos = table.getBlockPos();
        int light = getLightLevel(level, pos.above());

        poseStack.pushPose();

        // テーブルの上に配置
        poseStack.translate(0.5, 1.0, 0.5);

        // エンチャント中かどうかで表示を変える
        if (table.isEnchanting()) {
            renderEnchantingItem(table, partialTick, poseStack, buffer, light, level, gameTime, stack);
        } else {
            renderIdleItem(table, partialTick, poseStack, buffer, light, level, gameTime, stack);
        }

        poseStack.popPose();
    }

    /**
     * 通常時のアイテム表示
     */
    private void renderIdleItem(CelestialEnchantingTableBlockEntity table, float partialTick,
                                PoseStack poseStack, MultiBufferSource buffer,
                                int light, Level level, long gameTime, ItemStack stack) {
        
        // ゆっくり浮遊
        float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.08) * 0.08f;
        poseStack.translate(0, bobOffset + 0.3, 0);

        // ゆっくり回転
        float rotation = (gameTime + partialTick) * 1.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // 少し傾ける（本のような見た目）
        poseStack.mulPose(Axis.XP.rotationDegrees(15));

        // スケール
        poseStack.scale(0.6f, 0.6f, 0.6f);

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
    }

    /**
     * エンチャント中のアイテム表示
     */
    private void renderEnchantingItem(CelestialEnchantingTableBlockEntity table, float partialTick,
                                      PoseStack poseStack, MultiBufferSource buffer,
                                      int light, Level level, long gameTime, ItemStack stack) {
        
        float progress = (float) table.getEnchantProgress() / table.getEnchantTime();
        
        // 上昇しながら浮遊
        float baseHeight = 0.3f + progress * 0.5f;
        float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.2) * 0.05f;
        poseStack.translate(0, baseHeight + bobOffset, 0);

        // 高速回転（進捗に応じて加速）
        float rotationSpeed = 2.0f + progress * 8.0f;
        float rotation = (gameTime + partialTick) * rotationSpeed;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // 脈動するスケール
        float pulse = (float) Math.sin((gameTime + partialTick) * 0.4) * 0.05f;
        float scale = 0.5f + progress * 0.2f + pulse;
        poseStack.scale(scale, scale, scale);

        // アイテム描画（明るめのライト）
        int brightLight = LightTexture.pack(15, 15);
        itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            brightLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            level,
            0
        );
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
