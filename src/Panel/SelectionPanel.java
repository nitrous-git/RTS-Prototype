package Panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import Building.AbstractBuilding;
import Building.Barracks;
import Unit.AbstractUnit;
import GameObjects.IEntity;
import Manager.BuildingManager;
import Manager.PlayerUnitManager;

public class SelectionPanel extends JPanel {

	private final GamePanel gp;
	
    public SelectionPanel(GamePanel gp) {
    	this.gp = gp;
        setPreferredSize(new Dimension(500, 150));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<AbstractUnit> sel = PlayerUnitManager.getSelectedUnitList();
        IEntity sb = BuildingManager.getSelectedBuilding();
        g.setColor(Color.BLACK);
        int y = 20;

        if (sel.isEmpty() && sb == null) {
            g.drawString("No selection", 10, y);
            return;
        }

		// Enforce the unit-over-building policy in the selection box logic
		// just like Starcraft 1 
        if (!sel.isEmpty()) {
            paintUnitInfo(sel, g, y);
            return;
        }
        if (sb != null) { // only one selected building at the time
        	paintBuildingInfo(sb, g, y);
		}
    }

	public void paintUnitInfo(List<AbstractUnit> sel, Graphics g, int y) {
        if (sel.size() == 1) {
            AbstractUnit u = sel.get(0);
            g.drawString("Tag: " + u.getTag(), 10, y); 
            y += 20;
            g.drawString("ID: " + u.getID(), 10, y); 
            y += 20;
            g.drawString("Health: " + (int)u.getCurrentHealth() + "/" + (int)u.getMaxHealth(), 10, y);
        } else {
            if (allSameTag(sel)) {
                String tag = sel.get(0).getTag();
                g.drawString("Tag: " + tag, 10, y); 
                y += 20;
                g.drawString("Count: " + sel.size(), 10, y);
            } else {
                g.drawString("Source: Multiple", 10, y); 
                y += 20;
                g.drawString("Count: " + sel.size(), 10, y);
            }
        }
	}

    public void paintBuildingInfo(IEntity sb, Graphics g, int y) {
    	AbstractBuilding ab = (AbstractBuilding)sb;
        g.drawString("Tag: " + ab.getTag(), 10, y); 
        y += 20;
        g.drawString("ID: " + ab.getID(), 10, y); 
        y += 20;
        g.drawString("Health: " + (int)ab.getCurrentHealth() + "/" + (int)ab.getMaxHealth(), 10, y);
        
        // add UI if in production 
        y += 20;

        UnderConstructionSlider(ab, g, y);

        // ---production cooldown slider for Barracks ---
        if (ab instanceof Barracks) {
            Barracks b = (Barracks) ab;
            if (b.isProducing()) {
                // --- cooldown slider ---
                int cd    = b.getCooldownTimer();
                int maxCd = Building.Barracks.getCooldownTicks();
                int barW  = 100, barH = 10;
                float frac = (float)(maxCd - cd) / maxCd;
                int fillW = (int)(barW * frac);

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(10, y, barW, barH);
                g.setColor(new Color(0.0f, 0.4f, 0.0f));
                g.fillRect(10, y, fillW, barH);
                g.setColor(Color.BLACK);
                g.drawRect(10, y, barW, barH);
                g.drawString(
                    String.format("Spawn in: %d%%", Math.round(frac * 100)),
                    10 + barW + 5,
                    y + barH
                );
                    
                // --- blue-circle icons under slider ---
                y += barH + 8;
                int iconSize = 12;
                int spacing  = 6;
                int maxIcons = 5;
                int count    = Math.min(b.getQueueSize(), maxIcons);
   
                for (int i = 0; i < count; i++) {
	                int xPos = 10 + i * (iconSize + spacing);
	           		// filled blue circle
	               	g.setColor(Color.BLUE);
	               	g.fillOval(xPos, y, iconSize, iconSize);
	               	// black outline
	           		g.setColor(Color.BLACK);
	       			g.drawOval(xPos, y, iconSize, iconSize);
                }
            }
        }
	}

    private boolean allSameTag(List<? extends IEntity> entity) {
        if (entity.isEmpty()) {	
            return false;
        }
        String tag = entity.get(0).getTag();
        for (IEntity e : entity) {
            if (!e.getTag().equals(tag)) {
                return false;
            }
        }
        return true;
    }

    public void UnderConstructionSlider(AbstractBuilding ab, Graphics g, int y){
        if (ab.isUnderConstruction()) {
            // --- Barracks under construction --- //
            if (ab instanceof Barracks) {
                Barracks b = (Barracks) ab;
                // --- cooldown slider ---
                int cd    = b.getConstructionTimer();
                int maxCd = Building.Barracks.getConstructionTicks();
                int barW  = 100, barH = 10;
                float frac = (float)(maxCd - cd) / maxCd;
                int fillW = (int)(barW * frac);

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(10, y, barW, barH);
                g.setColor(new Color(0.0f, 0.4f, 0.0f));
                g.fillRect(10, y, fillW, barH);
                g.setColor(Color.BLACK);
                g.drawRect(10, y, barW, barH);
                g.drawString(
                        String.format("Completed in: %d%%", Math.round(frac * 100)),
                        10 + barW + 5,
                        y + barH
                );
            }
        }
    }
}