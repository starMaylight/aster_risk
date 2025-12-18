package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Stellar Endgame Armor - 星光セット（最終装備）
 */
public class StellarEndgameArmorItem extends ArmorItem {
    
    private static final StellarEndgameArmorMaterial MATERIAL = new StellarEndgameArmorMaterial();
    
    public StellarEndgameArmorItem(Type type, Properties properties) {
        super(MATERIAL, type, properties.fireResistant());
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            if (hasFullSet(player)) {
                applyFullSetBonus(player);
            }
            applyPieceBonus(player, level, this.type);
        }
    }
    
    private boolean hasFullSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        
        return head.getItem() instanceof StellarEndgameArmorItem &&
               chest.getItem() instanceof StellarEndgameArmorItem &&
               legs.getItem() instanceof StellarEndgameArmorItem &&
               feet.getItem() instanceof StellarEndgameArmorItem;
    }
    
    private void applyFullSetBonus(Player player) {
        // フルセットボーナス：発光効果
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
        
        // マナ回復ボーナス
        if (player.tickCount % 20 == 0) {
            LunarManaCapability.get(player).ifPresent(mana -> {
                mana.addMana(2); // 追加回復
            });
        }
    }
    
    private void applyPieceBonus(Player player, Level level, Type type) {
        switch (type) {
            case HELMET -> {
                // マナ回復UP（毎秒追加回復）
                if (player.tickCount % 40 == 0) {
                    LunarManaCapability.get(player).ifPresent(mana -> {
                        mana.addMana(1);
                    });
                }
            }
            case CHESTPLATE -> {
                // 被ダメージでマナ回復はhurt時に処理
            }
            case LEGGINGS -> {
                // ジャンプブースト
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 1, false, false));
            }
            case BOOTS -> {
                // 水上歩行
                if (player.isInWater() && !player.isUnderWater()) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, 0.04, 0));
                }
            }
        }
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6§l[Stellar Set]"));
        
        switch (this.type) {
            case HELMET -> {
                tooltip.add(Component.literal("§e Increases mana regeneration"));
                tooltip.add(Component.literal("§8[Full Set] Double mana regen"));
            }
            case CHESTPLATE -> {
                tooltip.add(Component.literal("§e Recover mana when hit"));
                tooltip.add(Component.literal("§8[Full Set] Double mana regen"));
            }
            case LEGGINGS -> {
                tooltip.add(Component.literal("§e Grants Jump Boost II"));
                tooltip.add(Component.literal("§8[Full Set] Double mana regen"));
            }
            case BOOTS -> {
                tooltip.add(Component.literal("§e Walk on water"));
                tooltip.add(Component.literal("§8[Full Set] Double mana regen"));
            }
        }
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
