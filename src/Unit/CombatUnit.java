package Unit;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import Building.AbstractBuilding;
import Command.CommandContext;
import Command.CommandType;
import Faction.Faction;
import GameObjects.ITargetable;
import GameObjects.Projectile;
import Panel.GamePanel;
import Pathfind.Pathfinder;
import Unit.AIComponent.CombatUnitAIComponent;
import Unit.AIComponent.CombatUnitRole;
import Unit.StatePackage.*;
import Util.*;

public class CombatUnit extends AbstractUnit {

    protected CommandType currentCommand = CommandType.IDLE;
    protected CommandContext ctx;

	public static final int TOKEN = 9;
    private boolean isMoving;
    private float speed = 1.8f;  
    
    private ITargetable currentTarget;

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

    // CombatUnitAIComponent Section
    private CombatUnitRole role = CombatUnitRole.ATTACKER;
    private CombatUnitAIComponent aiComponent;
    private ITargetable preferredTarget;

	// Constructor
    public CombatUnit(TileMap map, Faction ownerFaction, float x, float y, int width, int height) {
    	super(x, y, width, height);
    	this.selected = false;
    	this.isMoving = false;
    	
    	// boost player unit health 
    	setMaxHealth(300.0f);
    	currentHealth = 300f;
        healthBar.width = (float)(currentHealth/maxHealth)*2f*width;

        this.ownerFaction = ownerFaction;
    	this.map = map;
    	pf = new Pathfinder();

        preferredTarget = null;
        // Instantiate CUAIComponent if necessary
        if (ownerFaction != null && ownerFaction.isAI()) {
            aiComponent = new CombatUnitAIComponent(this);
        }

    	this.currentNode = GamePanel.convertWorldToCell(x, y);
        this.currentState = new IdleState<CombatUnit>();
        issueCommand(CommandType.IDLE, null);
    }

    ///  ------------------------------

    @Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !isDestroyed()) {
        	
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
			g.drawOval( (int)((visionBox.x - camera.getX()) * camera.scaleX),
						(int)((visionBox.y - camera.getY()) * camera.scaleY),
						(int)(visionBox.width * camera.scaleX),
						(int)(visionBox.height * camera.scaleY) );
			
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

        if (aiComponent != null) {
            aiComponent.update();
        }
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
    public void moveTo(float newX, float newY, Camera camera) {
    	
    	this.start = GamePanel.convertWorldToCell(this.x, this.y);
    	this.end = GamePanel.convertWorldToCell(newX, newY);
    	
    	//System.out.println("Start : " + start.toString());
    	//System.out.println("End : " + end.toString());
    	if (ownerFaction.getUnitManager().getGC().constructSelectedUnitList().size() > 1) {
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
     * ITargetable Sensing
     */

    @Override
    public void updateSensing() {
        // if the AI has a preferred target, honor it
        if (preferredTarget != null && !preferredTarget.isDestroyed()
            && preferredTarget.getOwnerFaction() != ownerFaction && visionBox.intersects(preferredTarget.getHitbox())) {
            currentTarget = preferredTarget;
            issueCommand(CommandType.ATTACK, null);
            return;
        }

        // fall back to the existing generic scan otherwise
        currentTarget = null;
        clearPreferredTarget();
        float minDistance = Float.MAX_VALUE;

        for (ITargetable target : ownerFaction.getUnitManager().getGC().getAllTargetables()) {
            if (target == this) continue;
            if (target.getOwnerFaction() == ownerFaction) continue;
            if (target.isDestroyed()) continue;
            if (!visionBox.intersects(target.getHitbox())){
                continue;
            }

            float distance = calculateDistance(target.getX(), target.getY());
            if (distance < minDistance) {
                minDistance = distance;
                currentTarget = target;
            }
        }

        if (currentTarget != null) {
            System.out.println("Current target is : "+ currentTarget);
            issueCommand(CommandType.ATTACK, null);
        }
    }

    public void automateShooting() {
        if (currentTarget != null) {
            shootingTimer++;
            if (shootingTimer % 25 == 0) {
                Projectile p = new Projectile(getFilteredAnyOtherITargetableList(), this.x, this.y, 7, 7);

                float targetX = currentTarget.getX();
                float targetY = currentTarget.getY();
                // Target the center if currentTarget is AbstractBuilding
                if (currentTarget instanceof AbstractBuilding building) {
                    targetX = building.getX() + building.getWidth() * 0.5f;
                    targetY = building.getY() + building.getHeight() * 0.5f;
                }
                p.setVelocity(targetX, targetY);
                p.setTag("player_projectile");
                projectileList.add(p);
            }
        }
    }

    public void checkForNewTarget() {
        // Invalidate current target if it is gone or out of vision
        if (currentTarget != null) {
            if (currentTarget.isDestroyed() || !visionBox.intersects(currentTarget.getHitbox())) {
                currentTarget = null;
            }
        }

        // Try to reacquire something in vision
        if (currentTarget == null) {
            updateSensing();

            // Still nothing -> leave attack state
            if (currentTarget == null && currentCommand == CommandType.ATTACK) {
                issueCommand(CommandType.IDLE, null);
            }
        }
    }

    private List<ITargetable> getFilteredAnyOtherITargetableList(){
        return ownerFaction.getUnitManager().getGC().getAllTargetables()
                .stream()
                .filter(u -> !u.getOwnerFaction().getName().equals(ownerFaction.getName()))
                .collect(Collectors.toList());
    }

    // ---------------------------------------------
    // CombatUnitAIComponent helper methods

    public CombatUnitAIComponent getAiComponent() {
        return aiComponent;
    }

    public CombatUnitRole getRole() {
        return role;
    }

    public void setRole(CombatUnitRole role) {
        this.role = role;
    }

    public void setPreferredTarget(ITargetable preferredTarget) {
        this.preferredTarget = preferredTarget;
    }

    public void clearPreferredTarget() {
        this.preferredTarget = null;
    }

    public ITargetable getPreferredTarget() {
        return preferredTarget;
    }

    // -----------------------------------
    // Getter & Setter
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public IUnitState<CombatUnit> getCurrentState() {
        return currentState;
    }

    public CommandType getCurrentCommand() {
        return currentCommand;
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
