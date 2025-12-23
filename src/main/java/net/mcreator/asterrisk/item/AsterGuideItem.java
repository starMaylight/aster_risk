package net.mcreator.asterrisk.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

/**
 * Aster Risk ガイドブック
 * Patchouliと連携して開く
 */
public class AsterGuideItem extends Item {
    
    private static final ResourceLocation BOOK_ID = new ResourceLocation("aster_risk", "aster_guide");
    
    public AsterGuideItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // クライアントサイドでPatchouli GUIを開く
        if (level.isClientSide && ModList.get().isLoaded("patchouli")) {
            openBook(player);
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    private void openBook(Player player) {
        vazkii.patchouli.api.PatchouliAPI.get().openBookGUI(BOOK_ID);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
