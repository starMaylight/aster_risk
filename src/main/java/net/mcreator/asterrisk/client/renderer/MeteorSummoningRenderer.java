package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.block.entity.MeteorSummoningBlockEntity;
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
 * 流星召喚陣のアイテムレンダラー
 * 召喚陣の中央に核を浮遊表示
 */
public class MeteorSummoningRenderer implements BlockEntityRenderer<MeteorSummoningBlockEntity> {

    private final ItemRenderer itemRenderer;

    public MeteorSummoningRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(MeteorSummoningBlockEntity summoner, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        ItemStack coreStack = summoner.getCore();
        if (coreStack.isEmpty()) return;

        Level level = summoner.getLevel();
        if (level == null) return;

        long gameTime = level.getGameTime();
        BlockPos pos = summoner.getBlockPos();
        int lightLevel = getLightLevel(level, pos.above());

        poseStack.pushPose();

        // 召喚陣の中央上部
        poseStack.translate(0.5, 0.5, 0.5);

        // 召喚中はより高く浮遊
        float baseHeight = summoner.isSummoning() ? 0.8f : 0.3f;
        float bobOffset = (float) Math.sin((gameTime + partialTick) * 0.2) * 0.1f;
        poseStack.translate(0, baseHeight + bobOffset, 0);

        // 回転（召喚中は高速）
        float rotationSpeed = summoner.isSummoning() ? 8.0f : 2.0f;
        float rotation = (gameTime + partialTick) * rotationSpeed;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // 傾き
        poseStack.mulPose(Axis.XP.rotationDegrees(15));
        poseStack.mulPose(Axis.ZP.rotationDegrees(10));

        // スケール（召喚中は脈動）
        float scale = 0.6f;
        if (summoner.isSummoning()) {
            float pulse = (float) Math.sin((gameTime + partialTick) * 0.5) * 0.15f;
            scale += pulse;
        }
        poseStack.scale(scale, scale, scale);

        // アイテム描画
        itemRenderer.renderStatic(
            coreStack,
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

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
