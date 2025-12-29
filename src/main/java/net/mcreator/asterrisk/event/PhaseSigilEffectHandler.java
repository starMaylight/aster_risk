package net.mcreator.asterrisk.event;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.entity.PhaseAnvilBlockEntity;
import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/**
 * 月相刻印の効果を適用するイベントハンドラー
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class PhaseSigilEffectHandler {

    // 効果係数（レベル1あたり）
    private static final float FULL_MOON_ATTACK_BONUS = 0.15f;      // +15% 攻撃力
    private static final float WANING_GIBBOUS_MAGIC_BONUS = 0.10f;  // +10% 魔法ダメージ
    private static final float LAST_QUARTER_DEFENSE_BONUS = 0.12f;  // +12% 防御力
    private static final float WANING_CRESCENT_MANA_BONUS = 0.08f;  // +8% マナ回復
    private static final float WAXING_CRESCENT_SPEED_BONUS = 0.10f; // +10% 移動速度
    private static final float FIRST_QUARTER_MINING_BONUS = 0.15f;  // +15% 採掘速度
    private static final float WAXING_GIBBOUS_XP_BONUS = 0.10f;     // +10% 経験値

    /**
     * 装備から全刻印レベルを取得
     */
    public static Map<PhaseSigilItem.MoonPhase, Integer> getTotalSigilLevels(Player player) {
        Map<PhaseSigilItem.MoonPhase, Integer> totals = new HashMap<>();
        
        for (PhaseSigilItem.MoonPhase phase : PhaseSigilItem.MoonPhase.values()) {
            totals.put(phase, 0);
        }

        // 全装備スロットをチェック
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS)) continue;

            CompoundTag sigils = tag.getCompound(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS);
            for (PhaseSigilItem.MoonPhase phase : PhaseSigilItem.MoonPhase.values()) {
                int level = sigils.getInt(phase.getName());
                if (level > 0) {
                    totals.put(phase, totals.get(phase) + level);
                }
            }
        }

        return totals;
    }

    /**
     * 攻撃時 - 満月: 攻撃力増加
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player player) {
            Map<PhaseSigilItem.MoonPhase, Integer> levels = getTotalSigilLevels(player);
            
            // 満月: 攻撃力ボーナス
            int fullMoonLevel = levels.get(PhaseSigilItem.MoonPhase.FULL_MOON);
            if (fullMoonLevel > 0) {
                float bonus = 1.0f + (FULL_MOON_ATTACK_BONUS * fullMoonLevel);
                event.setAmount(event.getAmount() * bonus);
            }
        }
    }

    /**
     * 被ダメージ時 - 下弦: 防御力増加
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Map<PhaseSigilItem.MoonPhase, Integer> levels = getTotalSigilLevels(player);
            
            // 下弦: 防御力ボーナス（ダメージ軽減）
            int lastQuarterLevel = levels.get(PhaseSigilItem.MoonPhase.LAST_QUARTER);
            if (lastQuarterLevel > 0) {
                float reduction = 1.0f - (LAST_QUARTER_DEFENSE_BONUS * lastQuarterLevel);
                reduction = Math.max(0.1f, reduction); // 最大90%軽減
                event.setAmount(event.getAmount() * reduction);
            }
        }
    }

    /**
     * 採掘速度 - 上弦: 採掘速度増加
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        Map<PhaseSigilItem.MoonPhase, Integer> levels = getTotalSigilLevels(player);
        
        // 上弦: 採掘速度ボーナス
        int firstQuarterLevel = levels.get(PhaseSigilItem.MoonPhase.FIRST_QUARTER);
        if (firstQuarterLevel > 0) {
            float bonus = 1.0f + (FIRST_QUARTER_MINING_BONUS * firstQuarterLevel);
            event.setNewSpeed(event.getOriginalSpeed() * bonus);
        }
    }

    /**
     * 経験値取得時 - 十三夜: 経験値増加
     */
    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        Map<PhaseSigilItem.MoonPhase, Integer> levels = getTotalSigilLevels(player);
        
        // 十三夜: 経験値ボーナス
        int waxingGibbousLevel = levels.get(PhaseSigilItem.MoonPhase.WAXING_GIBBOUS);
        if (waxingGibbousLevel > 0) {
            float bonus = WAXING_GIBBOUS_XP_BONUS * waxingGibbousLevel;
            int originalXp = event.getOrb().getValue();
            int bonusXp = (int) (originalXp * bonus);
            event.getOrb().value += bonusXp;
        }
    }
}
