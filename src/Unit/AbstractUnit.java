package Unit;
import Faction.Faction;
import GameObjects.Entity;
import GameObjects.ITargetable;
import GameObjects.Projectile;
import Util.Camera;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUnit extends Entity implements IControllable, ITargetable {

	protected Faction ownerFaction;

	public boolean selected;
	List<Projectile> projectileList = new ArrayList<>();
	int shootingTimer = 0;

	Ellipse2D.Float visionbox;
	
    Rectangle2D.Float healthBar;
    float maxHealth = 100;
    public float currentHealth = maxHealth;
	public int repairTimer = 0;
	public int gatherTimer = 0;
	public int deliverTimer = 0;

	public AbstractUnit(float x, float y, int width, int height) {
		super(x, y, width, height);
		initHitbox(x, y, width, height);
	}

	private void initHitbox(float x, float y, int width, int height) {
		// Body hitbox
		hitbox = new Rectangle2D.Float(x,  y, 0.9f*width, 0.9f*height);
		visionbox = new Ellipse2D.Float(x-(8.5f*width),  y-(8.5f*height), 18f*width, 18f*height);
		
		// init hp bar here because… why not …
		healthBar = new Rectangle2D.Float(x-(width/2),  y-1.2f*(height/2), 2f*width, 0.3f*height);
	}

	// update method
	public void update() { }
	
	public void updateProjectileList() {
		if (projectileList.size() != 0) {
			for (int i = 0; i < projectileList.size(); i++) {
				projectileList.get(i).update();
				if (projectileList.get(i).collided) {
					projectileList.remove(i);
				}
			}
		}
	}

	public void moveTo(float newX, float newY, Camera camera ){ };

	public void updateMoveToLocation(){ };

	public void endPathEarly(){ };

	public void updateUnitSensing(){ };

	public void updateSensing(){ };

	public float calculateDistance(float target_x, float target_y) {
		float dx = target_x - this.x;
		float dy = target_y - this.y;
		float distance = (float) Math.sqrt(dx*dx + dy*dy);
		return distance;
	}
	
    public void removeHealth(int damage) {
    	this.currentHealth -= damage;
    	healthBar.width = (float)(currentHealth/maxHealth)*2f*width;
	}

	public void addHealth(int buff) {
		this.currentHealth += buff;
		healthBar.width = (float)(currentHealth/maxHealth)*2f*width;
	}
    
    public boolean isDestroyed() {
		return currentHealth <= 0;
	}
    
    // Getters/Setters 
    // --------------------------------------------

    public void setMaxHealth(float maxHealth) {
		this.maxHealth = maxHealth;
	}
    
    public float getMaxHealth() {
    	return maxHealth;
    }
    
    public float getCurrentHealth() {
    	return currentHealth;
    }
    
    public void syncHitbox() {
    	hitbox.x = x;
    	hitbox.y = y;
    	visionbox.x = x-(8.5f*width);
    	visionbox.y = y-(8.5f*height);
    	healthBar.x = x-(width/2);
    	healthBar.y = y-1.2f*(height/2);
    }

	public Faction getOwnerFaction() {
		return ownerFaction;
	}
    
}
