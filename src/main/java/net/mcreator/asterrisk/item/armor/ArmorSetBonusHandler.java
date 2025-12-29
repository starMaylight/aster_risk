package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.mcreator.asterrisk.mana.ManaUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 防具セット効果のイベントハンドラ
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class ArmorSetBonusHandler {

    // セット効果による最大マナ増加量
    private static final float LUNAR_SET_BONUS_MAX_MANA = 25f;
    private static final float STELLAR_SET_BONUS_MAX_MANA = 50f;
    private static final float METEORITE_SET_BONUS_MAX_MANA = 15f;
    
    // 基本最大マナ
    private static final float BASE_MAX_MANA = 100f;
    
    // 隕石セットの攻撃時マナ回復量
    private static final float METEORITE_HIT_MANA_RESTORE = 3f;
    
    // 星屑セットの魔法コスト軽減率
    public static final float STELLAR_SPELL_COST_REDUCTION = 0.8f; // 20%軽減

    // 銀セット: アンデッド特効
    private static final float SILVER_UNDEAD_DAMAGE_BONUS = 0.25f; // 25%追加ダメージ

    // 虚空セット: ダメージ無効化
    private static final float VOID_NEGATE_CHANCE = 0.10f; // 10%無効化
    private static final int VOID_PHASE_SHIFT_TICKS = 20; // 1秒間の無敵

    // 前回のセット装備状態を保持（プレイヤーごと）
    private static final Map<UUID, ArmorSetType> previousSetState = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> voidPhaseShiftCooldown = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    public enum ArmorSetType {
        NONE(0),
        LUNAR(LUNAR_SET_BONUS_MAX_MANA),
        STELLAR(STELLAR_SET_BONUS_MAX_MANA),
        METEORITE(METEORITE_SET_BONUS_MAX_MANA),
        SILVER(0),
        VOID(0);
        
        public final float maxManaBonus;
        
        ArmorSetType(float maxManaBonus) {
            this.maxManaBonus = maxManaBonus;
        }
    }

    /**
     * ログイン時にセット状態を初期化
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        
        initializeArmorSet(player);
    }

    /**
     * リスポーン時にセット状態を初期化
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        
        initializeArmorSet(player);
    }

    /**
     * ディメンション移動時にセット状態を初期化
     */
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        
        initializeArmorSet(player);
    }

    /**
     * 防具セット状態を初期化し、最大マナを正しく設定
     */
    private static void initializeArmorSet(Player player) {
        UUID playerId = player.getUUID();
        ArmorSetType currentSet = getCurrentArmorSet(player);
        
        player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
            // 最大マナを基本値 + 現在のセットボーナスに設定
            float newMax = BASE_MAX_MANA + currentSet.maxManaBonus;
            mana.setMaxMana(newMax);
            
            // 現在のマナが最大値を超えていたら調整
            if (mana.getMana() > newMax) {
                mana.setMana(newMax);
            }
        });
        
        previousSetState.put(playerId, currentSet);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Player player = event.player;
        if (player.level().isClientSide()) return;
        
        // 1秒ごとにチェック（負荷軽減）
        if (player.tickCount % 20 != 0) return;
        
        UUID playerId = player.getUUID();
        ArmorSetType currentSet = getCurrentArmorSet(player);
        ArmorSetType previousSet = previousSetState.get(playerId);
        
        // 初回または状態不明の場合は初期化
        if (previousSet == null) {
            initializeArmorSet(player);
            return;
        }
        
        // セット変更があった場合
        if (currentSet != previousSet) {
            player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                // 最大マナを基本値 + 新しいセットボーナスに設定
                float newMax = BASE_MAX_MANA + currentSet.maxManaBonus;
                mana.setMaxMana(newMax);
                
                // 現在のマナが新しい最大値を超えていたら調整
                if (mana.getMana() > newMax) {
                    mana.setMana(newMax);
                }
            });
            
            previousSetState.put(playerId, currentSet);
        }

        // 銀セット: フルセットでアンデッドにダメージオーラ
        if (currentSet == ArmorSetType.SILVER && hasFullSilverSet(player)) {
            applySilverSmiteAura(player);
        }
    }

    /**
     * 銀セットのSmiteオーラ
     */
    private static void applySilverSmiteAura(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        AABB aura = player.getBoundingBox().inflate(4.0);
        List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(LivingEntity.class, aura,
            e -> e != player && e.getMobType() == MobType.UNDEAD);

        for (LivingEntity entity : nearbyEntities) {
            entity.hurt(player.damageSources().magic(), 1.0f);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, 
                entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                3, 0.2, 0.2, 0.2, 0.1);
        }
    }

    /**
     * 攻撃時のセット効果
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.level().isClientSide()) return;

            // 隕石セット: 攻撃時マナ回復
            if (MeteoriteArmorItem.hasFullSet(player)) {
                ManaUtils.addMana(player, METEORITE_HIT_MANA_RESTORE);
            }

            // 銀セット: アンデッド特効
            if (hasSilverPiece(player) && event.getEntity().getMobType() == MobType.UNDEAD) {
                int pieces = countSilverPieces(player);
                float bonusDamage = event.getAmount() * (SILVER_UNDEAD_DAMAGE_BONUS * pieces / 4f);
                event.setAmount(event.getAmount() + bonusDamage);
            }
        }
    }

    /**
     * 被ダメージ時のセット効果
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // 虚空セット: ダメージ無効化
        if (hasVoidPiece(player)) {
            int pieces = countVoidPieces(player);
            float negateChance = VOID_NEGATE_CHANCE * pieces;
            
            if (random.nextFloat() < negateChance) {
                event.setCanceled(true);
                
                // エフェクト
                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
                }
                return;
            }

            // フルセット: Phase Shift（被ダメージ時に短時間無敵）
            if (hasFullVoidSet(player)) {
                UUID playerId = player.getUUID();
                long currentTime = player.level().getGameTime();
                Long lastShift = voidPhaseShiftCooldown.get(playerId);

                // 10秒クールダウン
                if (lastShift == null || currentTime - lastShift > 200) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, VOID_PHASE_SHIFT_TICKS, 4, true, false, true));
                    voidPhaseShiftCooldown.put(playerId, currentTime);

                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                            player.getX(), player.getY() + 1, player.getZ(),
                            30, 0.5, 1, 0.5, 0.05);
                    }
                }
            }
        }
    }

    /**
     * 現在装備しているセットを取得
     */
    public static ArmorSetType getCurrentArmorSet(Player player) {
        if (LunarArmorItem.hasFullSet(player)) {
            return ArmorSetType.LUNAR;
        } else if (StellarArmorItem.hasFullSet(player)) {
            return ArmorSetType.STELLAR;
        } else if (MeteoriteArmorItem.hasFullSet(player)) {
            return ArmorSetType.METEORITE;
        } else if (hasFullSilverSet(player)) {
            return ArmorSetType.SILVER;
        } else if (hasFullVoidSet(player)) {
            return ArmorSetType.VOID;
        }
        return ArmorSetType.NONE;
    }

    // ===== 銀セット判定 =====
    public static boolean hasSilverPiece(Player player) {
        return countSilverPieces(player) > 0;
    }

    public static boolean hasFullSilverSet(Player player) {
        return countSilverPieces(player) == 4;
    }

    public static int countSilverPieces(Player player) {
        int count = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = player.getItemBySlot(slot);
                if (stack.getItem() instanceof SilverArmorItem) {
                    count++;
                }
            }
        }
        return count;
    }

    // ===== 虚空セット判定 =====
    public static boolean hasVoidPiece(Player player) {
        return countVoidPieces(player) > 0;
    }

    public static boolean hasFullVoidSet(Player player) {
        return countVoidPieces(player) == 4;
    }

    public static int countVoidPieces(Player player) {
        int count = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = player.getItemBySlot(slot);
                if (stack.getItem() instanceof VoidArmorItem) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 月光セット装備時の夜間マナ回復ボーナス倍率を取得
     */
    public static float getNightRegenMultiplier(Player player) {
        if (LunarArmorItem.hasFullSet(player) && ManaUtils.isNight(player.level())) {
            return 2.0f;
        }
        return 1.0f;
    }

    /**
     * 星屑セット装備時の魔法コスト倍率を取得
     */
    public static float getSpellCostMultiplier(Player player) {
        if (StellarArmorItem.hasFullSet(player)) {
            return STELLAR_SPELL_COST_REDUCTION;
        }
        return 1.0f;
    }
}
