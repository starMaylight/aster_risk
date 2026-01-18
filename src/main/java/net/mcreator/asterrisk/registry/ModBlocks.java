package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 新クラフトシステム用ブロック登録
 * このファイルはregistryパッケージにあるためMCreatorに上書きされない
 */
public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, AsterRiskMod.MODID);
    
    public static final DeferredRegister<Item> BLOCK_ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, AsterRiskMod.MODID);
    
    // === 魔法陣クラフトシステム ===
    public static final RegistryObject<Block> RITUAL_CIRCLE = registerBlock("ritual_circle",
        () -> new RitualCircleBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .strength(3.0f, 6.0f)
            .sound(SoundType.STONE)
            .lightLevel(state -> 7)
            .noOcclusion()));
    
    // === 月光集光システム ===
    public static final RegistryObject<Block> MOONLIGHT_FOCUS = registerBlock("moonlight_focus",
        () -> new MoonlightFocusBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(4.0f, 6.0f)
            .sound(SoundType.GLASS)
            .lightLevel(state -> 10)
            .noOcclusion()));
    
    public static final RegistryObject<Block> FOCUS_CHAMBER_CORE = registerBlock("focus_chamber_core",
        () -> new FocusChamberCoreBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_CYAN)
            .strength(5.0f, 8.0f)
            .sound(SoundType.METAL)
            .lightLevel(state -> 5)
            .noOcclusion()));
    
    // === 天体エンチャントシステム ===
    public static final RegistryObject<Block> CELESTIAL_ENCHANTING_TABLE = registerBlock("celestial_enchanting_table",
        () -> new CelestialEnchantingTableBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .strength(5.0f, 1200.0f)
            .sound(SoundType.STONE)
            .lightLevel(state -> 12)
            .noOcclusion()));
    
    /**
     * ブロックとそのBlockItemを同時に登録するヘルパーメソッド
     */
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
        BLOCK_ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
        return registeredBlock;
    }
    
    /**
     * 登録をイベントバスに追加
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }
}
