package GameObjects;

import java.awt.Color;
import java.awt.Graphics;

import Util.Camera;

public class Tile extends Entity {

	Color color;
	
	public Tile(float x, float y, int width, int height, Color color) {
		super(x, y, width, height);
		this.color = color;
	}
	
    @Override
    public void draw(Graphics g, Camera camera) {
        g.setColor(color);
        if (camera.captures(this)) {
			g.fillRect( (int)((x - camera.getX()) * camera.scaleX),
						(int)((y - camera.getY()) * camera.scaleY),
						(int)(width * camera.scaleX),
						(int)(height * camera.scaleY) );
		}
    }

}
