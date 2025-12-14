package Unit;
import java.awt.Color;
import java.awt.Graphics;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Command.CommandContext;
import Command.CommandType;
import Faction.Faction;
import GameObjects.Projectile;
import Panel.GamePanel;
import Manager.EnemyUnitManager;
import Manager.PlayerUnitManager;
import Pathfind.Pathfinder;
import Unit.StatePackage.*;
import Util.*;

public class CombatUnit extends AbstractUnit implements IControllable {

    protected CommandType currentCommand = CommandType.IDLE;
    protected CommandContext ctx;

	public static final int TOKEN = 9;
    private boolean isMoving;
    private float speed = 1.8f;  
    
	AbstractUnit targetEnemyUnit;
	
	//String[] states =  new String[] { "IDL", "MVG", "ATK" };
	//String currentState = states[0];
    private IUnitState<CombatUnit> currentState;
	
	Pathfinder pf;
	TileMap map; // Reference to the game map
	List<Vector2Int> path; // Path to follow
	Vector2Int currentNode; // Current grid cell
	Vector2Int nextNode; // Next grid cell to move toward
	int currentIndex; // Current step in the path
	
	Vector2Int start, end; // Start and destination cells
	
    private long blockStartTime = 0; // Timer for handling blocked cells
    private boolean isWaiting = false; // Indicates if the unit is waiting for a block to clear
    Random random = new Random();

	// Constructor
    public CombatUnit(TileMap map, Faction ownerFaction, float x, float y, int width, int height) {
    	super(x, y, width, height);
    	this.selected = false;
    	this.isMoving = false;
    	
    	// boost player unit health 
    	setMaxHealth(300.0f);
    	currentHealth = 100f;
        healthBar.width = (float)(currentHealth/maxHealth)*2f*width;

        this.ownerFaction = ownerFaction;
    	this.map = map;
    	pf = new Pathfinder();
    	
    	this.currentNode = GamePanel.convertWorldToCell(x, y);
        this.currentState = new IdleState<CombatUnit>();
        issueCommand(CommandType.IDLE, null);
    }

    ///  ------------------------------

