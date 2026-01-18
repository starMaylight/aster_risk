package net.mcreator.asterrisk.pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

/**
 * パターンマネージャー
 * Focus/PedestalパターンをJSONファイルから読み込み管理
 * 
 * パターンJSONの場所:
 * - data/aster_risk/aster_risk/patterns/focus/*.json
 * - data/aster_risk/aster_risk/patterns/pedestal/*.json
 */
public class PatternManager extends SimpleJsonResourceReloadListener {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // シングルトンインスタンス
    private static PatternManager INSTANCE;
    
    // パターン格納
    private Map<ResourceLocation, FocusPattern> focusPatterns = new HashMap<>();
    private Map<ResourceLocation, PedestalPattern> pedestalPatterns = new HashMap<>();
    
    public PatternManager() {
        super(GSON, "aster_risk/patterns");
        INSTANCE = this;
    }
    
    public static PatternManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PatternManager();
        }
        return INSTANCE;
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        // クリア
        focusPatterns.clear();
        pedestalPatterns.clear();
        
        // JSONからパターンを読み込み
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonObject json = entry.getValue().getAsJsonObject();
            
            try {
                String type = json.has("type") ? json.get("type").getAsString() : "";
                
                if (type.equals("focus") || id.getPath().startsWith("focus/")) {
                    FocusPattern pattern = FocusPattern.fromJson(id, json);
                    focusPatterns.put(id, pattern);
                    AsterRiskMod.LOGGER.debug("Loaded focus pattern: {}", id);
                } else if (type.equals("pedestal") || id.getPath().startsWith("pedestal/")) {
                    PedestalPattern pattern = PedestalPattern.fromJson(id, json);
                    pedestalPatterns.put(id, pattern);
                    AsterRiskMod.LOGGER.debug("Loaded pedestal pattern: {}", id);
                }
            } catch (Exception e) {
                AsterRiskMod.LOGGER.error("Failed to load pattern: " + id, e);
            }
        }
        
        AsterRiskMod.LOGGER.info("Loaded {} focus patterns, {} pedestal patterns", 
            focusPatterns.size(), pedestalPatterns.size());
    }
    
    // ===== Getters =====
    
    public Collection<FocusPattern> getAllFocusPatterns() {
        return focusPatterns.values();
    }
    
    public Collection<PedestalPattern> getAllPedestalPatterns() {
        return pedestalPatterns.values();
    }
    
    public FocusPattern getFocusPattern(ResourceLocation id) {
        return focusPatterns.get(id);
    }
    
    public PedestalPattern getPedestalPattern(ResourceLocation id) {
        return pedestalPatterns.get(id);
    }
    
    public FocusPattern getFocusPatternByName(String name) {
        for (FocusPattern pattern : focusPatterns.values()) {
            if (pattern.getName().equalsIgnoreCase(name)) {
                return pattern;
            }
        }
        return null;
    }
    
    public PedestalPattern getPedestalPatternByName(String name) {
        for (PedestalPattern pattern : pedestalPatterns.values()) {
            if (pattern.getName().equalsIgnoreCase(name)) {
                return pattern;
            }
        }
        return null;
    }
    
    /**
     * 名前からFocusパターンの位置リストを取得
     */
    public List<BlockPos> getFocusPositions(String patternName) {
        FocusPattern pattern = getFocusPatternByName(patternName);
        return pattern != null ? pattern.getPositions() : null;
    }
    
    /**
     * 名前からPedestalパターンの位置リストを取得
     */
    public List<BlockPos> getPedestalPositions(String patternName) {
        PedestalPattern pattern = getPedestalPatternByName(patternName);
        return pattern != null ? pattern.getPositions() : null;
    }
    
    /**
     * 全Focusパターンを名前→位置のMapで取得
     */
    public Map<String, List<BlockPos>> getAllFocusPatternsAsMap() {
        Map<String, List<BlockPos>> map = new LinkedHashMap<>();
        for (FocusPattern pattern : focusPatterns.values()) {
            map.put(pattern.getName(), pattern.getPositions());
        }
        return map;
    }
    
    /**
     * 全Pedestalパターンを名前→位置のMapで取得
     */
    public Map<String, List<BlockPos>> getAllPedestalPatternsAsMap() {
        Map<String, List<BlockPos>> map = new LinkedHashMap<>();
        for (PedestalPattern pattern : pedestalPatterns.values()) {
            map.put(pattern.getName(), pattern.getPositions());
        }
        return map;
    }
}
