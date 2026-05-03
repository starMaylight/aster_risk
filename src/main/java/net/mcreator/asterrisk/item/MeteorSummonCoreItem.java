package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
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
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        String typeKey = "tooltip.aster_risk.meteor_summon_core." + type.getName() + ".type";
        TooltipHelper.addInfo(tooltip, ChatFormatting.GRAY,
            "tooltip.aster_risk.meteor_summon_core.type", Component.translatable(typeKey));

        ChatFormatting effectColor = switch (type) {
            case SMALL -> ChatFormatting.GRAY;
            case STARDUST -> ChatFormatting.AQUA;
            case PRISMATIC -> ChatFormatting.LIGHT_PURPLE;
            case OMINOUS -> ChatFormatting.DARK_RED;
        };
        TooltipHelper.addStat(tooltip, effectColor, "tooltip.aster_risk.meteor_summon_core." + type.getName() + ".effect");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.meteor_summon_core.usage");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == MeteorType.PRISMATIC || type == MeteorType.OMINOUS;
    }
}
