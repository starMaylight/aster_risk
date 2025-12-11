package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.mcreator.asterrisk.mana.ManaUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
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

    // 前回のセット装備状態を保持（プレイヤーごと）
    private static final Map<UUID, ArmorSetType> previousSetState = new ConcurrentHashMap<>();

    public enum ArmorSetType {
        NONE(0),
        LUNAR(LUNAR_SET_BONUS_MAX_MANA),
        STELLAR(STELLAR_SET_BONUS_MAX_MANA),
        METEORITE(METEORITE_SET_BONUS_MAX_MANA);
        
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
    }

    /**
     * 隕石セットの攻撃時マナ回復
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (!player.level().isClientSide() && MeteoriteArmorItem.hasFullSet(player)) {
                ManaUtils.addMana(player, METEORITE_HIT_MANA_RESTORE);
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
        }
        return ArmorSetType.NONE;
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
