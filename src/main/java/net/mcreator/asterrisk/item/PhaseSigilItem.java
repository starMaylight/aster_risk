package net.mcreator.asterrisk.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月相の刻印 - 装備に月の力を付与するアイテム
 */
public class PhaseSigilItem extends Item {
    
    private final MoonPhase phase;

    public enum MoonPhase {
        FULL_MOON(0, "full_moon", "満月", 0xFFFFAA),
        WANING_GIBBOUS(1, "waning_gibbous", "更待月", 0xDDDDAA),
        LAST_QUARTER(2, "last_quarter", "下弦", 0xAAAA88),
        WANING_CRESCENT(3, "waning_crescent", "有明月", 0x888866),
        NEW_MOON(4, "new_moon", "新月", 0x444444),
        WAXING_CRESCENT(5, "waxing_crescent", "三日月", 0x666688),
        FIRST_QUARTER(6, "first_quarter", "上弦", 0x8888AA),
        WAXING_GIBBOUS(7, "waxing_gibbous", "十三夜", 0xAAAADD);

        private final int index;
        private final String name;
        private final String displayName;
        private final int color;

        MoonPhase(int index, String name, String displayName, int color) {
            this.index = index;
            this.name = name;
            this.displayName = displayName;
            this.color = color;
        }

        public int getIndex() { return index; }
        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public int getColor() { return color; }

        public static MoonPhase fromIndex(int index) {
            for (MoonPhase phase : values()) {
                if (phase.index == index) return phase;
            }
            return FULL_MOON;
        }
    }

    public PhaseSigilItem(MoonPhase phase) {
        super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
        this.phase = phase;
    }

    public MoonPhase getPhase() {
        return phase;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Moon Phase: §f" + phase.getDisplayName()));
        
        String effect = switch (phase) {
            case FULL_MOON -> "§c+15% Attack Damage";
            case WANING_GIBBOUS -> "§9+10% Magic Damage";
            case LAST_QUARTER -> "§b+12% Defense";
            case WANING_CRESCENT -> "§d+8% Mana Regen";
            case NEW_MOON -> "§8Stealth: Reduced detection";
            case WAXING_CRESCENT -> "§a+10% Movement Speed";
            case FIRST_QUARTER -> "§e+15% Mining Speed";
            case WAXING_GIBBOUS -> "§6+10% Experience Gain";
        };
        tooltip.add(Component.literal(effect));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Use at Phase Anvil to apply"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return phase == MoonPhase.FULL_MOON || phase == MoonPhase.NEW_MOON;
    }
}
