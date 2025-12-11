package net.mcreator.asterrisk.mana;

import net.mcreator.asterrisk.item.armor.ArmorSetBonusHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 月魔力（Lunar Mana）のCapabilityインターフェースと実装
 */
public class LunarManaCapability {

    // Capabilityのインスタンス
    public static final Capability<ILunarMana> LUNAR_MANA = CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * 月魔力のインターフェース
     */
    public interface ILunarMana extends INBTSerializable<CompoundTag> {
        float getMana();
        void setMana(float mana);
        float getMaxMana();
        void setMaxMana(float maxMana);
        void addMana(float amount);
        void consumeMana(float amount);
        boolean canConsume(float amount);
        void tick(Player player);
    }

    /**
     * 月魔力の実装
     */
    public static class LunarMana implements ILunarMana {
        private float mana = 100f;
        private float maxMana = 100f;
        private int tickCounter = 0;

        // 月齢ごとの回復速度（1秒あたり）
        private static final float[] MOON_PHASE_RECOVERY = {
            2.0f,   // 0: 満月
            1.5f,   // 1: 更待月
            1.0f,   // 2: 下弦の月
            0.5f,   // 3: 有明月
            0.25f,  // 4: 新月
            0.5f,   // 5: 三日月
            1.0f,   // 6: 上弦の月
            1.5f    // 7: 十三夜月
        };

        @Override
        public float getMana() {
            return mana;
        }

        @Override
        public void setMana(float mana) {
            this.mana = Math.max(0, Math.min(mana, maxMana));
        }

        @Override
        public float getMaxMana() {
            return maxMana;
        }

        @Override
        public void setMaxMana(float maxMana) {
            this.maxMana = maxMana;
            if (this.mana > maxMana) {
                this.mana = maxMana;
            }
        }

        @Override
        public void addMana(float amount) {
            setMana(this.mana + amount);
        }

        @Override
        public void consumeMana(float amount) {
            setMana(this.mana - amount);
        }

        @Override
        public boolean canConsume(float amount) {
            return this.mana >= amount;
        }

        @Override
        public void tick(Player player) {
            tickCounter++;
            
            // 毎秒（20tick）ごとに回復
            if (tickCounter >= 20) {
                tickCounter = 0;
                
                Level level = player.level();
                if (!level.isClientSide() && mana < maxMana) {
                    // 月齢を取得（0-7）
                    int moonPhase = level.getMoonPhase();
                    
                    // 夜間かどうかで回復量を調整
                    float recoveryRate = MOON_PHASE_RECOVERY[moonPhase];
                    
                    // 夜間は回復速度2倍
                    long dayTime = level.getDayTime() % 24000;
                    boolean isNight = dayTime >= 13000 && dayTime < 23000;
                    if (isNight) {
                        recoveryRate *= 2.0f;
                        
                        // 月光セット効果：夜間回復がさらに2倍
                        recoveryRate *= ArmorSetBonusHandler.getNightRegenMultiplier(player);
                    }
                    
                    // 空が見えているとさらにボーナス
                    if (level.canSeeSky(player.blockPosition())) {
                        recoveryRate *= 1.5f;
                    }
                    
                    addMana(recoveryRate);
                }
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("mana", mana);
            tag.putFloat("maxMana", maxMana);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            mana = tag.getFloat("mana");
            maxMana = tag.contains("maxMana") ? tag.getFloat("maxMana") : 100f;
        }
    }

    /**
     * Capabilityプロバイダー
     */
    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final LunarMana mana = new LunarMana();
        private final LazyOptional<ILunarMana> optional = LazyOptional.of(() -> mana);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == LUNAR_MANA ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return mana.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            mana.deserializeNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }
    }

    /**
     * プレイヤーの月魔力を取得するヘルパーメソッド
     */
    public static LazyOptional<ILunarMana> get(Player player) {
        return player.getCapability(LUNAR_MANA);
    }
}
