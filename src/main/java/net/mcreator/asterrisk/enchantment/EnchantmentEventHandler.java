package net.mcreator.asterrisk.enchantment;

import net.mcreator.asterrisk.registry.ModEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * エンチャント効果を処理するイベントハンドラ
 */
@Mod.EventBusSubscriber(modid = "aster_risk", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentEventHandler {

    /**
     * 月光エンチャント：夜間ダメージ増加
     * 天体の守護エンチャント：ダメージ軽減
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity source = event.getSource().getEntity();
        
        // 攻撃者側：月光エンチャント
        if (source instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            Level level = player.level();
            
            int moonlightLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.MOONLIGHT.get(), weapon);
            
            if (moonlightLevel > 0) {
                long dayTime = level.getDayTime() % 24000;
                boolean isNight = dayTime >= 13000 && dayTime < 23000;
                
                if (isNight) {
                    float multiplier = MoonlightEnchantment.getNightDamageMultiplier(moonlightLevel);
                    float newDamage = event.getAmount() * multiplier;
                    event.setAmount(newDamage);
                }
            }
        }
        
        // 防御者側：天体の守護エンチャント
        if (event.getEntity() instanceof Player player) {
            int totalProtection = 0;
            
            for (ItemStack armor : player.getArmorSlots()) {
                int protLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    ModEnchantments.CELESTIAL_PROTECTION.get(), armor);
                totalProtection += CelestialProtectionEnchantment.getDamageProtection(protLevel);
                
                // 魔法ダメージの場合は追加軽減（1.20.1のAPI）
                if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)) {
                    totalProtection += CelestialProtectionEnchantment.getMagicDamageProtection(protLevel);
                }
            }
            
            if (totalProtection > 0) {
                totalProtection = Math.min(totalProtection, 20);
                float damageReduction = totalProtection * 0.04f;
                float newDamage = event.getAmount() * (1.0f - damageReduction);
                event.setAmount(newDamage);
            }
        }
    }

    /**
     * 月の引力エンチャント：アイテム吸引
     * ManaEventHandlerから呼び出される
     */
    public static void handleLunarAttraction(Player player) {
        if (player.level().isClientSide()) return;
        
        // チェストプレートの月の引力エンチャントをチェック
        ItemStack chestplate = player.getInventory().armor.get(2);
        int gravityLevel = EnchantmentHelper.getItemEnchantmentLevel(
            ModEnchantments.LUNAR_GRAVITY.get(), chestplate);
        
        if (gravityLevel <= 0) return;
        
        double range = LunarGravityEnchantment.getAttractionRange(gravityLevel);
        Vec3 playerPos = player.position();
        
        AABB searchBox = new AABB(
            playerPos.x - range, playerPos.y - range, playerPos.z - range,
            playerPos.x + range, playerPos.y + range, playerPos.z + range
        );
        
        // アイテムを引き寄せ
        List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, searchBox);
        for (ItemEntity item : items) {
            // 投げたばかりのアイテムは除外（pickupDelayで判定）
            if (item.hasPickUpDelay()) continue;
            
            Vec3 direction = playerPos.subtract(item.position()).normalize();
            double speed = 0.15;
            item.setDeltaMovement(direction.scale(speed));
        }
        
        // 経験値オーブも引き寄せ
        List<ExperienceOrb> orbs = player.level().getEntitiesOfClass(ExperienceOrb.class, searchBox);
        for (ExperienceOrb orb : orbs) {
            Vec3 direction = playerPos.subtract(orb.position()).normalize();
            double speed = 0.2;
            orb.setDeltaMovement(direction.scale(speed));
        }
    }
}
