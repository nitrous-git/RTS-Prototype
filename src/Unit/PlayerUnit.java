package Unit;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import GameObjects.Projectile;
import Panel.GamePanel;
import Manager.EnemyUnitManager;
import Manager.PlayerUnitManager;
import Pathfind.Pathfinder;
import Util.Camera;
import Util.TileMap;
import Util.Vector2;
import Util.Vector2Int;

public class PlayerUnit extends AbstractUnit {
	
	// track selection status
	private boolean selected;  
	
    // The destination we want to move to

    private boolean isMoving;
    
    // speed in pixels per update 
    private float speed = 1.8f;  
    
	EnemyUnit targetEnemyUnit;
	
	String[] states =  new String[] { "IDL", "MVG", "ATK" }; // regular mode, attack mode
	String currentState = states[0];
	
	Pathfinder pf;
	TileMap map; // Reference to the game map
	List<Vector2Int> path; // Path to follow
	Vector2Int currentNode; // Current grid cell
	Vector2Int nextNode; // Next grid cell to move toward
	int currentIndex; // Current step in the path
	
	Vector2Int start, end; // Start and destination cells
	
    private long blockStartTime = 0; // Timer for handling blocked cells
    private boolean isWaiting = false; // Indicates if the unit is waiting for a block to clear
	
    // Simple enum for 4-direction facing
    private enum Direction
    {
        Up,
        Down,
        Left,
        Right
    }

    private Direction lastDirection = Direction.Down;

