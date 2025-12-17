package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.entity.AltarCoreBlockEntity;
import net.mcreator.asterrisk.block.entity.LunarCollectorBlockEntity;
import net.mcreator.asterrisk.block.entity.LunarInfuserBlockEntity;
import net.mcreator.asterrisk.block.entity.ManaBatteryBlockEntity;
import net.mcreator.asterrisk.block.entity.MoonlightBeaconBlockEntity;
import net.mcreator.asterrisk.block.entity.ResonatorBlockEntity;
import net.mcreator.asterrisk.block.entity.RitualPedestalBlockEntity;
import net.mcreator.asterrisk.block.entity.StarAnvilBlockEntity;
import net.mcreator.asterrisk.block.entity.ObeliskBlockEntity;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * BlockEntity登録（MCreatorから独立したパッケージ）
 */
public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AsterRiskMod.MODID);

    // 月光収集器
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<LunarCollectorBlockEntity>> LUNAR_COLLECTOR = 
        REGISTRY.register("lunar_collector", () -> 
            BlockEntityType.Builder.of(
                LunarCollectorBlockEntity::create, 
                AsterRiskModBlocks.LUNAR_COLLECTOR.get())
            .build(null));

    // 共振器 Tier1
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ResonatorBlockEntity>> RESONATOR_TIER1 = 
        REGISTRY.register("resonator_tier1", () -> 
            BlockEntityType.Builder.of(
                ResonatorBlockEntity::createTier1,
                AsterRiskModBlocks.RESONATOR_TIER1.get())
            .build(null));

    // 共振器 Tier2
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ResonatorBlockEntity>> RESONATOR_TIER2 = 
        REGISTRY.register("resonator_tier2", () -> 
            BlockEntityType.Builder.of(
                ResonatorBlockEntity::createTier2,
                AsterRiskModBlocks.RESONATOR_TIER2.get())
            .build(null));

    // 共振器 Tier3
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ResonatorBlockEntity>> RESONATOR_TIER3 = 
        REGISTRY.register("resonator_tier3", () -> 
            BlockEntityType.Builder.of(
                ResonatorBlockEntity::createTier3,
                AsterRiskModBlocks.RESONATOR_TIER3.get())
            .build(null));

    // マナバッテリー
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ManaBatteryBlockEntity>> MANA_BATTERY = 
        REGISTRY.register("mana_battery", () -> 
            BlockEntityType.Builder.of(
                ManaBatteryBlockEntity::create,
                AsterRiskModBlocks.MANA_BATTERY.get())
            .build(null));

    // 儀式台座
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<RitualPedestalBlockEntity>> RITUAL_PEDESTAL = 
        REGISTRY.register("ritual_pedestal", () -> 
            BlockEntityType.Builder.of(
                RitualPedestalBlockEntity::create,
                AsterRiskModBlocks.RITUAL_PEDESTAL.get())
            .build(null));

    // 祭壇コア
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<AltarCoreBlockEntity>> ALTAR_CORE = 
        REGISTRY.register("altar_core", () -> 
            BlockEntityType.Builder.of(
                AltarCoreBlockEntity::create,
                AsterRiskModBlocks.ALTAR_CORE.get())
            .build(null));

    // === Phase 6.1: 機能ブロック ===

    // 月光ビーコン
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<MoonlightBeaconBlockEntity>> MOONLIGHT_BEACON = 
        REGISTRY.register("moonlight_beacon", () -> 
            BlockEntityType.Builder.of(
                MoonlightBeaconBlockEntity::create,
                AsterRiskModBlocks.MOONLIGHT_BEACON.get())
            .build(null));

    // 月光注入機
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<LunarInfuserBlockEntity>> LUNAR_INFUSER = 
        REGISTRY.register("lunar_infuser", () -> 
            BlockEntityType.Builder.of(
                LunarInfuserBlockEntity::create,
                AsterRiskModBlocks.LUNAR_INFUSER.get())
            .build(null));

    // 星の金床
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<StarAnvilBlockEntity>> STAR_ANVIL = 
        REGISTRY.register("star_anvil", () -> 
            BlockEntityType.Builder.of(
                StarAnvilBlockEntity::create,
                AsterRiskModBlocks.STAR_ANVIL.get())
            .build(null));

    // === オベリスク ===

    // オベリスク（全タイプ共通BlockEntity）
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ObeliskBlockEntity>> OBELISK = 
        REGISTRY.register("obelisk", () -> 
            BlockEntityType.Builder.of(
                ObeliskBlockEntity::new,
                AsterRiskModBlocks.LUNAR_OBELISK.get(),
                AsterRiskModBlocks.STELLAR_OBELISK.get(),
                AsterRiskModBlocks.SOLAR_OBELISK.get(),
                AsterRiskModBlocks.VOID_OBELISK.get())
            .build(null));
}
