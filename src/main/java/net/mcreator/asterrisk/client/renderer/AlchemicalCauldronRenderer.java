package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.entity.AlchemicalCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 錬金釜のアイテム＆水レンダラー
 * 釜の中に月水と材料アイテムを表示
 */
public class AlchemicalCauldronRenderer implements BlockEntityRenderer<AlchemicalCauldronBlockEntity> {

    private final ItemRenderer itemRenderer;
    private static final ResourceLocation MOON_WATER_TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "block/moon_water_still");

    public AlchemicalCauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(AlchemicalCauldronBlockEntity cauldron, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        
        Level level = cauldron.getLevel();
        if (level == null) return;

        long gameTime = level.getGameTime();
        BlockPos pos = cauldron.getBlockPos();
        int lightLevel = getLightLevel(level, pos.above());

        // 月水の描画
        float waterLevel = cauldron.getWaterLevel();
        if (waterLevel > 0) {
            renderWater(cauldron, poseStack, buffer, lightLevel, waterLevel, gameTime, partialTick);
        }

        // アイテムの描画
        List<ItemStack> ingredients = cauldron.getIngredients();
        if (!ingredients.isEmpty()) {
            renderIngredients(cauldron, poseStack, buffer, lightLevel, ingredients, gameTime, partialTick, level);
        }
    }

    private void renderWater(AlchemicalCauldronBlockEntity cauldron, PoseStack poseStack, 
                            MultiBufferSource buffer, int light, float waterLevel,
                            long gameTime, float partialTick) {
        
        // 水面の高さ（底面Y=3/16、最大Y=10/16）
        float minY = 3f / 16f;
        float maxY = 10f / 16f;
        float waterY = minY + (maxY - minY) * Math.min(waterLevel, 1.0f);
        
        // 処理中は水面が波打つ
        if (cauldron.isProcessing()) {
            float wave = (float) Math.sin((gameTime + partialTick) * 0.3) * 0.02f;
            waterY += wave;
        }

        poseStack.pushPose();

        // テクスチャを取得
        TextureAtlasSprite sprite = Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(MOON_WATER_TEXTURE);

        // 半透明レンダリング
        VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        // 水面の範囲（壁の内側）
        float minX = 3f / 16f;
        float maxX = 13f / 16f;
        float minZ = 3f / 16f;
        float maxZ = 13f / 16f;

        // UV座標
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // 水の色（月水は青白い色）
        int r = 180;
        int g = 200;
        int b = 255;
        int a = 200; // 半透明

        // 水面を描画（上向き）
        consumer.vertex(matrix, minX, waterY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, minX, waterY, maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, maxX, waterY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, maxX, waterY, minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();

        poseStack.popPose();
    }

    private void renderIngredients(AlchemicalCauldronBlockEntity cauldron, PoseStack poseStack,
                                   MultiBufferSource buffer, int light, List<ItemStack> ingredients,
                                   long gameTime, float partialTick, Level level) {
        
        // 回転速度（処理中は高速）
        float rotationSpeed = cauldron.isProcessing() ? 5.0f : 1.0f;
        float baseRotation = (gameTime + partialTick) * rotationSpeed;

        // 水面より上にアイテムを配置
        float waterLevel = cauldron.getWaterLevel();
        float baseY = waterLevel > 0 ? 0.5f + waterLevel * 0.15f : 0.35f;

        // 各材料を円形に配置
        int count = ingredients.size();
        float radius = 0.18f;
        
        for (int i = 0; i < count; i++) {
            ItemStack stack = ingredients.get(i);
            if (stack.isEmpty()) continue;

            poseStack.pushPose();

            // 釜の内部
            poseStack.translate(0.5, baseY, 0.5);

            // 円形配置
            float angle = (float) (i * 2 * Math.PI / count) + baseRotation * 0.02f;
            float xOffset = (float) Math.cos(angle) * radius;
            float zOffset = (float) Math.sin(angle) * radius;
            poseStack.translate(xOffset, 0, zOffset);

            // 浮遊アニメーション
            float bobOffset = (float) Math.sin((gameTime + partialTick + i * 20) * 0.15) * 0.03f;
            poseStack.translate(0, bobOffset, 0);

            // 個別回転
            float itemRotation = baseRotation + i * 90;
            poseStack.mulPose(Axis.YP.rotationDegrees(itemRotation));

            // スケール（処理中は脈動）
            float scale = 0.3f;
            if (cauldron.isProcessing()) {
                float pulse = (float) Math.sin((gameTime + partialTick) * 0.4) * 0.05f;
                scale += pulse;
            }
            poseStack.scale(scale, scale, scale);

            // 描画
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
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
