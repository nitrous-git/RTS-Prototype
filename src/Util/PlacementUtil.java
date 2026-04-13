package Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PlacementUtil {

    private PlacementUtil() {}

    public enum PlacementPolicy {
        CLOSEST,        // minimize distance to target (seeker or footprint center)
        MOST_OPEN,      // maximize openness around candidate
        OPEN_THEN_CLOSE // maximize (openWeight*open - distWeight*dist)
    }

    // ------------------------------------------------------------
    // Public API (single-depth)
    // ------------------------------------------------------------

    /**
     * Returns ALL FREE placements (walkable cells) on the ring at exact depth.
     * Ring = perimeter of (footprint expanded by depth).
     *
     * footprintTopLeft is in cell coordinates.
     */
    public static List<Vector2Int> getAllFreePlacementsOnRing(
            TileMap map,
            Vector2Int footprintTopLeft,
            int w, int h,
            int depth
    ) {
        if (map == null || footprintTopLeft == null) return Collections.emptyList();
        if (w <= 0 || h <= 0) return Collections.emptyList();
        if (depth < 0) depth = 0;

        int fx = footprintTopLeft.x;
        int fy = footprintTopLeft.y;

        int minX = fx - depth;
        int minY = fy - depth;
        int maxX = fx + (w - 1) + depth;
        int maxY = fy + (h - 1) + depth;

        ArrayList<Vector2Int> out = new ArrayList<>();

        // Top edge
        for (int x = minX; x <= maxX; x++) addIfWalkable(map, x, minY, out);

        // Bottom edge (avoid duplicate if same line)
        if (maxY != minY) {
            for (int x = minX; x <= maxX; x++) addIfWalkable(map, x, maxY, out);
        }

        // Left edge excluding corners (avoid duplicates)
        for (int y = minY + 1; y <= maxY - 1; y++) addIfWalkable(map, minX, y, out);

        // Right edge excluding corners (avoid duplicates; avoid duplicate if same column)
        if (maxX != minX) {
            for (int y = minY + 1; y <= maxY - 1; y++) addIfWalkable(map, maxX, y, out);
        }

        return out;
    }

    /**
     * Simple convenience: best = closest (Manhattan) to seekerCell if provided,
     * otherwise closest to footprint center.
     *
     * Returns null if none found at this depth.
     */
    public static Vector2Int getPlacementAroundFootprint(
            TileMap map,
            Vector2Int footprintTopLeft,
            int w, int h,
            int depth,
            Vector2Int seekerCell
    ) {
        List<Vector2Int> candidates = getAllFreePlacementsOnRing(map, footprintTopLeft, w, h, depth);
        if (candidates.isEmpty()) return null;

        final Vector2Int target = (seekerCell != null)
                ? seekerCell
                : footprintCenterCell(footprintTopLeft, w, h);

        return Collections.min(candidates, Comparator.comparingInt(c -> manhattan(c, target)));
    }

    /**
     * Scored selection on an exact depth ring.
     * - policy=CLOSEST: best distance (Manhattan)
     * - policy=MOST_OPEN: best openness
     * - policy=OPEN_THEN_CLOSE: maximize openWeight*open - distWeight*dist
     *
     * openRadius: neighborhood "radius" for openness (square). Typical rally: 5..10.
     */
    public static Vector2Int getPlacementAroundFootprintScored(
            TileMap map,
            Vector2Int footprintTopLeft,
            int w, int h,
            int depth,
            Vector2Int seekerCell,
            PlacementPolicy policy,
            int openRadius,
            int openWeight,
            int distWeight
    ) {
        List<Vector2Int> candidates = getAllFreePlacementsOnRing(map, footprintTopLeft, w, h, depth);
        if (candidates.isEmpty()) return null;

        final Vector2Int target = (seekerCell != null)
                ? seekerCell
                : footprintCenterCell(footprintTopLeft, w, h);

        Vector2Int best = null;
        int bestScore = Integer.MIN_VALUE;
        int bestOpen = Integer.MIN_VALUE;
        int bestDist = Integer.MAX_VALUE;

        for (Vector2Int c : candidates) {
            int open = freeCountInSquare(map, c, openRadius);
            int dist = manhattan(c, target);

            int score;
            switch (policy) {
                case CLOSEST:
                    score = -dist; // larger is better
                    break;
                case MOST_OPEN:
                    score = open;
                    break;
                case OPEN_THEN_CLOSE:
                default:
                    score = openWeight * open - distWeight * dist;
                    break;
            }

            // Stable tie-breakers:
            // 1) higher score
            // 2) higher openness
            // 3) smaller distance
            if (score > bestScore
                    || (score == bestScore && open > bestOpen)
                    || (score == bestScore && open == bestOpen && dist < bestDist)) {
                bestScore = score;
                bestOpen = open;
                bestDist = dist;
                best = c;
            }
        }

        return best;
    }

    // ------------------------------------------------------------
    // Public API (fallback depth)
    // ------------------------------------------------------------
    // map: tile grid (bounds + walkability via intArr)
    // footprintTopLeft: top-left cell of the target footprint
    // w, h: footprint size in tiles
    // depth: ring distance from footprint (0=perimeter, 1=1 tile away, ...)
    // seekerCell: requester cell (used for "closest" scoring); null => use footprint center
    // policy: how to pick best candidate (closest / most open / open-then-close)
    // openRadius: neighborhood radius used to estimate "openness" around a candidate
    // openWeight: weight of openness term (only used for OPEN_THEN_CLOSE)
    // distWeight: weight of distance term (only used for OPEN_THEN_CLOSE)

    /**
     * Same as getPlacementAroundFootprintScored(), but tries depth...depth+maxExtraDepth
     * and returns the best candidate found.
     *
     * Returns null if none found.
     */
    public static Vector2Int getPlacementAroundFootprintScoredWithFallback(
            TileMap map,
            Vector2Int footprintTopLeft,
            int w, int h,
            int depth,
            int maxExtraDepth,
            Vector2Int seekerCell,
            PlacementPolicy policy,
            int openRadius,
            int openWeight,
            int distWeight
    ) {
        if (depth < 0) depth = 0;
        if (maxExtraDepth < 0) maxExtraDepth = 0;

        final Vector2Int target = (seekerCell != null)
                ? seekerCell
                : footprintCenterCell(footprintTopLeft, w, h);

        Vector2Int best = null;
        int bestScore = Integer.MIN_VALUE;
        int bestOpen = Integer.MIN_VALUE;
        int bestDist = Integer.MAX_VALUE;

        for (int d = depth; d <= depth + maxExtraDepth; d++) {
            List<Vector2Int> ring = getAllFreePlacementsOnRing(map, footprintTopLeft, w, h, d);
            for (Vector2Int c : ring) {
                int open = freeCountInSquare(map, c, openRadius);
                int dist = manhattan(c, target);

                int score;
                switch (policy) {
                    case CLOSEST:
                        score = -dist;
                        break;
                    case MOST_OPEN:
                        score = open;
                        break;
                    case OPEN_THEN_CLOSE:
                    default:
                        score = openWeight * open - distWeight * dist;
                        break;
                }

                if (score > bestScore
                        || (score == bestScore && open > bestOpen)
                        || (score == bestScore && open == bestOpen && dist < bestDist)) {
                    bestScore = score;
                    bestOpen = open;
                    bestDist = dist;
                    best = c;
                }
            }
        }

        return best;
    }

    // ------------------------------------------------------------
    // Walkability + helpers
    // ------------------------------------------------------------

    /** Matches your movement convention: blocked if intArr[y][x] != 0 */
    public static boolean isWalkable(TileMap map, int x, int y) {
        return x >= 0 && y >= 0 && x < map.column && y < map.row
                && map.intArr[y][x] == 0;
    }

    private static void addIfWalkable(TileMap map, int x, int y, List<Vector2Int> out) {
        if (isWalkable(map, x, y)) out.add(new Vector2Int(x, y));
    }

    private static int manhattan(Vector2Int a, Vector2Int b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static Vector2Int footprintCenterCell(Vector2Int topLeft, int w, int h) {
        // Center biased toward top-left for even sizes; fine for scoring/targeting.
        int cx = topLeft.x + (w - 1) / 2;
        int cy = topLeft.y + (h - 1) / 2;
        return new Vector2Int(cx, cy);
    }

    /**
     * Openness = number of walkable cells in a (2*radius+1)^2 square around center.
     * This is intentionally simple, fast, and stable for rally-point selection.
     */
    private static int freeCountInSquare(TileMap map, Vector2Int center, int radius) {
        if (map == null || center == null) return 0;
        if (radius < 0) radius = 0;

        int count = 0;
        int minX = center.x - radius;
        int maxX = center.x + radius;
        int minY = center.y - radius;
        int maxY = center.y + radius;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isWalkable(map, x, y)) count++;
            }
        }
        return count;
    }
}
