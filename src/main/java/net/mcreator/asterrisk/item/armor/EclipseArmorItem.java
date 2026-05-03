package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.registry.ModItems;
import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Eclipse Armor - 日蝕セット（最終装備）
 */
public class EclipseArmorItem extends ArmorItem {
    
    private static final EclipseArmorMaterial MATERIAL = new EclipseArmorMaterial();
    
    public EclipseArmorItem(Type type, Properties properties) {
        super(MATERIAL, type, properties.fireResistant());
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            if (hasFullSet(player)) {
                applyFullSetBonus(player);
            }
            applyPieceBonus(player, this.type);
        }
    }
    
    private boolean hasFullSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        
        return head.getItem() instanceof EclipseArmorItem &&
               chest.getItem() instanceof EclipseArmorItem &&
               legs.getItem() instanceof EclipseArmorItem &&
               feet.getItem() instanceof EclipseArmorItem;
    }
    
    private void applyFullSetBonus(Player player) {
        // フルセットボーナス：周囲の敵にデバフ
        if (player.tickCount % 40 == 0) {
            AABB area = player.getBoundingBox().inflate(8.0D);
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive() && !(e instanceof Player));
            
            for (LivingEntity target : entities) {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false));
            }
        }
        
        // 耐性UP
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false));
    }
    
    private void applyPieceBonus(Player player, Type type) {
        switch (type) {
            case HELMET -> {
                // 暗視
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
            }
            case CHESTPLATE -> {
                // 反射ダメージは別途hurt時に処理
            }
            case LEGGINGS -> {
                // 移動速度UP
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, false, false));
            }
            case BOOTS -> {
                // 落下ダメージ無効はArmorItem側で処理
            }
        }
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.DARK_PURPLE, "tooltip.aster_risk.eclipse_armor.header");
        switch (this.type) {
            case HELMET -> TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.eclipse_armor.bonus_helmet");
            case CHESTPLATE -> TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.eclipse_armor.bonus_chestplate");
            case LEGGINGS -> TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.eclipse_armor.bonus_leggings");
            case BOOTS -> TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.eclipse_armor.bonus_boots");
        }
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.eclipse_armor.set_bonus");
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
