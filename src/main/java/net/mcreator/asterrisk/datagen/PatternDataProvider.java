package net.mcreator.asterrisk.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.BlockPos;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * パターンJSONデータ生成プロバイダー
 * ビルド時にJSONファイルを自動生成
 */
public class PatternDataProvider implements DataProvider {
    
    private final PackOutput output;
    private final List<PatternEntry> focusPatterns = new ArrayList<>();
    private final List<PatternEntry> pedestalPatterns = new ArrayList<>();
    
    public PatternDataProvider(PackOutput output) {
        this.output = output;
        registerPatterns();
    }
    
    /**
     * パターンを登録
     */
    private void registerPatterns() {
        // ===== Focus Patterns =====
        
        // 基本パターン
        addFocusPattern("lunar_crescent", "Basic crescent moon shape", Arrays.asList(
            new BlockPos(-2, 1, -1),
            new BlockPos(-2, 1, 0),
            new BlockPos(-2, 1, 1),
            new BlockPos(-1, 1, 2),
            new BlockPos(0, 1, 2)
        ));
        
        addFocusPattern("star_cross", "Cross pattern with elevated center", Arrays.asList(
            new BlockPos(0, 2, 0),
            new BlockPos(2, 1, 0),
            new BlockPos(-2, 1, 0),
            new BlockPos(0, 1, 2),
            new BlockPos(0, 1, -2)
        ));
        
        addFocusPattern("constellation", "Multi-point constellation shape", Arrays.asList(
            new BlockPos(-1, 1, -2),
            new BlockPos(0, 2, -1),
            new BlockPos(1, 1, -2),
            new BlockPos(-1, 1, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 2, 1),
            new BlockPos(1, 2, 1)
        ));
        
        addFocusPattern("eclipse_ring", "Ring pattern around the table", Arrays.asList(
            new BlockPos(2, 1, 0),
            new BlockPos(-2, 1, 0),
            new BlockPos(0, 1, 2),
            new BlockPos(0, 1, -2),
            new BlockPos(1, 1, 1),
            new BlockPos(-1, 1, 1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, -1)
        ));
        
        addFocusPattern("void_spiral", "Ascending spiral pattern", Arrays.asList(
            new BlockPos(1, 1, 0),
            new BlockPos(1, 1, 1),
            new BlockPos(0, 2, 2),
            new BlockPos(-1, 2, 1),
            new BlockPos(-1, 3, 0),
            new BlockPos(0, 3, -1)
        ));
        
        // 専用エンチャント用パターン
        addFocusPattern("heavens_hammer", "Cross pattern for Heaven's Hammer enchant", Arrays.asList(
            new BlockPos(0, 2, 3),
            new BlockPos(0, 2, -3),
            new BlockPos(3, 2, 0),
            new BlockPos(-3, 2, 0)
        ));
        
        addFocusPattern("cursed_mirror", "Diagonal corners for Cursed Mirror enchant", Arrays.asList(
            new BlockPos(1, 1, 1),
            new BlockPos(-1, 1, -1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, 1)
        ));
        
        addFocusPattern("lucky_nova", "Extended star for Lucky Nova enchant", Arrays.asList(
            new BlockPos(0, 1, 3),
            new BlockPos(0, 1, -3),
            new BlockPos(3, 1, 0),
            new BlockPos(-3, 1, 0),
            new BlockPos(2, 1, 2),
            new BlockPos(-2, 1, 2)
        ));
        
        addFocusPattern("mana_focus", "Concentrated pattern for Mana Focus enchant", Arrays.asList(
            new BlockPos(1, 2, 0),
            new BlockPos(-1, 2, 0),
            new BlockPos(0, 2, 1),
            new BlockPos(0, 2, -1),
            new BlockPos(0, 3, 0)
        ));
        
        addFocusPattern("stellar_core", "3D octahedron for Stellar Core enchant", Arrays.asList(
            new BlockPos(0, 3, 0),
            new BlockPos(2, 1, 0),
            new BlockPos(-2, 1, 0),
            new BlockPos(0, 1, 2),
            new BlockPos(0, 1, -2),
            new BlockPos(1, 2, 1),
            new BlockPos(-1, 2, -1)
        ));
        
        // ===== Pedestal Patterns =====
        
        addPedestalPattern("cross", "Basic cross pattern", Arrays.asList(
            new BlockPos(2, 0, 0),
            new BlockPos(-2, 0, 0),
            new BlockPos(0, 0, 2),
            new BlockPos(0, 0, -2)
        ));
        
        addPedestalPattern("diamond", "Four corners pattern", Arrays.asList(
            new BlockPos(2, 0, 2),
            new BlockPos(-2, 0, 2),
            new BlockPos(2, 0, -2),
            new BlockPos(-2, 0, -2)
        ));
        
        addPedestalPattern("star", "Eight-point star pattern", Arrays.asList(
            new BlockPos(2, 0, 0),
            new BlockPos(-2, 0, 0),
            new BlockPos(0, 0, 2),
            new BlockPos(0, 0, -2),
            new BlockPos(2, 0, 2),
            new BlockPos(-2, 0, 2),
            new BlockPos(2, 0, -2),
            new BlockPos(-2, 0, -2)
        ));
        
        addPedestalPattern("hexagon", "Six-point hexagonal pattern", Arrays.asList(
            new BlockPos(2, 0, 0),
            new BlockPos(-2, 0, 0),
            new BlockPos(1, 0, 2),
            new BlockPos(-1, 0, 2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, -2)
        ));
        
        addPedestalPattern("triangle", "Three-point triangle pattern", Arrays.asList(
            new BlockPos(0, 0, -2),
            new BlockPos(2, 0, 2),
            new BlockPos(-2, 0, 2)
        ));
    }
    
    private void addFocusPattern(String name, String description, List<BlockPos> positions) {
        focusPatterns.add(new PatternEntry(name, description, positions));
    }
    
    private void addPedestalPattern(String name, String description, List<BlockPos> positions) {
        pedestalPatterns.add(new PatternEntry(name, description, positions));
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        Path basePath = output.getOutputFolder().resolve("data/" + AsterRiskMod.MODID + "/aster_risk/patterns");
        
        // Focus patterns
        for (PatternEntry entry : focusPatterns) {
            Path path = basePath.resolve("focus/" + entry.name + ".json");
            futures.add(DataProvider.saveStable(cache, entry.toJson("focus"), path));
        }
        
        // Pedestal patterns
        for (PatternEntry entry : pedestalPatterns) {
            Path path = basePath.resolve("pedestal/" + entry.name + ".json");
            futures.add(DataProvider.saveStable(cache, entry.toJson("pedestal"), path));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    @Override
    public String getName() {
        return "Aster Risk Patterns";
    }
    
    /**
     * パターンエントリ
     */
    private static class PatternEntry {
        final String name;
        final String description;
        final List<BlockPos> positions;
        
        PatternEntry(String name, String description, List<BlockPos> positions) {
            this.name = name;
            this.description = description;
            this.positions = positions;
        }
        
        JsonObject toJson(String type) {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("name", name.toUpperCase());
            json.addProperty("description", description);
            
            JsonArray posArray = new JsonArray();
            for (BlockPos pos : positions) {
                JsonObject posJson = new JsonObject();
                posJson.addProperty("x", pos.getX());
                posJson.addProperty("y", pos.getY());
                posJson.addProperty("z", pos.getZ());
                posArray.add(posJson);
            }
            json.add("positions", posArray);
            
            return json;
        }
    }
}
