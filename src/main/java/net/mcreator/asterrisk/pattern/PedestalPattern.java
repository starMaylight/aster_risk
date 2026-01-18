package net.mcreator.asterrisk.pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 台座パターン定義
 * RitualCircle用の台座配置パターン
 */
public class PedestalPattern {
    private final ResourceLocation id;
    private final String name;
    private final String description;
    private final List<BlockPos> positions;
    
    public PedestalPattern(ResourceLocation id, String name, String description, List<BlockPos> positions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.positions = positions;
    }
    
    public ResourceLocation getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<BlockPos> getPositions() { return positions; }
    
    /**
     * JSONからパターンを読み込む
     */
    public static PedestalPattern fromJson(ResourceLocation id, JsonObject json) {
        String name = json.has("name") ? json.get("name").getAsString() : id.getPath();
        String description = json.has("description") ? json.get("description").getAsString() : "";
        
        List<BlockPos> positions = new ArrayList<>();
        if (json.has("positions")) {
            JsonArray posArray = json.getAsJsonArray("positions");
            for (JsonElement elem : posArray) {
                JsonObject posObj = elem.getAsJsonObject();
                int x = posObj.get("x").getAsInt();
                int y = posObj.has("y") ? posObj.get("y").getAsInt() : 0; // 台座はY=0がデフォルト
                int z = posObj.get("z").getAsInt();
                positions.add(new BlockPos(x, y, z));
            }
        }
        
        return new PedestalPattern(id, name, description, positions);
    }
    
    /**
     * パターンをJSONに変換
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        if (!description.isEmpty()) {
            json.addProperty("description", description);
        }
        
        JsonArray posArray = new JsonArray();
        for (BlockPos pos : positions) {
            JsonObject posObj = new JsonObject();
            posObj.addProperty("x", pos.getX());
            if (pos.getY() != 0) {
                posObj.addProperty("y", pos.getY());
            }
            posObj.addProperty("z", pos.getZ());
            posArray.add(posObj);
        }
        json.add("positions", posArray);
        
        return json;
    }
}
