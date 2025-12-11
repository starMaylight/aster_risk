package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.block.entity.StarAnvilBlockEntity;
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
 * Star Anvilのアイテムレンダラー
 * 金床の上にアイテムを横に寝かせて表示
 */
public class StarAnvilRenderer implements BlockEntityRenderer<StarAnvilBlockEntity> {

    private final ItemRenderer itemRenderer;

    public StarAnvilRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(StarAnvilBlockEntity anvil, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        ItemStack stack = anvil.getItem();
        if (stack.isEmpty()) return;

        Level level = anvil.getLevel();
        if (level == null) return;

        poseStack.pushPose();

        // アイテムの位置（金床の作業面の上）- 作業面は Y=12/16 = 0.75
        poseStack.translate(0.5, 0.85, 0.5);

        // 横に寝かせる（X軸で90度回転）
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        // ゆっくり回転
        long gameTime = level.getGameTime();
        float rotation = (gameTime + partialTick) * 0.5f;
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));

        // スケール調整
        poseStack.scale(0.6f, 0.6f, 0.6f);

        // ライティング計算
        BlockPos pos = anvil.getBlockPos();
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
