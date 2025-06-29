package Pathfind;

public class PathNode {
    public int x;
    public int y;
    public boolean isWalkable;

    public int gCost;              // Distance from start
    public int hCost;              // Distance (heuristic) to end
    public int fCost;              // gCost + hCost
    public PathNode cameFromNode;  // For path reconstruction

    public PathNode(int x, int y, boolean isWalkable) {
        this.x = x;
        this.y = y;
        this.isWalkable = isWalkable;
        this.gCost = Integer.MAX_VALUE;
        this.hCost = 0;
        this.fCost = Integer.MAX_VALUE;
        this.cameFromNode = null;
    }

    public void calculateFCost() {
        fCost = gCost + hCost;
    }
}