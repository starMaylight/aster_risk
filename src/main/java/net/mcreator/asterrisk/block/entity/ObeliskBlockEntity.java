package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * オベリスクBlockEntity - 特定の時間帯にエネルギーを蓄積
 */
public class ObeliskBlockEntity extends BlockEntity {
    
    private ObeliskEnergyType energyType;
    private int storedEnergy = 0;
    private int maxEnergy = 1000;
    private int generateRate = 5;
    private Set<BlockPos> linkedAltars = new HashSet<>();
    
    public ObeliskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OBELISK.get(), pos, state);
    }
    
    public void setEnergyType(ObeliskEnergyType type) {
        this.energyType = type;
        setChanged();
    }
    
    public ObeliskEnergyType getEnergyType() {
        return energyType;
    }
    
    public int getStoredEnergy() {
        return storedEnergy;
    }
    
    public int getMaxEnergy() {
        return maxEnergy;
    }
    
    public boolean hasEnergy(int amount) {
        return storedEnergy >= amount;
    }
    
    public boolean consumeEnergy(int amount) {
        if (storedEnergy >= amount) {
            storedEnergy -= amount;
            setChanged();
            return true;
        }
        return false;
    }
    
    public void addLinkedAltar(BlockPos pos) {
        linkedAltars.add(pos);
        setChanged();
    }
    
    public void removeLinkedAltar(BlockPos pos) {
        linkedAltars.remove(pos);
        setChanged();
    }
    
    public void clearLinkedAltars() {
        linkedAltars.clear();
        setChanged();
    }
    
    public Set<BlockPos> getLinkedAltars() {
        return linkedAltars;
    }
    
    public boolean isLinkedTo(BlockPos altarPos) {
        return linkedAltars.contains(altarPos);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ObeliskBlockEntity entity) {
        if (entity.energyType == null) return;
        
        boolean canGenerate = entity.canGenerateEnergy(level);
        
        if (canGenerate && entity.storedEnergy < entity.maxEnergy) {
            int bonus = entity.getGenerationBonus(level);
            entity.storedEnergy = Math.min(entity.maxEnergy, entity.storedEnergy + entity.generateRate + bonus);
            entity.setChanged();
            
            // パーティクル
            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 20 == 0) {
                entity.spawnEnergyParticles(serverLevel, pos);
            }
        }
    }
    
    private boolean canGenerateEnergy(Level level) {
        if (energyType == null) return false;
        
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;
        boolean isDay = !isNight;
        int moonPhase = getMoonPhase(level);
        boolean isNewMoon = moonPhase == 4;
        boolean canSeeSky = level.canSeeSky(worldPosition.above());
        
        if (!canSeeSky) return false;
        
        return switch (energyType) {
            case LUNAR -> isNight;
            case STELLAR -> isNight && !level.isRaining();
            case SOLAR -> isDay && !level.isRaining();
            case VOID -> isNight && isNewMoon;
        };
    }
    
    private int getGenerationBonus(Level level) {
        if (energyType == null) return 0;
        
        int moonPhase = getMoonPhase(level);
        
        return switch (energyType) {
            case LUNAR -> (moonPhase == 0) ? 10 : 0; // 満月ボーナス
            case STELLAR -> level.isRaining() ? -5 : 3; // 晴天ボーナス
            case SOLAR -> (level.getDayTime() % 24000 >= 5000 && level.getDayTime() % 24000 <= 7000) ? 8 : 0; // 正午ボーナス
            case VOID -> (moonPhase == 4) ? 15 : 0; // 新月時のみ大幅ボーナス
        };
    }
    
    private int getMoonPhase(Level level) {
        return (int)(level.getDayTime() / 24000L % 8L);
    }
    
    private void spawnEnergyParticles(ServerLevel level, BlockPos pos) {
        if (energyType == null) return;
        
        var particleType = switch (energyType) {
            case LUNAR -> ParticleTypes.END_ROD;
            case STELLAR -> ParticleTypes.FIREWORK;
            case SOLAR -> ParticleTypes.FLAME;
            case VOID -> ParticleTypes.PORTAL;
        };
        
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 1.5 + level.random.nextDouble() * 0.5;
            double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
            level.sendParticles(particleType, x, y, z, 1, 0, 0.05, 0, 0.01);
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (energyType != null) {
            tag.putString("EnergyType", energyType.getName());
        }
        tag.putInt("StoredEnergy", storedEnergy);
        
        // リンク保存
        int[] altarData = new int[linkedAltars.size() * 3];
        int i = 0;
        for (BlockPos altarPos : linkedAltars) {
            altarData[i++] = altarPos.getX();
            altarData[i++] = altarPos.getY();
            altarData[i++] = altarPos.getZ();
        }
        tag.putIntArray("LinkedAltars", altarData);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("EnergyType")) {
            energyType = ObeliskEnergyType.fromName(tag.getString("EnergyType"));
        }
        storedEnergy = tag.getInt("StoredEnergy");
        
        // リンク読み込み
        linkedAltars.clear();
        if (tag.contains("LinkedAltars")) {
            int[] altarData = tag.getIntArray("LinkedAltars");
            for (int i = 0; i < altarData.length; i += 3) {
                linkedAltars.add(new BlockPos(altarData[i], altarData[i + 1], altarData[i + 2]));
            }
        }
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
