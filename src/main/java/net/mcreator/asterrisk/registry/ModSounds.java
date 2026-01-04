package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Aster Risk Modのカスタムサウンド登録
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AsterRiskMod.MODID);

    // === 魔法系サウンド ===
    
    // マナ充填音
    public static final RegistryObject<SoundEvent> MANA_CHARGE = registerSound("mana_charge");
    
    // マナ消費音
    public static final RegistryObject<SoundEvent> MANA_CONSUME = registerSound("mana_consume");
    
    // 魔法発動音
    public static final RegistryObject<SoundEvent> SPELL_CAST = registerSound("spell_cast");
    
    // 月光魔法音
    public static final RegistryObject<SoundEvent> LUNAR_MAGIC = registerSound("lunar_magic");
    
    // 星屑魔法音
    public static final RegistryObject<SoundEvent> STELLAR_MAGIC = registerSound("stellar_magic");
    
    // === ブロック系サウンド ===
    
    // 錬金釜作動音
    public static final RegistryObject<SoundEvent> CAULDRON_BUBBLE = registerSound("cauldron_bubble");
    
    // 祭壇起動音
    public static final RegistryObject<SoundEvent> ALTAR_ACTIVATE = registerSound("altar_activate");
    
    // 儀式完了音
    public static final RegistryObject<SoundEvent> RITUAL_COMPLETE = registerSound("ritual_complete");
    
    // 月相の金床打撃音
    public static final RegistryObject<SoundEvent> PHASE_ANVIL_USE = registerSound("phase_anvil_use");
    
    // 共振器リンク音
    public static final RegistryObject<SoundEvent> RESONATOR_LINK = registerSound("resonator_link");
    
    // === 環境音 ===
    
    // 月の領域環境音
    public static final RegistryObject<SoundEvent> LUNAR_REALM_AMBIENT = registerSound("lunar_realm_ambient");
    
    // 流星落下音
    public static final RegistryObject<SoundEvent> METEOR_FALL = registerSound("meteor_fall");
    
    // 流星衝突音
    public static final RegistryObject<SoundEvent> METEOR_IMPACT = registerSound("meteor_impact");
    
    // === エンティティ系サウンド ===
    
    // ボス登場音
    public static final RegistryObject<SoundEvent> BOSS_SPAWN = registerSound("boss_spawn");
    
    // ボス攻撃音
    public static final RegistryObject<SoundEvent> BOSS_ATTACK = registerSound("boss_attack");
    
    // ボス死亡音
    public static final RegistryObject<SoundEvent> BOSS_DEATH = registerSound("boss_death");
    
    // 虚空エンティティ音
    public static final RegistryObject<SoundEvent> VOID_WHISPER = registerSound("void_whisper");
    
    // === アイテム系サウンド ===
    
    // 虹色武器ヒット音
    public static final RegistryObject<SoundEvent> PRISMATIC_HIT = registerSound("prismatic_hit");
    
    // 影の大鎌スイング音
    public static final RegistryObject<SoundEvent> SHADOW_SWING = registerSound("shadow_swing");
    
    // 虚空の短剣ヒット音
    public static final RegistryObject<SoundEvent> VOID_STRIKE = registerSound("void_strike");
    
    private static RegistryObject<SoundEvent> registerSound(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, name)));
    }
}
