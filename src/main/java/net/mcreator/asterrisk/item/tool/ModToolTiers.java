package net.mcreator.asterrisk.item.tool;

import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Aster Risk Modのツールティア定義
 */
public enum ModToolTiers implements Tier {
    
    // 銀: 鉄より少し上、エンチャント適性が高い
    SILVER(2, 300, 6.5f, 2.0f, 18, () -> Ingredient.of(AsterRiskModItems.SILVER_INGOT.get())),
    
    // 月光石: ダイヤ相当、夜間ボーナス用
    MOONSTONE(3, 1200, 8.0f, 3.0f, 15, () -> Ingredient.of(AsterRiskModItems.MOONSTONE.get())),
    
    // 隕石: 高耐久、重い
    METEORITE(3, 1800, 7.0f, 3.5f, 10, () -> Ingredient.of(AsterRiskModItems.METEORITE_FRAGMENT.get())),
    
    // 虹色隕石: 最高級
    PRISMATIC(4, 2500, 10.0f, 4.5f, 22, () -> Ingredient.of(AsterRiskModItems.PRISMATIC_METEORITE.get()));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    ModToolTiers(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() { return uses; }

    @Override
    public float getSpeed() { return speed; }

    @Override
    public float getAttackDamageBonus() { return damage; }

    @Override
    public int getLevel() { return level; }

    @Override
    public int getEnchantmentValue() { return enchantmentValue; }

    @Override
    public Ingredient getRepairIngredient() { return repairIngredient.get(); }
}
