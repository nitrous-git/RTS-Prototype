package Faction;

import Util.TileMap;
import Util.Vector2Int;

import java.util.ArrayList;
import java.util.List;

public class SpawnSeedRepository {

    private final List<Vector2Int> availableSeeds = new ArrayList<>();

    public SpawnSeedRepository(TileMap map) {
        int maxX = map.column;
        int maxY = map.row;
        int BUFFER = 4;

        // 5 available spawn point selected manually
        // we should retrieve them from a Token on the map.intArr
        availableSeeds.add(new Vector2Int(BUFFER, BUFFER));                               // top-left
        availableSeeds.add(new Vector2Int(maxX - BUFFER - 1, BUFFER));                 // top-right
        availableSeeds.add(new Vector2Int(BUFFER, maxY - BUFFER - 1));                 // bottom-left
        availableSeeds.add(new Vector2Int(maxX - BUFFER - 1, maxY - BUFFER - 1));   // bottom-right
        availableSeeds.add(new Vector2Int(maxX / 2, maxY / 2));                     // center
    }

    public Vector2Int requestSeed(int preferredIndex) {
        // Try to pick the preferred seed
        if (preferredIndex >= 0 && preferredIndex < availableSeeds.size()) {
            Vector2Int preferred = availableSeeds.get(preferredIndex);
            availableSeeds.remove(preferred);  // mark as used
            return preferred;
        }

        // Fallback to first available
        if (!availableSeeds.isEmpty()) {
            return availableSeeds.remove(0);
        }

        throw new RuntimeException("No spawn seeds available.");
    }

}
