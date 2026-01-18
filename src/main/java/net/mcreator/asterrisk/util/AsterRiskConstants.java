package net.mcreator.asterrisk.util;

/**
 * Aster Risk mod の定数を一元管理
 */
public final class AsterRiskConstants {
    
    private AsterRiskConstants() {} // インスタンス化防止
    
    // ===== マナシステム =====
    
    /** デフォルトの最大マナ */
    public static final float DEFAULT_MAX_MANA = 100f;
    
    /** マナ回復チェック間隔（tick） */
    public static final int MANA_RECOVERY_INTERVAL = 20;
    
    /** 夜間マナ回復ボーナス倍率 */
    public static final float NIGHT_MANA_BONUS = 2.0f;
    
    /** 空が見える場合のマナ回復ボーナス倍率 */
    public static final float SKY_VISIBLE_MANA_BONUS = 1.5f;
    
    // ===== 月相 =====
    
    /** 月相の数 */
    public static final int MOON_PHASE_COUNT = 8;
    
    /** 満月の月相インデックス */
    public static final int MOON_PHASE_FULL = 0;
    
    /** 新月の月相インデックス */
    public static final int MOON_PHASE_NEW = 4;
    
    // ===== 時間 =====
    
    /** 夜の開始時刻（tick） */
    public static final long NIGHT_START = 13000L;
    
    /** 夜の終了時刻（tick） */
    public static final long NIGHT_END = 23000L;
    
    /** 1日の長さ（tick） */
    public static final long DAY_LENGTH = 24000L;
    
    // ===== 儀式システム =====
    
    /** 台座検出範囲 */
    public static final int PEDESTAL_SEARCH_RADIUS = 3;
    
    /** フォーカス検出範囲 */
    public static final int FOCUS_SEARCH_RADIUS = 4;
    
    /** ピラー検出範囲 */
    public static final int PILLAR_SEARCH_RADIUS = 3;
    
    // ===== ブロックエンティティ =====
    
    /** Lunar Collector の最大マナ容量 */
    public static final float LUNAR_COLLECTOR_MAX_MANA = 1000f;
    
    /** Lunar Collector のマナ受信レート */
    public static final float LUNAR_COLLECTOR_RECEIVE_RATE = 10f;
    
    /** Lunar Resonator の転送範囲 */
    public static final int LUNAR_RESONATOR_RANGE = 16;
    
    // ===== エンチャントシステム =====
    
    /** 天体エンチャントの基本月光コスト */
    public static final int BASE_MOONLIGHT_COST = 100;
    
    /** レベルごとの追加月光コスト */
    public static final int MOONLIGHT_COST_PER_LEVEL = 50;
    
    // ===== パーティクル =====
    
    /** 基本パーティクル数 */
    public static final int DEFAULT_PARTICLE_COUNT = 5;
    
    /** 儀式パーティクル数 */
    public static final int RITUAL_PARTICLE_COUNT = 10;
    
    // ===== ネットワーク =====
    
    /** ネットワークプロトコルバージョン */
    public static final String NETWORK_PROTOCOL_VERSION = "1";
    
    // ===== レシピ =====
    
    /** デフォルトの儀式マナコスト */
    public static final float DEFAULT_RITUAL_MANA_COST = 100f;
    
    /** デフォルトの処理時間（tick） */
    public static final int DEFAULT_PROCESS_TIME = 100;
}