	// Constructor
    public PlayerUnit(TileMap map, float x, float y, int width, int height) {
    	super(x, y, width, height);
    	this.selected = false;
    	this.isMoving = false;
    	
    	// boost player unit health 
    	setMaxHealth(300.0f);
    	currentHealth = maxHealth;
    	
    	this.map = map;
    	pf = new Pathfinder();
    	
    	this.currentNode = GamePanel.convertWorldToCell(x, y);
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !isDead()) {
        	
		    // draw a highlight if selected
	        if (selected) {
	            // draw a border around the oval
	            g.setColor(Color.CYAN);
	            g.drawOval((int)(((x - 2) - camera.getX()) * camera.scaleX), 
	            		(int)(((y - 2) - camera.getY()) * camera.scaleY), 
	            		(int)((width + 4) * camera.scaleX), 
	            		(int)((height + 4) * camera.scaleY));
	        }
	        
	        g.setColor(Color.BLUE);
			g.fillOval( (int)((x - camera.getX()) * camera.scaleX),
						(int)((y - camera.getY()) * camera.scaleY),
						(int)(width * camera.scaleX),
						(int)(height * camera.scaleY) );
			/*
			g.setColor(Color.red);
			g.drawRect( (int)((hitbox.x - camera.getX()) * camera.scaleX),
						(int)((hitbox.y - camera.getY()) * camera.scaleY),
						(int)(hitbox.width * camera.scaleX),
						(int)(hitbox.height * camera.scaleY) );
			*/
			
			g.setColor(Color.GRAY);
			g.drawOval( (int)((visionbox.x - camera.getX()) * camera.scaleX),
						(int)((visionbox.y - camera.getY()) * camera.scaleY),
						(int)(visionbox.width * camera.scaleX),
						(int)(visionbox.height * camera.scaleY) );
			
			g.fillRect( (int)((healthBar.x - camera.getX()) * camera.scaleX),
					(int)((healthBar.y - camera.getY()) * camera.scaleY),
					(int)(healthBar.width * camera.scaleX),
					(int)(healthBar.height * camera.scaleY) );
			
			
			for (int i = 0; i < projectileList.size(); i++) {
				projectileList.get(i).draw(g, camera);
			}
			
		}
    }
    
    // update position
    public void update() {
    	updateProjectileList();
    	
    	// update states
    	switch (currentState) {
			case "IDL": {
		    	updateUnitSensing();
				return;
			}
			case "MVG":{
				updateMovement();
				// updateUnitSensing();
				// unit sensing to skip to atk 
				return;
			}
			case "ATK": {
				automateShooting();
				checkForNewTarget();
				return;
			}
    	}
    }
    
    public void updateMovement() {
        if (isMoving) {
        	
            // done with path
            if (path == null || currentIndex >= path.size())
            {
                isMoving = false;
                vel_x = 0;
                vel_y = 0;
                currentIndex = 0;
                
                path = null;
                // go back to IDL state
                currentState = states[0];
                return;
            }
        	
            // Next node to move toward
            nextNode = path.get(currentIndex);
            Vector2 targetPos = GamePanel.convertCellToWorld(nextNode.x, nextNode.y);
            
            // Check if the next cell is blocked
            if (isBlocked(nextNode.x, nextNode.y) && currentIndex > 0) {
               handleBlockedCell(nextNode);
               return;
            }
            
            float distance = calculateDistance(targetPos.x, targetPos.y);
            
            if (distance <= 2.0f)
            {
                // Snap to the target position
                snapToTarget(targetPos);

                currentIndex++;
                // Save current node as the old nextNode
                currentNode = nextNode;
                
                // Update last position in intArr
                map.intArr[currentNode.y][currentNode.x] = 9;
            }
            else
            {
                // Calculate the difference
                float dx = (targetPos.x - this.x);
                float dy = (targetPos.y - this.y);

                // Calculate the angle to the target
                float angle = (float) Math.atan2(dy, dx);

                // From the angle, derive the velocity components
                vel_x = (float) (Math.cos(angle) * speed);
                vel_y = (float) (Math.sin(angle) * speed);
                
            	moveX();
            	moveY();
            	// update hitbox
            	syncHitbox();
            }
        }
	}
    
    public void removeOldPositions(int lastIndex) {
    	for (int i = 0; i < lastIndex; i++) {
    		map.intArr[path.get(i).y][path.get(i).x] = 0;
		}
	}
    
    
    // Set a new target and compute velocity so we move toward it.
    public void moveTo(float newX, float newY, Camera camera ) {
    	
    	this.start = GamePanel.convertWorldToCell(this.x, this.y);
    	this.end = GamePanel.convertWorldToCell(newX, newY);
    	
    	//System.out.println("Start : " + start.toString());
    	//System.out.println("End : " + end.toString());
    	if (PlayerUnitManager.getSelectedUnitList().size() > 1) {
    		end = getRandomNearbyPoint(end, 3);
		}
    	//System.out.println("End : " + end.toString());
    	
        // Validate destination
        if (map.intArr[end.y][end.x] == 1) {
            System.out.println("Destination is blocked.");
            //end = getRandomNearbyPoint(end, 3); // Try within a range of 2
            return;
        }
    	
    	path = pf.FindPath(map.intArr, start, end);
        //map.highlightPath(path);
        //map.printer();
        
        if (path == null || path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }
    	
        currentIndex = 0;
        isMoving = true; // is moving order is ongoing
        currentState = states[1];
    }
    
    
    // Handles when the next cell is blocked
    private void handleBlockedCell(Vector2Int blockedNode) {
        if (!isWaiting) {
            isWaiting = true;
            blockStartTime = System.currentTimeMillis();
        } else {
            long elapsed = System.currentTimeMillis() - blockStartTime;
            if (elapsed > 500) { // 500 ms threshold
            	
                // Randomize endpoint if blocked
                //end = getRandomNearbyPoint(end, 1); // Adjust end point within a range of 1

            	
                // Recalculate path
                start = GamePanel.convertWorldToCell(this.x, this.y);
                path = pf.FindPath(map.intArr, start, end);
                
                if (path == null || path.isEmpty()) {
                    stopMovement();
                    // the path might be blocked indefinetely
                    // go back to IDL state
                    currentState = states[0];
                    return;
                } 
                
                // Reset waiting state
                isWaiting = false;
                blockStartTime = 0;
                currentIndex = 0;
            }
        }
    }
    
    // Checks if a cell is blocked
    private boolean isBlocked(int x, int y) {
        // Check if the cell is occupied but not the current cell
        return map.intArr[y][x] != 0;
    }
    
    // Snap to the target position
    private void snapToTarget(Vector2 targetPos) {
        this.x = targetPos.x;
        this.y = targetPos.y;

        // Mark the previous node as unoccupied
        if (currentNode != null) {
           map.intArr[currentNode.y][currentNode.x] = 0;
        }
    }
    
    // -------------------------------
    private Vector2Int getRandomNearbyPoint(Vector2Int original, int range) {
        int newX, newY;
        Vector2Int randomPoint;

        do {
            newX = original.x + (int) (Math.random() * (2 * range + 1)) - range;
            newY = original.y + (int) (Math.random() * (2 * range + 1)) - range;
            randomPoint = new Vector2Int(newX, newY);
        } while (!isValidPoint(randomPoint));

        return randomPoint;
    }

    private boolean isValidPoint(Vector2Int point) {
        return point.x >= 0 && point.x < map.intArr[0].length &&
               point.y >= 0 && point.y < map.intArr.length &&
               map.intArr[point.y][point.x] == 0; // Check if walkable
    }
    // -------------------------------------

    // Stops movement and resets relevant states
    private void stopMovement() {
        isMoving = false;
        vel_x = 0;
        vel_y = 0;
        currentIndex = 0;

        path = null;
    }
    

    
	public void updateUnitSensing() {
		List<EnemyUnit> eul = EnemyUnitManager.unitList;
		float max = (float) Double.MAX_VALUE;
		
		targetEnemyUnit = null;
		
		// find closest enemy
		for (int i = 0; i < eul.size(); i++) {
			if ( visionbox.intersects(eul.get(i).getHitbox()) ) {
				float distance = calculateDistance(eul.get(i).getX(), eul.get(i).getY());
				
				if (distance < max) {
					// set closest to target enemy
					targetEnemyUnit = eul.get(i);
					max = distance;
				}

			}
		} 
		
		// check for automate shoot
		if (targetEnemyUnit != null) {
			currentState = states[2]; // to ATK state
			//System.out.println(targetEnemyUnit.toString());
		}
	}

	public void automateShooting() {
		if (targetEnemyUnit != null) {
			shootingTimer++;
			if (shootingTimer%25==0) {
				//System.out.println("SHOOT");
				Projectile p = new Projectile(this.x, this.y, 8, 8);
				p.setVelocity(targetEnemyUnit.x, targetEnemyUnit.y);
				p.setTag("player_projectile");
				projectileList.add(p);
			}
		}
	}
	
	public void checkForNewTarget() {
		if (targetEnemyUnit == null || targetEnemyUnit.isDead()) {
			updateUnitSensing();
		}
	}
	
	
    private Direction DetermineDirection(Vector2Int fromTile, Vector2Int toTile)
    {
        int dx = toTile.x - fromTile.x;
        int dy = toTile.y - fromTile.y;

        // If dx == 1 => we moved to the right tile
        if (dx == 1 && dy == 0)
        {
            return Direction.Up;
        }
        else if (dx == -1 && dy == 0)
        {
            return Direction.Down;
        }
        else if (dx == 0 && dy == 1)
        {
            return Direction.Left;
        }
        else if (dx == 0 && dy == -1)
        {
            return Direction.Right;
        }

        // default to Down
        return Direction.Down;
    }
	
	
    // Getters/Setters 
    // --------------------------------------------
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
    
    // pretty printing 
    @Override
    public String toString() {
    	return "tag : " + tag +" "+ ID + " currentState : " + currentState + " IsSelected : " + selected;
	}
}
