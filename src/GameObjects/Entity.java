package GameObjects;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import Util.Camera;

/**
 * Parent class for any game object
 */
public class Entity implements IEntity {

    public float x;
    public float y;
    protected int width;
    protected int height;
    public float vel_x;
    public float vel_y;
    public String tag = "";
    public int ID;
    // unit collision box
    public Rectangle2D.Float hitbox;

    // ------- constructor -------- //
    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        hitbox = new Rectangle2D.Float(x-3,  y-4, 1.4f*width, 1.4f*height);
    }

    // ------ method ------- //
    @Override
    public void draw(Graphics g, Camera camera) {
    } // override in child class

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean intersects(Entity other) {
        //hitbox.setBounds((int) x, (int) y, (int) width, (int) height);
        return hitbox.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
    }

    @Override
    public float getXVelocity() {
        return vel_x;
    }

    @Override
    public float getYVelocity() {
        return vel_y;
    }

    @Override
    public void setXVelocity(float vel_x) {
        this.vel_x = vel_x;
    }

    @Override
    public void setYVelocity(float vel_y) {
        this.vel_y = vel_y;
    }

    @Override
    public void moveX() {
        this.x += vel_x;
    }

    @Override
    public void moveY() {
        this.y += vel_y;
    }

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public void setID(int ID) {
		this.ID = ID;
	}



}
