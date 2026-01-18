package net.mcreator.asterrisk.mana;

/**
 * マナを受け取ることができるブロックエンティティ用インターフェース
 */
public interface IManaReceiver {
    
    /**
     * マナを受け取る
     * @param amount 受け取るマナ量
     * @return 実際に受け取ったマナ量
     */
    float receiveMana(float amount);
    
    /**
     * 現在のマナ量を取得
     */
    float getMana();
    
    /**
     * 最大マナ量を取得
     */
    float getMaxMana();
    
    /**
     * マナを受け取れる状態かどうか
     */
    boolean canReceiveMana();
}
