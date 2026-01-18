package net.mcreator.asterrisk.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.asterrisk.entity.HeavenlyAnvilEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 天罰の鉄槌の金床レンダラー
 */
public class HeavenlyAnvilRenderer extends EntityRenderer<HeavenlyAnvilEntity> {
    
    private final BlockRenderDispatcher blockRenderer;
    
    public HeavenlyAnvilRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5f;
    }
    
    @Override
    public void render(HeavenlyAnvilEntity entity, float entityYaw, float partialTicks, 
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockState = entity.getBlockState();
        
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            poseStack.pushPose();
            
            // 中央に配置
            poseStack.translate(-0.5, 0.0, -0.5);
            
            // ブロックをレンダリング
            this.blockRenderer.renderSingleBlock(
                blockState, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            
            poseStack.popPose();
        }
        
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
    
    @Override
    public ResourceLocation getTextureLocation(HeavenlyAnvilEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