    @Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !isDead()) {
        	
		    // draw a highlight if selected
	        if (selected) {
	            // draw a border around the oval
	            g.setColor(GameColors.UNIT_HIGHLIGHT);
	            g.drawOval((int)(((x - 2) - camera.getX()) * camera.scaleX), 
	            		(int)(((y - 2) - camera.getY()) * camera.scaleY), 
	            		(int)((width + 4) * camera.scaleX), 
	            		(int)((height + 4) * camera.scaleY));
	        }
	        
	        g.setColor(GameColors.UNIT_PLAYER_COMBAT);
			g.fillOval( (int)((x - camera.getX()) * camera.scaleX),
						(int)((y - camera.getY()) * camera.scaleY),
						(int)(width * camera.scaleX),
						(int)(height * camera.scaleY) );
			
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

    // issueCommand one time on call
    @Override
    public void issueCommand(CommandType command, CommandContext ctx) {
        this.currentCommand = command;
        this.ctx = ctx;

        switch (currentCommand) {
            case IDLE -> setState(new IdleState<CombatUnit>());
            case MOVE -> setState(new MoveState<CombatUnit>(ctx.getX(), ctx.getY(), ctx.getCamera()));
            case HOLD_POSITION -> endPathEarly();
            case ATTACK -> setState(new AttackState());
        }
    }

    // update command on every ticks
    public void update() {
        updateProjectileList();
        currentState.update(this);
    }

    public void setState(IUnitState<CombatUnit> newState) {
        if (currentState != null) currentState.onExit(this);
        currentState = newState;
        currentState.onEnter(this);
    }

    ///  ------------------------------

    /**
     * Movement section
     * Same code as CombatUnit handle basic movement
     */
    @Override
    public void updateMoveToLocation() {
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
                issueCommand(CommandType.IDLE, null);
                return;
            }
        	
            // Next node to move toward
            nextNode = path.get(currentIndex);
            Vector2 targetPos = GamePanel.convertCellToWorld(nextNode.x, nextNode.y);
            
            // Check if the next cell is blocked
            /*
            if (isBlocked(nextNode.x, nextNode.y) && currentIndex > 0) {
               handleBlockedCell(nextNode);
               return;
            }
            */

            Vector2Int blocked = getFirstBlockedNodeAhead(LOOKAHEAD);
            if (blocked != null) {
                handleBlockedCell(blocked);
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

    @Override
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
            Logger.log("Destination is blocked.");
            System.out.println("Destination is blocked.");
            //end = getRandomNearbyPoint(end, 3); // Try within a range of 2
            return;
        }
    	
    	path = pf.FindPath(map.intArr, start, end);
        //map.highlightPath(path);
        //map.printer();
        
        if (path == null || path.isEmpty()) {
            Logger.log("No path found.");
            System.out.println("No path found.");
            return;
        }
    	
        currentIndex = 0;
        isMoving = true; // is moving order is ongoing
    }

    // Handles when the next cell is blocked
    /*
    private void handleBlockedCell(Vector2Int blockedNode) {
        if (!isWaiting) {
            isWaiting = true;
            blockStartTime = System.currentTimeMillis();
        } else {
            long elapsed = System.currentTimeMillis() - blockStartTime;
            if (elapsed > 1000) { // 500 ms threshold

                // Recalculate path
                start = GamePanel.convertWorldToCell(this.x, this.y);
                path = pf.FindPath(map.intArr, start, end);

                if (path == null || path.isEmpty()) {
                    stopMovement();
                    // the path might be blocked indefinitely
                    // go back to IDL state
                    issueCommand(CommandType.IDLE, null);
                    return;
                }

                // Reset waiting state
                isWaiting = false;
                blockStartTime = 0;
                currentIndex = 0;
            }
        }
    }
    */

    private static final int LOOKAHEAD = 5;

    // Returns the first blocked node ahead, or null if all clear
    private Vector2Int getFirstBlockedNodeAhead(int lookahead) {
        if (path == null || path.isEmpty()) return null;

        int from = Math.max(1, currentIndex); // often path[0] is the start/current cell
        int to   = Math.min(path.size() - 1, currentIndex + lookahead);

        for (int i = from; i <= to; i++) {
            Vector2Int n = path.get(i);
            if (isBlocked(n.x, n.y)) return n;
        }
        return null;
    }

    private void handleBlockedCell(Vector2Int blockedNode) {

        if (!isBlocked(blockedNode.x, blockedNode.y)) {
            isWaiting = false;
            blockStartTime = 0;
            return;
        }

        if (!isWaiting) {
            isWaiting = true;
            blockStartTime = System.currentTimeMillis();
            return;
        }

        long elapsed = System.currentTimeMillis() - blockStartTime;

        if (elapsed > 1000) { // 1000 ms threshold

            // Recalculate path
            start = GamePanel.convertWorldToCell(this.x, this.y);
            path = pf.FindPath(map.intArr, start, end);

            if (path == null || path.isEmpty()) {
                stopMovement();
                issueCommand(CommandType.IDLE, null);
                return;
            }

            // Reset waiting state
            isWaiting = false;
            blockStartTime = 0;
            currentIndex = 0;
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

    // Stops movement and resets relevant states
    private void stopMovement() {
        isMoving = false;
        vel_x = 0;
        vel_y = 0;
        currentIndex = 0;

        path = null;
    }

    // End the path at next step
    @Override
    public void endPathEarly(){
        // if there's no “next” step, just bail out and stop completely
        if (path == null || currentIndex >= path.size() - 1) {
            stopMovement();
            return;
        }

        // build a new path list containing only that one step
        List<Vector2Int> newPath = new ArrayList<>();
        newPath.add(path.get(currentIndex));
        newPath.add(path.get(currentIndex + 1));
        path = newPath;

        // reset movement command and finish path
        currentIndex = 0;
        vel_x = 0;
        vel_y = 0;
        isMoving = true;
        currentCommand = CommandType.MOVE;
    }

    /**
     * UnitSensing And Shooting
     */
    @Override
	public void updateUnitSensing() {
		List<AbstractUnit> eul = EnemyUnitManager.unitList;
        if (eul == null || eul.isEmpty()) return;

		targetEnemyUnit = null;
        float max = (float) Double.MAX_VALUE;

		// find the closest enemy
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
			//currentState = states[2]; // to ATK state
            issueCommand(CommandType.ATTACK, null);
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



    // -----------------------------------
    // Getter & Setter
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    // -----------------------------------
    // pretty printing 
    @Override
    public String toString() {
    	return "tag : " + tag +" "+ ID + " currentState : " + currentState + " IsSelected : " + selected;
	}
}
