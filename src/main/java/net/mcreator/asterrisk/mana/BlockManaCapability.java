package net.mcreator.asterrisk.mana;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ブロック用マナストレージのCapability
 */
public class BlockManaCapability {

    public static final Capability<IBlockMana> BLOCK_MANA = CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * ブロックマナのインターフェース
     */
    public interface IBlockMana extends INBTSerializable<CompoundTag> {
        float getMana();
        void setMana(float mana);
        float getMaxMana();
        void setMaxMana(float maxMana);
        float addMana(float amount);
        float extractMana(float amount);
        boolean canReceive();
        boolean canExtract();
        float getReceiveRate();
        float getExtractRate();
    }

    /**
     * 基本的なブロックマナ実装
     */
    public static class BlockMana implements IBlockMana {
        protected float mana = 0f;
        protected float maxMana;
        protected float receiveRate;
        protected float extractRate;
        protected boolean canReceive;
        protected boolean canExtract;

        public BlockMana(float maxMana, float receiveRate, float extractRate, boolean canReceive, boolean canExtract) {
            this.maxMana = maxMana;
            this.receiveRate = receiveRate;
            this.extractRate = extractRate;
            this.canReceive = canReceive;
            this.canExtract = canExtract;
        }

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
        public float addMana(float amount) {
            if (!canReceive) return 0;
            float toAdd = Math.min(amount, Math.min(receiveRate, maxMana - mana));
            mana += toAdd;
            return toAdd;
        }

        @Override
        public float extractMana(float amount) {
            if (!canExtract) return 0;
            float toExtract = Math.min(amount, Math.min(extractRate, mana));
            mana -= toExtract;
            return toExtract;
        }

        @Override
        public boolean canReceive() {
            return canReceive && mana < maxMana;
        }

        @Override
        public boolean canExtract() {
            return canExtract && mana > 0;
        }

        @Override
        public float getReceiveRate() {
            return receiveRate;
        }

        @Override
        public float getExtractRate() {
            return extractRate;
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
            if (tag.contains("maxMana")) {
                maxMana = tag.getFloat("maxMana");
            }
        }
    }

    /**
     * Capabilityプロバイダー
     */
    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final BlockMana mana;
        private final LazyOptional<IBlockMana> optional;

        public Provider(float maxMana, float receiveRate, float extractRate, boolean canReceive, boolean canExtract) {
            this.mana = new BlockMana(maxMana, receiveRate, extractRate, canReceive, canExtract);
            this.optional = LazyOptional.of(() -> mana);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == BLOCK_MANA ? optional.cast() : LazyOptional.empty();
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
}
