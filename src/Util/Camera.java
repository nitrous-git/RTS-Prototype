package Util;

import GameObjects.Entity;
import Panel.GamePanel;

public class Camera {
	
	private float x, y, width, height;
	private float vel_x, vel_y;
	public float scaleX, scaleY;
	public boolean up, down, left, right;
	
	public Camera(float x, float y, float width, float height, GamePanel window) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		scaleX = (window.WIDTH * GamePanel.SCALE)/ width;
		scaleY = (window.HEIGHT * GamePanel.SCALE) / height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setXVelocity(float vel_x) {
		this.vel_x = vel_x;
	}

	public void setYVelocity(float vel_y) {
		this.vel_y = vel_y;
	}

	public void setScale(float width, float height, GamePanel window) {
		scaleX = window.WIDTH / width;
		scaleY = window.HEIGHT / height;
	}

	public void moveX(){
        x += vel_x;
    }

	public void moveY(){
        y += vel_y;
    }

	public boolean captures(Entity object) {
		return 	object.getX() <= (x + width/scaleX) && (object.getX() + object.getWidth()) >= x 
                && object.getY() <= (y + height/scaleY) && (object.getY() + object.getHeight()) >= y;
	}
	
	public void update() {
		if (down && getY() < (GamePanel.ROWS * GamePanel.TILE_SIZE)-(GamePanel.HEIGHT-3.5f*GamePanel.TILE_SIZE)) {
			setYVelocity(4.5f);
			moveY();
		}
		if (up && getY() > 0) {
			setYVelocity(-4.5f);
			moveY();
		}
		if (left && getX() > 0) {
			setXVelocity(-4.5f);
			moveX();
		}
		if (right && getX() < (GamePanel.COLS * GamePanel.TILE_SIZE)-(GamePanel.WIDTH-3.5f*GamePanel.TILE_SIZE)) {
			setXVelocity(4.5f);
			moveX();
		}
	}
	
	// Getter & Setter
	
	public boolean getUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean getDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public boolean getLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean getRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}
}
