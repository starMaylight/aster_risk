package net.mcreator.asterrisk.damage;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Mod独自のダメージタイプ
 */
public class ModDamageTypes {

    /**
     * 太陽の接触ダメージ。
     * 防具・耐性・エンチャント保護をすべて貫通する。
     */
    public static final ResourceKey<DamageType> SOLAR_CONTACT = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "solar_contact"));

    /**
     * 太陽の接触ダメージ源を生成。
     * データパックが読み込めない環境ではnullを返す（呼び出し側でフォールバックする）。
     */
    public static DamageSource solarContact(Level level, Entity attacker) {
        try {
            return new DamageSource(
                level.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(SOLAR_CONTACT),
                attacker);
        } catch (Exception e) {
            return null;
        }
    }
}
