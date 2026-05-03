package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.registry.ModBlocks;
import net.mcreator.asterrisk.pattern.FocusPattern;
import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.mcreator.asterrisk.recipe.CelestialEnchantRecipe;
import net.mcreator.asterrisk.recipe.CelestialEnchantRecipeManager;
import net.mcreator.asterrisk.recipe.ExclusiveEnchantRecipeManager;
import net.mcreator.asterrisk.recipe.ExclusiveEnchantRecipeManager.ExclusiveEnchantData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 天体エンチャント台ブロックエンティティ
 * 
 * 仕様:
 * 1. CelestialEnchantTableを中央に、四隅にCelestialTileの冠、LunarPillar2段を配置
 * 2. Focusを特定パターンに設置し、全てをEnchantTableを経由してリンク
 * 3. EnchantTableが感知し、アイテムが乗っている時にShift+クリックでエンチャント付与
 * 4. 乗っているアイテムはレンダリング
 * 5. Focusからマナを受け取れる
 * 
 * パターンはPatternManagerからデータ駆動で読み込まれる
 */
public class CelestialEnchantingTableBlockEntity extends BlockEntity {
    
    // 四隅のピラー位置（EnchantTable基準）
    public static final BlockPos[] PILLAR_BASES = {
        new BlockPos(-2, 0, -2),
        new BlockPos(-2, 0, 2),
        new BlockPos(2, 0, -2),
        new BlockPos(2, 0, 2)
    };
    
    private ItemStack heldItem = ItemStack.EMPTY;
    private float storedMana = 0;
    private static final float MAX_MANA = Integer.MAX_VALUE; // 専用エンチャントの指数コスト対応
    
    private boolean isStructureValid = false;
    private boolean isEnchanting = false;
    private int enchantProgress = 0;
    private static final int ENCHANT_TIME = 100;
    
    @Nullable
    private CelestialEnchantRecipe currentRecipe = null;
    @Nullable
    private ExclusiveEnchantData currentExclusiveEnchant = null;
    private int currentEnchantCost = 0;
    private String currentPattern = "";
    
    // リンクされたFocusの追跡用
    private Set<BlockPos> linkedFocuses = new HashSet<>();
    
    // デバッグ用
    private String debugPillarInfo = "";
    
