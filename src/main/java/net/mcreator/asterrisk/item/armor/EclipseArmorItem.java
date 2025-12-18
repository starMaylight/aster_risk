package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.init.AsterRiskModItems;
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
        tooltip.add(Component.literal("§5§l[Eclipse Set]"));
        
        switch (this.type) {
            case HELMET -> {
                tooltip.add(Component.literal("§7Grants Night Vision"));
                tooltip.add(Component.literal("§8[Full Set] Weakens nearby enemies"));
            }
            case CHESTPLATE -> {
                tooltip.add(Component.literal("§7Reflects damage to attackers"));
                tooltip.add(Component.literal("§8[Full Set] Weakens nearby enemies"));
            }
            case LEGGINGS -> {
                tooltip.add(Component.literal("§7Increases movement speed"));
                tooltip.add(Component.literal("§8[Full Set] Weakens nearby enemies"));
            }
            case BOOTS -> {
                tooltip.add(Component.literal("§7Negates fall damage"));
                tooltip.add(Component.literal("§8[Full Set] Weakens nearby enemies"));
            }
        }
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
