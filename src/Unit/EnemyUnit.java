package Unit;
import java.awt.Color;
import java.awt.Graphics;

import Command.CommandContext;
import Command.CommandType;
import Util.Camera;
import Util.GameColors;

public class EnemyUnit extends AbstractUnit {
    
	AbstractUnit targetPlayerUnit;
	public static final int TOKEN = 8;
	
    public EnemyUnit(float x, float y, int width, int height) {
		super(x, y, width, height);
	}

    @Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !isDestroyed()) {
	        g.setColor(Color.WHITE);
			g.fillOval( (int)((x - camera.getX()) * camera.scaleX),
						(int)((y - camera.getY()) * camera.scaleY),
						(int)(width * camera.scaleX),
						(int)(height * camera.scaleY) );
			
			g.setColor(Color.GRAY);
			g.drawOval( (int)((visionBox.x - camera.getX()) * camera.scaleX),
						(int)((visionBox.y - camera.getY()) * camera.scaleY),
						(int)(visionBox.width * camera.scaleX),
						(int)(visionBox.height * camera.scaleY) );
			/*
			g.drawRect( (int)((hitbox.x - camera.getX()) * camera.scaleX),
					(int)((hitbox.y - camera.getY()) * camera.scaleY),
					(int)(hitbox.width * camera.scaleX),
					(int)(hitbox.height * camera.scaleY) );
			
			
			g.setColor(Color.GRAY);
			g.drawRect( (int)((hitbox.x - camera.getX()) * camera.scaleX),
						(int)((hitbox.y - camera.getY()) * camera.scaleY),
						(int)(hitbox.width * camera.scaleX),
						(int)(hitbox.height * camera.scaleY) );
			*/
			
			g.fillRect( (int)((healthBar.x - camera.getX()) * camera.scaleX),
					(int)((healthBar.y - camera.getY()) * camera.scaleY),
					(int)(healthBar.width * camera.scaleX),
					(int)(healthBar.height * camera.scaleY) );
			
			for (int i = 0; i < projectileList.size(); i++) {
				projectileList.get(i).draw(g, camera);
			}
        }
    }
    
    public void update() {
    	updateProjectileList();
		updateUnitSensing();
		automateShooting("enemy_projectile");
		checkForNewTarget();
	}

	@Override
    public void updateUnitSensing() {
//		List<AbstractUnit> pul = PlayerUnitManager.unitList;
//		//System.out.println(pul.size());
//		float max = (float) Double.MAX_VALUE;
//
//		targetPlayerUnit = null;
//
//		// find the closest player
//		for (int i = 0; i < pul.size(); i++) {
//			if ( visionbox.intersects(pul.get(i).getHitbox()) ) {
//				float distance = calculateDistance(pul.get(i).getX(), pul.get(i).getY());
//
//				if (distance < max) {
//					// set closest to target player
//					targetPlayerUnit = pul.get(i);
//					max = distance;
//				}
//			}
//		}
		
	}
    
    
	public void automateShooting(String tag) {
//		if (targetPlayerUnit != null) {
//			shootingTimer++;
//			if (shootingTimer%25==0) {
//				//System.out.println("SHOOT");
//				Projectile p = new Projectile(this.x, this.y, 8, 8);
//				p.setVelocity(targetPlayerUnit.x, targetPlayerUnit.y);
//				p.setTag(tag);
//				projectileList.add(p);
//			}
//		}
	}
	
	public void checkForNewTarget() {
		if (targetPlayerUnit == null || targetPlayerUnit.isDestroyed()) {
			updateUnitSensing();
		}
	}
    
	
    // pretty printing 
    @Override
    public String toString() {
    	return "EU : " + tag +" "+ ID;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {

	}

	@Override
	public void issueCommand(CommandType command, CommandContext ctx) {

	}
}
