package Building;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import GameObjects.Entity;
import Resource.BuildingType;

/**
 * Base class for all buildings in the game.
 */
public class AbstractBuilding extends Entity{
	
    Rectangle2D.Float healthBar;
    float maxHealth = 100;
    float currentHealth = maxHealth;
    protected boolean selected;

    // In construction cycle
    public enum State { PRE_DEPLOYMENT, UNDER_CONSTRUCTION, IN_OPERATION }
    public State currentState;

    public BuildingType TYPE;

    public AbstractBuilding(float x, float y, int width, int height) {
		super(x, y, width, height);
		initHitbox(x, y, width, height);
	}
	
	private void initHitbox(float x, float y, int width, int height) {
		// Body hitbox
		hitbox = new Rectangle2D.Float(x,  y, 0.9f*width, 0.9f*height);

		// init hp bar here because... why not ...
		healthBar = new Rectangle2D.Float(x-(width/2),  y-1.2f*(height/2), 2f*width, 0.3f*height);
	}
	
	
    public void removeHealth(int damage) {
    	this.currentHealth -= damage;
    	healthBar.width = (float)(currentHealth/maxHealth)*2f*width;
	}

    public void syncHitbox() {
        hitbox.x = x;
        hitbox.y = y;
        healthBar.x = x-(width/2);
        healthBar.y = y-1.2f*(height/2);
    }


    /* --- Getter & Setter --- */

    public boolean isDestroyed() {
		return currentHealth <= 0;
	}
	
    public Rectangle.Float getHitbox() {
		return hitbox;
	}
	
    public void setMaxHealth(float maxHealth) {
		this.maxHealth = maxHealth;
	}
    
    public float getMaxHealth() {
    	return maxHealth;
    }
    
    public float getCurrentHealth() {
    	return currentHealth;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isUnderConstruction(){ return currentState == State.UNDER_CONSTRUCTION
                                                || currentState == State.PRE_DEPLOYMENT ; }
}