    public CelestialEnchantingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CELESTIAL_ENCHANTING_TABLE.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, CelestialEnchantingTableBlockEntity entity) {
        // 構造チェック
        entity.isStructureValid = entity.checkPillarStructure(level);
        
        // Focusパターン検出とリンク確認
        entity.detectLinkedFocusPattern(level);
        
        // エンチャント処理中
        if (entity.isEnchanting) {
            float manaCost = entity.currentEnchantCost;
            float manaPerTick = manaCost / ENCHANT_TIME;
            
            if (entity.storedMana >= manaPerTick) {
                entity.storedMana -= manaPerTick;
                entity.enchantProgress++;
                
                // パーティクル
                if (level instanceof ServerLevel serverLevel && entity.enchantProgress % 5 == 0) {
                    entity.spawnEnchantParticles(serverLevel);
                }
                
                // 完了
                if (entity.enchantProgress >= ENCHANT_TIME) {
                    entity.completeEnchanting(level);
                }
            } else {
                // マナ不足でキャンセル
                entity.cancelEnchanting();
            }
        }
        
        entity.setChanged();
        
        // クライアント同期（アイテム表示用）
        level.sendBlockUpdated(pos, state, state, 3);
    }
    
    /**
     * 四隅のピラー構造チェック
     * 各隅: LunarPillar x2 + CelestialTile(頂上)
     */
    private boolean checkPillarStructure(Level level) {
        Block lunarPillarBlock = ModBlocks.LUNAR_PILLAR.get();
        Block celestialTileBlock = ModBlocks.CELESTIAL_TILE.get();
        
        StringBuilder debug = new StringBuilder();
        
        for (int i = 0; i < PILLAR_BASES.length; i++) {
            BlockPos baseOffset = PILLAR_BASES[i];
            BlockPos base = worldPosition.offset(baseOffset);
            
            debug.append("Corner").append(i).append(": ");
            
            // 1段目: LunarPillar
            BlockPos pillar1Pos = base;
            BlockState pillar1State = level.getBlockState(pillar1Pos);
            if (!pillar1State.is(lunarPillarBlock)) {
                debug.append("P1=").append(pillar1State.getBlock().getDescriptionId()).append(" ");
                debugPillarInfo = debug.toString();
                return false;
            }
            
            // 2段目: LunarPillar
            BlockPos pillar2Pos = base.above(1);
            BlockState pillar2State = level.getBlockState(pillar2Pos);
            if (!pillar2State.is(lunarPillarBlock)) {
                debug.append("P2=").append(pillar2State.getBlock().getDescriptionId()).append(" ");
                debugPillarInfo = debug.toString();
                return false;
            }
            
            // 3段目: CelestialTile (冠)
            BlockPos crownPos = base.above(2);
            BlockState crownState = level.getBlockState(crownPos);
            if (!crownState.is(celestialTileBlock)) {
                debug.append("Crown=").append(crownState.getBlock().getDescriptionId()).append(" ");
                debugPillarInfo = debug.toString();
                return false;
            }
            
            debug.append("OK ");
        }
        
        debugPillarInfo = "All pillars valid";
        return true;
    }
    
    /**
     * リンクされたFocusのパターンを検出
     * PatternManagerから全Focusパターンを取得してチェック
     */
    private void detectLinkedFocusPattern(Level level) {
        linkedFocuses.clear();
        currentPattern = "";
        
        // PatternManagerから全パターンを取得
        for (FocusPattern pattern : PatternManager.getInstance().getAllFocusPatterns()) {
            String patternName = pattern.getName();
            List<BlockPos> positions = pattern.getPositions();
            
            boolean allLinked = true;
            Set<BlockPos> patternFocuses = new HashSet<>();
            
            for (BlockPos relPos : positions) {
                BlockPos focusPos = worldPosition.offset(relPos);
                BlockEntity be = level.getBlockEntity(focusPos);
                
                if (be instanceof MoonlightFocusBlockEntity focus) {
                    if (focus.hasLinkTo(worldPosition)) {
                        patternFocuses.add(focusPos);
                    } else {
                        allLinked = false;
                        break;
                    }
                } else {
                    allLinked = false;
                    break;
                }
            }
            
            if (allLinked) {
                linkedFocuses = patternFocuses;
                currentPattern = patternName;
                return;
            }
        }
    }
    
    /**
     * Focusからマナを受け取る
     */
    public float receiveMana(float amount) {
        float received = Math.min(amount, MAX_MANA - storedMana);
        storedMana += received;
        setChanged();
        return received;
    }
    
    /**
     * Shift+クリックでエンチャント開始
     */
    public boolean tryStartEnchanting(Player player) {
        if (level == null || level.isClientSide || isEnchanting) return false;
        if (heldItem.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.aster_risk.celestial.no_item"), true);
            return false;
        }
        if (!isStructureValid) {
            player.displayClientMessage(Component.translatable("message.aster_risk.celestial.pillar_incomplete", debugPillarInfo), true);
            return false;
        }
        if (currentPattern.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.aster_risk.celestial.no_pattern"), true);
            return false;
        }
        
        // 専用エンチャントを先にチェック（JSON駆動）
        ExclusiveEnchantRecipeManager manager = ExclusiveEnchantRecipeManager.getInstance();
        ExclusiveEnchantData exclusiveData = manager.findApplicable(currentPattern, heldItem);
        if (exclusiveData != null) {
            int cost = manager.calculateCost(exclusiveData, heldItem);
            
            if (storedMana < cost) {
                player.displayClientMessage(
                    Component.translatable("message.aster_risk.celestial.not_enough_mana", cost, (int)storedMana),
                    true
                );
                return false;
            }
            
            currentExclusiveEnchant = exclusiveData;
            currentRecipe = null;
            currentEnchantCost = cost;
            isEnchanting = true;
            enchantProgress = 0;
            
            int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(exclusiveData.enchantment, heldItem);
            int newLevel = currentLevel + 1;
            
            level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
            player.displayClientMessage(Component.translatable("message.aster_risk.celestial.enchanting",
                exclusiveData.enchantment.getDescriptionId(), newLevel, cost), true);
            return true;
        }
        
        // 通常レシピ検索
        CelestialEnchantRecipe recipe = CelestialEnchantRecipeManager.findRecipe(currentPattern, heldItem);
        if (recipe == null) {
            player.displayClientMessage(Component.translatable("message.aster_risk.celestial.no_recipe"), true);
            return false;
        }

        if (storedMana < recipe.getMoonlightCost()) {
            player.displayClientMessage(
                Component.translatable("message.aster_risk.celestial.not_enough_mana", recipe.getMoonlightCost(), (int)storedMana),
                true
            );
            return false;
        }
        
        currentRecipe = recipe;
        currentExclusiveEnchant = null;
        currentEnchantCost = recipe.getMoonlightCost();
        isEnchanting = true;
        enchantProgress = 0;
        
        level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        player.displayClientMessage(Component.translatable("message.aster_risk.celestial.started"), true);
        return true;
    }
    
    /**
     * エンチャント完了
     */
    private void completeEnchanting(Level level) {
        if (heldItem.isEmpty()) return;
        
        // 専用エンチャントの場合（JSON駆動）
        if (currentExclusiveEnchant != null) {
            ExclusiveEnchantRecipeManager.getInstance().applyEnchantment(currentExclusiveEnchant, heldItem);
        }
        // 通常レシピの場合
        else if (currentRecipe != null) {
            Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(currentRecipe.getEnchantment());
            if (enchant != null) {
                Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(heldItem);
                currentEnchants.put(enchant, currentRecipe.getEnchantmentLevel());
                EnchantmentHelper.setEnchantments(currentEnchants, heldItem);
            }
        }
        
        level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 1.5f);
        if (level instanceof ServerLevel serverLevel) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.5;
            double z = worldPosition.getZ() + 0.5;
            serverLevel.sendParticles(ParticleTypes.ENCHANT, x, y, z, 200, 1.0, 1.0, 1.0, 0.5);
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y + 1, z, 50, 0.5, 0.5, 0.5, 0.1);
        }
        
        isEnchanting = false;
        enchantProgress = 0;
        currentRecipe = null;
        currentExclusiveEnchant = null;
        currentEnchantCost = 0;
    }
    
    private void cancelEnchanting() {
        isEnchanting = false;
        enchantProgress = 0;
        currentRecipe = null;
        currentExclusiveEnchant = null;
        currentEnchantCost = 0;
    }
    
    private void spawnEnchantParticles(ServerLevel level) {
        for (BlockPos focusPos : linkedFocuses) {
            double fx = focusPos.getX() + 0.5;
            double fy = focusPos.getY() + 0.5;
            double fz = focusPos.getZ() + 0.5;
            
            double dx = (worldPosition.getX() + 0.5 - fx) * 0.1;
            double dy = (worldPosition.getY() + 1.0 - fy) * 0.1;
            double dz = (worldPosition.getZ() + 0.5 - fz) * 0.1;
            
            level.sendParticles(ParticleTypes.ENCHANT, fx, fy, fz, 3, dx, dy, dz, 0.5);
        }
        
        double cx = worldPosition.getX() + 0.5;
        double cy = worldPosition.getY() + 1.2;
        double cz = worldPosition.getZ() + 0.5;
        double angle = (enchantProgress * 0.3) % (Math.PI * 2);
        double radius = 0.5;
        level.sendParticles(ParticleTypes.PORTAL,
            cx + Math.cos(angle) * radius, cy, cz + Math.sin(angle) * radius,
            2, 0, 0.2, 0, 0);
    }
    
    // アイテム操作
    public void setItem(ItemStack stack) {
        this.heldItem = stack;
        setChanged();
    }
    
    public ItemStack getItem() {
        return heldItem;
    }
    
    public ItemStack removeItem() {
        ItemStack removed = heldItem;
        heldItem = ItemStack.EMPTY;
        setChanged();
        return removed;
    }
    
    // Getters
    public float getStoredMana() { return storedMana; }
    public float getMaxMana() { return MAX_MANA; }
    public float getStoredMoonlight() { return storedMana; }
    public float getMaxMoonlight() { return MAX_MANA; }
    public boolean isStructureValid() { return isStructureValid; }
    public boolean isEnchanting() { return isEnchanting; }
    public int getEnchantProgress() { return enchantProgress; }
    public int getEnchantTime() { return ENCHANT_TIME; }
    public String getCurrentPattern() { return currentPattern; }
    public Set<BlockPos> getLinkedFocuses() { return linkedFocuses; }
    public String getDebugPillarInfo() { return debugPillarInfo; }
    
    @Nullable
    public CelestialEnchantRecipe getCurrentRecipe() { return currentRecipe; }
    
    @Nullable
    public String getDetectedPattern() {
        return currentPattern.isEmpty() ? null : currentPattern;
    }
    
    // NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Item", heldItem.save(new CompoundTag()));
        tag.putFloat("Mana", storedMana);
        tag.putBoolean("Enchanting", isEnchanting);
        tag.putInt("Progress", enchantProgress);
        tag.putString("Pattern", currentPattern);
        tag.putBoolean("StructureValid", isStructureValid);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heldItem = ItemStack.of(tag.getCompound("Item"));
        storedMana = tag.getFloat("Mana");
        isEnchanting = tag.getBoolean("Enchanting");
        enchantProgress = tag.getInt("Progress");
        currentPattern = tag.getString("Pattern");
        isStructureValid = tag.getBoolean("StructureValid");
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
    
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
