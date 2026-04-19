package Panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import Building.AbstractBuilding;
import Building.Barracks;
import Building.CommandCenter;
import Faction.Faction;
import Manager.GameContext;
import Manager.ResourceManager;
import Resource.ResourceNode;
import Unit.AbstractUnit;
import GameObjects.IEntity;
import Manager.BuildingManager;
import Manager.PlayerUnitManager;
import Unit.CombatUnit;
import Unit.WorkerUnit;

public class SelectionPanel extends JPanel {

    private Faction playerFaction;
    private GameContext GC;

    public SelectionPanel(Faction playerFaction, GameContext GC) {
        this.playerFaction = playerFaction;
        this.GC = GC;
        setPreferredSize(new Dimension(500, 150));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<AbstractUnit> sel = GC.constructSelectedUnitList();
        List<ResourceNode> rnl = playerFaction.getResourceManager().getSelectedResourceNodeList();
        IEntity sb = GC.getSelectedBuilding(); //playerFaction.getBuildingManager().getSelectedBuilding();

        g.setColor(Color.LIGHT_GRAY);
        int y = 20;

        if (sel.isEmpty() && sb == null && rnl.isEmpty()) {
            //g.drawString("No selection", 10, y);
            paintResourceEconomyInfo(g, y);
            return;
        }

		// Enforce the unit-over-building policy in the selection box logic
		// just like Starcraft 1
        if (!sel.isEmpty()) {
            paintUnitInfo(sel, g, y);
            return;
        }
        else if (sb != null) { // only one selected building at the time
        	paintSelectedBuildingInfo(sb, g, y);
            return;
		}
        else if (!rnl.isEmpty()) {
            paintResourceNodeInfo(rnl,g, y);
            return;
        }
    }

	public void paintUnitInfo(List<AbstractUnit> sel, Graphics g, int y) {
        if (sel.size() == 1) {
            AbstractUnit u = sel.get(0);
            g.drawString("Tag: " + u.getTag(), 10, y); 
            y += 20;
            g.drawString("ID: " + u.getID(), 10, y);
            y += 20;
            g.drawString("Faction: " + u.getOwnerFaction().getName(), 10, y);
            y += 20;
            g.drawString("Health: " + (int)u.getCurrentHealth() + "/" + (int)u.getMaxHealth(), 10, y);
            if (u instanceof WorkerUnit wu) {
                y += 20;
                g.drawString("Type : " + wu.getCurrentGatherType(), 10, y);
                y += 20;
                g.drawString("Capacity : " + wu.carryLoad + "/" + wu.carryCapacity, 10, y);
            }
            if (u instanceof CombatUnit cu) {
                y += 20;
                g.drawString("State : " + cu.getCurrentState(), 10, y);
            }
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

    public void paintResourceNodeInfo(List<ResourceNode> sel, Graphics g, int y) {
        if (sel.size() == 1) {
            ResourceNode n = sel.get(0);
            g.drawString("Tag: " + n.getTag(), 10, y);
            y += 20;
            g.drawString("ID: " + n.getID(), 10, y);
            y += 20;
            g.drawString("Resources : " + (int)n.getRemainingAmount() + "/" + (int)n.initialAmount, 10, y);
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

    public void paintSelectedBuildingInfo(IEntity sb, Graphics g, int y) {
    	AbstractBuilding ab = (AbstractBuilding)sb;
        g.drawString("Tag: " + ab.getTag(), 10, y); 
        y += 20;
        g.drawString("ID: " + ab.getID(), 10, y); 
        y += 20;
        g.drawString("Faction: " + ab.getOwnerFaction().getName(), 10, y);
        y += 20;
        g.drawString("Health: " + (int)ab.getCurrentHealth() + "/" + (int)ab.getMaxHealth(), 10, y);
        
        // add UI if in production 
        y += 20;

        UnderConstructionSlider(ab, g, y);
        ProductionSlider(ab, g, y);
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
            // --- cooldown slider ---
            int cd    = ab.getConstructionTimer();
            int maxCd = AbstractBuilding.getConstructionTicks();
            int barW  = 100, barH = 10;
            float frac = (float)(maxCd - cd) / maxCd;
            int fillW = (int)(barW * frac);

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(10, y, barW, barH);
            g.setColor(new Color(0.0f, 0.4f, 0.0f));
            g.fillRect(10, y, fillW, barH);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(10, y, barW, barH);
            g.drawString(
                    String.format("Completed in: %d%%", Math.round(frac * 100)),
                    10 + barW + 5,
                    y + barH
            );

        }
    }

    public void ProductionSlider(AbstractBuilding ab, Graphics g, int y){
        if (ab.isProducing()) {
            // --- production cooldown slider
            int cd = ab.getCooldownTimer();
            int maxCd = AbstractBuilding.getCooldownTicks();
            int barW = 100, barH = 10;
            float frac = (float) (maxCd - cd) / maxCd;
            int fillW = (int) (barW * frac);

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
            int spacing = 6;
            int maxIcons = 5;
            int count = Math.min(ab.getQueueSize(), maxIcons);

            for (int i = 0; i < count; i++) {
                int xPos = 10 + i * (iconSize + spacing);

                if (ab instanceof Barracks) {
                    // filled blue circle
                    g.setColor(Color.BLUE);
                    g.fillOval(xPos, y, iconSize, iconSize);
                }

                if (ab instanceof CommandCenter) {
                    // filled blue circle
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillOval(xPos, y, iconSize, iconSize);
                }

                // black outline
                g.setColor(Color.BLACK);
                g.drawOval(xPos, y, iconSize, iconSize);
            }
        }
    }

    public void paintResourceEconomyInfo(Graphics g, int y){
        g.drawString("Economy: ", 10, y);
        y += 20;
        g.drawString("Mineral: " + playerFaction.getResourceManager().getMineralCount(), 10, y);
        y += 20;
        g.drawString("Gas: " + playerFaction.getResourceManager().getGasCount(), 10, y);
        y += 20;
        g.drawString("Population : " + (int)playerFaction.getResourceManager().getUsedSupply() + "/" + (int)playerFaction.getResourceManager().getMaxSupply(), 10, y);
    }
}