package Util;

public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public String toString() {
		return "("+this.x+", "+this.y+")";
	}
}