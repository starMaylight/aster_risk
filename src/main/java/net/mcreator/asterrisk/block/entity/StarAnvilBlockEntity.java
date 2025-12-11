package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 星の金床
 * マナを使って装備を強化・修理
 * 
 * 操作方法:
 * - 右クリック（アイテム持ち）: アイテムを置く
 * - 右クリック（素手）: アイテムを取り出す
 * - Shift+右クリック（素手）: 修理（200マナ）
 * - Shift+右クリック×2（素手）: 強化（500マナ）
 */
public class StarAnvilBlockEntity extends BlockEntity {

    public static final float MAX_MANA = 1500f;
    public static final float RECEIVE_RATE = 40f;
    public static final float REPAIR_MANA_COST = 200f;
    public static final float ENHANCE_MANA_COST = 500f;

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;
    
    // インベントリ（1スロット）
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);
    
    private final Random random = new Random();

    public StarAnvilBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, 0, true, false);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    public static StarAnvilBlockEntity create(BlockPos pos, BlockState state) {
        return new StarAnvilBlockEntity(ModBlockEntities.STAR_ANVIL.get(), pos, state);
    }

    // === アイテム操作 ===
    
    public ItemStack getItem() {
        return inventory.getStackInSlot(0);
    }

    public void setItem(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
    }

    public ItemStack removeItem() {
        return inventory.extractItem(0, 64, false);
    }

    public void dropContents() {
        if (level != null && !level.isClientSide()) {
            ItemStack stack = inventory.getStackInSlot(0);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5,
                    stack);
                level.addFreshEntity(itemEntity);
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            }
        }
    }

    // === 修理処理 ===
    
    public boolean repairItem() {
        if (level == null || level.isClientSide()) return false;
        
        ItemStack stack = getItem();
        if (stack.isEmpty() || !stack.isDamaged()) return false;
        if (manaStorage.getMana() < REPAIR_MANA_COST) return false;

        manaStorage.setMana(manaStorage.getMana() - REPAIR_MANA_COST);
        stack.setDamageValue(0);
        playSuccessEffect();
        setChanged();
        return true;
    }

    // === 強化処理 ===
    
    public boolean enhanceItem() {
        if (level == null || level.isClientSide()) return false;
        
        ItemStack stack = getItem();
        if (stack.isEmpty()) return false;
        if (manaStorage.getMana() < ENHANCE_MANA_COST) return false;
        if (!canEnhance(stack)) return false;

        boolean success = applyEnchantment(stack);
        
        if (success) {
            manaStorage.setMana(manaStorage.getMana() - ENHANCE_MANA_COST);
            playEnhanceEffect();
            setChanged();
        }
        
        return success;
    }

    private boolean canEnhance(ItemStack stack) {
        if (stack.isEnchanted()) return true;
        if (stack.isDamageableItem()) return true;
        if (stack.isEnchantable()) return true;
        return false;
    }

    private boolean applyEnchantment(ItemStack stack) {
        List<Enchantment> possibleEnchants = getPossibleEnchantments(stack);
        if (possibleEnchants.isEmpty()) return false;
        
        Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(stack);
        
        // 適用可能なエンチャントをフィルタリング
        List<Enchantment> validEnchants = new ArrayList<>();
        for (Enchantment ench : possibleEnchants) {
            if (canApplyEnchantment(stack, ench, currentEnchants)) {
                validEnchants.add(ench);
            }
        }
        
        if (validEnchants.isEmpty()) return false;
        
        // 既存エンチャントでレベルアップ可能なものを探す
        List<Enchantment> upgradeable = new ArrayList<>();
        for (Enchantment ench : validEnchants) {
            int currentLevel = currentEnchants.getOrDefault(ench, 0);
            if (currentLevel > 0 && currentLevel < ench.getMaxLevel()) {
                upgradeable.add(ench);
            }
        }
        
        Enchantment chosen;
        if (!upgradeable.isEmpty() && random.nextFloat() < 0.6f) {
            chosen = upgradeable.get(random.nextInt(upgradeable.size()));
        } else {
            chosen = validEnchants.get(random.nextInt(validEnchants.size()));
        }
        
        int currentLevel = currentEnchants.getOrDefault(chosen, 0);
        int newLevel = Math.min(currentLevel + 1, chosen.getMaxLevel());
        
        Map<Enchantment, Integer> newEnchants = new HashMap<>(currentEnchants);
        newEnchants.put(chosen, newLevel);
        EnchantmentHelper.setEnchantments(newEnchants, stack);
        
        return true;
    }

    private boolean canApplyEnchantment(ItemStack stack, Enchantment enchantment, Map<Enchantment, Integer> currentEnchants) {
        // アイテムに適用可能か（既に持っているものはOK）
        if (!enchantment.canEnchant(stack) && !currentEnchants.containsKey(enchantment)) {
            return false;
        }
        
        // 既存のエンチャントとの互換性チェック
        for (Enchantment existing : currentEnchants.keySet()) {
            if (existing != enchantment && !enchantment.isCompatibleWith(existing)) {
                return false;
            }
        }
        
        // 既に最大レベルならスキップ
        int currentLevel = currentEnchants.getOrDefault(enchantment, 0);
        if (currentLevel >= enchantment.getMaxLevel()) {
            return false;
        }
        
        return true;
    }

    private List<Enchantment> getPossibleEnchantments(ItemStack stack) {
        List<Enchantment> enchants = new ArrayList<>();
        
        if (stack.getItem() instanceof net.minecraft.world.item.SwordItem) {
            enchants.add(Enchantments.SHARPNESS);
            enchants.add(Enchantments.FIRE_ASPECT);
            enchants.add(Enchantments.KNOCKBACK);
            enchants.add(Enchantments.MOB_LOOTING);
            enchants.add(Enchantments.SWEEPING_EDGE);
            enchants.add(Enchantments.UNBREAKING);
        } else if (stack.getItem() instanceof net.minecraft.world.item.PickaxeItem ||
                   stack.getItem() instanceof net.minecraft.world.item.ShovelItem ||
                   stack.getItem() instanceof net.minecraft.world.item.HoeItem) {
            enchants.add(Enchantments.BLOCK_EFFICIENCY);
            enchants.add(Enchantments.UNBREAKING);
            enchants.add(Enchantments.BLOCK_FORTUNE);
            enchants.add(Enchantments.SILK_TOUCH);
        } else if (stack.getItem() instanceof net.minecraft.world.item.AxeItem) {
            enchants.add(Enchantments.BLOCK_EFFICIENCY);
            enchants.add(Enchantments.UNBREAKING);
            enchants.add(Enchantments.SHARPNESS);
            enchants.add(Enchantments.BLOCK_FORTUNE);
            enchants.add(Enchantments.SILK_TOUCH);
        } else if (stack.getItem() instanceof net.minecraft.world.item.BowItem) {
            enchants.add(Enchantments.POWER_ARROWS);
            enchants.add(Enchantments.PUNCH_ARROWS);
            enchants.add(Enchantments.FLAMING_ARROWS);
            enchants.add(Enchantments.INFINITY_ARROWS);
            enchants.add(Enchantments.UNBREAKING);
        } else if (stack.getItem() instanceof net.minecraft.world.item.CrossbowItem) {
            enchants.add(Enchantments.QUICK_CHARGE);
            enchants.add(Enchantments.PIERCING);
            enchants.add(Enchantments.MULTISHOT);
            enchants.add(Enchantments.UNBREAKING);
        } else if (stack.getItem() instanceof net.minecraft.world.item.TridentItem) {
            enchants.add(Enchantments.LOYALTY);
            enchants.add(Enchantments.CHANNELING);
            enchants.add(Enchantments.IMPALING);
            enchants.add(Enchantments.RIPTIDE);
            enchants.add(Enchantments.UNBREAKING);
        } else if (stack.getItem() instanceof net.minecraft.world.item.ArmorItem armorItem) {
            enchants.add(Enchantments.ALL_DAMAGE_PROTECTION);
            enchants.add(Enchantments.UNBREAKING);
            enchants.add(Enchantments.MENDING);
            
            switch (armorItem.getType()) {
                case HELMET:
                    enchants.add(Enchantments.RESPIRATION);
                    enchants.add(Enchantments.AQUA_AFFINITY);
                    enchants.add(Enchantments.THORNS);
                    break;
                case CHESTPLATE:
                    enchants.add(Enchantments.THORNS);
                    break;
                case LEGGINGS:
                    enchants.add(Enchantments.THORNS);
                    enchants.add(Enchantments.SWIFT_SNEAK);
                    break;
                case BOOTS:
                    enchants.add(Enchantments.FALL_PROTECTION);
                    enchants.add(Enchantments.DEPTH_STRIDER);
                    enchants.add(Enchantments.FROST_WALKER);
                    enchants.add(Enchantments.SOUL_SPEED);
                    break;
            }
        } else if (stack.getItem() instanceof net.minecraft.world.item.FishingRodItem) {
            enchants.add(Enchantments.FISHING_LUCK);
            enchants.add(Enchantments.FISHING_SPEED);
            enchants.add(Enchantments.UNBREAKING);
            enchants.add(Enchantments.MENDING);
        } else if (stack.getItem() instanceof net.minecraft.world.item.ShieldItem) {
            enchants.add(Enchantments.UNBREAKING);
            enchants.add(Enchantments.MENDING);
        } else {
            enchants.add(Enchantments.UNBREAKING);
            enchants.add(Enchantments.MENDING);
        }
        
        return enchants;
    }

    // === エフェクト ===
    
    private void playSuccessEffect() {
        if (level instanceof ServerLevel serverLevel) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.2;
            double z = worldPosition.getZ() + 0.5;

            serverLevel.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 10, 0.3, 0.2, 0.3, 0.02);
        }
    }

    private void playEnhanceEffect() {
        if (level instanceof ServerLevel serverLevel) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.2;
            double z = worldPosition.getZ() + 0.5;

            serverLevel.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.2f);
            serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 30, 0.4, 0.3, 0.4, 0.1);
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 15, 0.3, 0.3, 0.3, 0.05);
        }
    }

    // === マナ ===
    
    public float getMana() {
        return manaStorage.getMana();
    }

    public float getMaxMana() {
        return manaStorage.getMaxMana();
    }

    // === NBT ===

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.put("inventory", inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("mana", manaStorage.getMana());
        tag.put("inventory", inventory.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("mana")) {
            manaStorage.setMana(tag.getFloat("mana"));
        }
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == BlockManaCapability.BLOCK_MANA) {
            return manaHandler.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        manaHandler.invalidate();
        inventoryHandler.invalidate();
    }
}
