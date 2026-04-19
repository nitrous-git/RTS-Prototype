package GameObjects;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import Util.Camera;
/**
 * implement this interface in Entity class
 */
public interface IEntity {
    public void draw(Graphics g, Camera camera);
    public void setX(float x);
	public void setY(float y);
	public void setWidth(int width);
	public void setHeight(int height);
	public float getX();
	public float getY();
	public int getWidth();
	public int getHeight();
	public boolean intersects(Entity other);
    public float getXVelocity();
	public float getYVelocity();
	public void setXVelocity(float vel_x);
	public void setYVelocity(float vel_y);
	public void moveX();
    public void moveY();
	public String getTag();
	public int getID();
	public void setTag(String tag);
	public void setID(int ID);
	public Rectangle2D.Float getHitbox();
}
