package Pathfind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import Util.Vector2Int;

public class Pathfinder {

	// Empty constructor
	public Pathfinder() { }
	
	public List<Vector2Int> FindPath(int[][] map, Vector2Int start, Vector2Int end) {
	    // Correctly assign height and width
	    int height = map.length; // Rows
	    int width = map[0].length; // Columns

	    // Validate start/end
	    if (!isInBounds(start, width, height) || !isInBounds(end, width, height)) {
	        System.out.println("Start or end is out of map bounds!");
	        return null;
	    }
	    if (map[start.y][start.x] == 1 || map[end.y][end.x] == 1) {
	        System.out.println("Start or end is blocked!");
	        return null;
	    }

	    // Create all nodes
	    PathNode[][] nodes = new PathNode[width][height];
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            boolean walkable = (map[y][x] == 0 ); // Correct indexing 
	            nodes[x][y] = new PathNode(x, y, walkable);
	        }
	    }

	    // Initialize nodes
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            PathNode node = nodes[x][y];
	            node.gCost = Integer.MAX_VALUE;
	            node.cameFromNode = null;
	            node.calculateFCost();
	        }
	    }

	    // Setup start/end nodes
	    PathNode startNode = nodes[start.x][start.y];
	    PathNode endNode = nodes[end.x][end.y];

	    // Start node init
	    startNode.gCost = 0;
	    startNode.hCost = calculateDistanceCost(startNode, endNode);
	    startNode.calculateFCost();

	    // Replace openList/closedList:
	    PriorityQueue<PathNode> openHeap = new PriorityQueue<>(Comparator.comparingInt((PathNode n) -> n.fCost).thenComparingInt(n -> n.hCost));
	    HashSet<PathNode> openSet   = new HashSet<>();
	    HashSet<PathNode> closedSet = new HashSet<>();

	    // Initialize start node
	    startNode.gCost = 0;
	    startNode.hCost = calculateDistanceCost(startNode, endNode);
	    startNode.calculateFCost();
	    openHeap.offer(startNode);
	    openSet.add(startNode);

	    while (!openHeap.isEmpty()) {
	        // 1) Get best node
	        PathNode current = openHeap.poll();
	        // Skip stale entries if we’d already closed it
	        if (!openSet.remove(current)) continue;

	        // 2) Found the end?
	        if (current == endNode)
	            return calculatePath(endNode);

	        // 3) Move to closed
	        closedSet.add(current);

	        // 4) Examine neighbors
	        for (PathNode nbr : getNeighbourList(current, nodes)) {
	            if (!nbr.isWalkable || closedSet.contains(nbr))
	                continue;

	            int tentativeG = current.gCost + calculateDistanceCost(current, nbr);
	            if (tentativeG < nbr.gCost) {
	                nbr.cameFromNode = current;
	                nbr.gCost        = tentativeG;
	                nbr.hCost        = calculateDistanceCost(nbr, endNode);
	                nbr.calculateFCost();

	                if (!openSet.contains(nbr)) {
	                    // brand-new or reopened
	                    openHeap.offer(nbr);
	                    openSet.add(nbr);
	                } else {
	                    // cost decreased — re-insert so heap order updates
	                    openHeap.offer(nbr);
	                }
	            }
	        }
	    }

	    return null; // no path
	}


	// -------------------------
	// Utilities
	// -------------------------
	private boolean isInBounds(Vector2Int pos, int width, int height) {
	    return (pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height);
	}

	// Using Manhattan distance here
	private int calculateDistanceCost(PathNode a, PathNode b) {
	    int xDistance = Math.abs(a.x - b.x);
	    int yDistance = Math.abs(a.y - b.y);
	    return xDistance + yDistance;
	}

	private PathNode getLowestFCostNode(List<PathNode> nodeList) {
	    PathNode lowestFCostNode = nodeList.get(0);
	    for (int i = 1; i < nodeList.size(); i++) {
	        if (nodeList.get(i).fCost < lowestFCostNode.fCost) {
	            lowestFCostNode = nodeList.get(i);
	        }
	    }
	    return lowestFCostNode;
	}

	private List<PathNode> getNeighbourList(PathNode currentNode, PathNode[][] nodes) {
	    List<PathNode> neighbours = new ArrayList<>();
	    int width = nodes.length;
	    int height = nodes[0].length;

	    // Up
	    if (currentNode.y + 1 < height) {
	        neighbours.add(nodes[currentNode.x][currentNode.y + 1]);
	    }
	    // Down
	    if (currentNode.y - 1 >= 0) {
	        neighbours.add(nodes[currentNode.x][currentNode.y - 1]);
	    }
	    // Left
	    if (currentNode.x - 1 >= 0) {
	        neighbours.add(nodes[currentNode.x - 1][currentNode.y]);
	    }
	    // Right
	    if (currentNode.x + 1 < width) {
	        neighbours.add(nodes[currentNode.x + 1][currentNode.y]);
	    }

	    return neighbours;
	}

	// Path Reconstruction
	private List<Vector2Int> calculatePath(PathNode endNode) {
	    List<Vector2Int> path = new ArrayList<>();
	    PathNode currentNode = endNode;
	    while (currentNode != null) {
	        path.add(new Vector2Int(currentNode.x, currentNode.y));
	        currentNode = currentNode.cameFromNode;
	    }
	    // Reverse the path to go from start -> end
	    java.util.Collections.reverse(path);
	    return path;
	}

}
