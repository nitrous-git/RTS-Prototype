package Util;

public class Vector2Int {
    public int x;
    public int y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public String toString() {
		return "("+this.x+", "+this.y+")";
	}
}