package GameObjects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Manager.EnemyUnitManager;
import Manager.PlayerUnitManager;
import Util.Camera;

public class Projectile extends Entity{
    String tag = "";
    int ID;
    
    // speed in pixels per update 
    private float speed = 2.2f;  
    
    public boolean collided = false;
    
    public Projectile(float x, float y, int width, int height) {
		super(x, y, width, height);
		initHitbox(x, y, width, height);
	}
    
    private void initHitbox(float x, float y, int width, int height) {
		hitbox = new Rectangle2D.Float(x,  y, 0.8f*width, 0.8f*height);
	}

	@Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !collided) {	 
        	
	        g.setColor(Color.GRAY);
			g.fillOval( (int)((x - camera.getX()) * camera.scaleX),
						(int)((y - camera.getY()) * camera.scaleY),
						(int)(width * camera.scaleX),
						(int)(height * camera.scaleY) );
			
        	/*
			g.setColor(Color.RED);
			g.drawRect( (int)((hitbox.x - camera.getX()) * camera.scaleX),
					(int)((hitbox.y - camera.getY()) * camera.scaleY),
					(int)(hitbox.width * camera.scaleX),
					(int)(hitbox.height * camera.scaleY) );
			*/
        }
    }
    
    public void update() {
		checkCollisions();
		if (!collided) {
			moveX();
			moveY();
			hitbox.x = x;
			hitbox.y = y;
		}
	}
    
    public void checkCollisions() {
		switch (tag) {
			case "player_projectile":
				for (int i = 0; i < EnemyUnitManager.unitList.size(); i++) {
					if (hitbox.intersects(EnemyUnitManager.unitList.get(i).hitbox)) {
						//System.out.println("COLLIDE");
						EnemyUnitManager.unitList.get(i).removeHealth(10);
						collided = true;
						vel_x = 0;
						vel_y = 0;
					}
				}
				return;
			case "enemy_projectile":
				for (int i = 0; i < PlayerUnitManager.unitList.size(); i++) {
					if (hitbox.intersects(PlayerUnitManager.unitList.get(i).hitbox)) {
						//System.out.println("COLLIDE");
						PlayerUnitManager.unitList.get(i).removeHealth(10);
						collided = true;
						vel_x = 0;
						vel_y = 0;
					}
				}
				return;
		}
	}
    
    public void setVelocity(float enemyX, float enemyY) {
		
        // Calculate the difference
        float dx = (enemyX - this.x);
        float dy = (enemyY - this.y);
		
        // Calculate the angle to the target
        float angle = (float) Math.atan2(dy, dx);

        // From the angle, derive the velocity components:
        vel_x = (float) (Math.cos(angle) * speed);
        vel_y = (float) (Math.sin(angle) * speed);
	}
    
    public void setTag(String tag) {
		this.tag = tag;
	}
}
