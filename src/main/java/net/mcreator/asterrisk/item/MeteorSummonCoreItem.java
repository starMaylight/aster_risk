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
 * 流星召喚の核 - 流星召喚陣の起動に使用
 */
public class MeteorSummonCoreItem extends Item {
    
    public enum MeteorType {
        SMALL("small", "小流星", Rarity.COMMON),
        STARDUST("stardust", "星屑流星", Rarity.UNCOMMON),
        PRISMATIC("prismatic", "虹流星", Rarity.RARE),
        OMINOUS("ominous", "凶星", Rarity.EPIC);

        private final String name;
        private final String displayName;
        private final Rarity rarity;

        MeteorType(String name, String displayName, Rarity rarity) {
            this.name = name;
            this.displayName = displayName;
            this.rarity = rarity;
        }

        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public Rarity getRarity() { return rarity; }
    }

    private final MeteorType type;

    public MeteorSummonCoreItem(MeteorType type) {
        super(new Properties().stacksTo(16).rarity(type.getRarity()));
        this.type = type;
    }

    public MeteorType getMeteorType() {
        return type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Type: §f" + type.getDisplayName()));
        
        String description = switch (type) {
            case SMALL -> "§7Drops Meteorite Fragments";
            case STARDUST -> "§bDrops large amounts of Stardust";
            case PRISMATIC -> "§dDrops Prismatic Meteorite (Rare!)";
            case OMINOUS -> "§4WARNING: Summons hostile entities!";
        };
        tooltip.add(Component.literal(description));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Place in Meteor Summoning Circle"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == MeteorType.PRISMATIC || type == MeteorType.OMINOUS;
    }
}
