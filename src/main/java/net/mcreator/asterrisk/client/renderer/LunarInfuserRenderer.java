package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.block.entity.LunarInfuserBlockEntity;
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
 * Lunar Infuserのアイテムレンダラー
 * 皿の上にアイテムを浮遊表示し、変換中は回転速度を上げる
 */
public class LunarInfuserRenderer implements BlockEntityRenderer<LunarInfuserBlockEntity> {

    private final ItemRenderer itemRenderer;

    public LunarInfuserRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(LunarInfuserBlockEntity infuser, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        ItemStack stack = infuser.getItem();
        if (stack.isEmpty()) return;

        Level level = infuser.getLevel();
        if (level == null) return;

        poseStack.pushPose();

        // アイテムの位置（皿の上、中央）- Infuserの上部は Y=10/16 = 0.625
        poseStack.translate(0.5, 0.85, 0.5);

        // 浮遊アニメーション
        long gameTime = level.getGameTime();
        float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.1) * 0.03f;
        poseStack.translate(0, bobOffset, 0);

        // 回転アニメーション（変換中は高速回転）
        float rotationSpeed = infuser.isInfusing() ? 8.0f : 1.5f;
        float rotation = (gameTime + partialTick) * rotationSpeed;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // 変換中はスケールを脈動させる
        float scale = 0.5f;
        if (infuser.isInfusing()) {
            float pulse = (float) Math.sin((gameTime + partialTick) * 0.3) * 0.05f;
            scale += pulse;
        }
        poseStack.scale(scale, scale, scale);

        // ライティング計算
        BlockPos pos = infuser.getBlockPos();
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
