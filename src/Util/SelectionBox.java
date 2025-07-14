package Util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Panel.GamePanel;

public class SelectionBox {
	
	GamePanel p;

    // Variables to track the selection box
    public boolean dragging;
    private int startX, startY;  // where the user started dragging
    private int endX, endY;      // where the user ended dragging  
    
    public SelectionBox(GamePanel panel) {
		this.p = panel;
	}

	public void drawSelectionBox(Graphics2D g2d) {
	    // Calculate delta
        int dx = endX - startX;
        int dy = endY - startY;

        // Default assumption: we draw from (startX, startY)
        int rectX = startX;
        int rectY = startY;
        int width = dx;
        int height = dy;

        // If dx < 0, shift rectX and flip width
        if (dx < 0) {
            rectX = startX + dx;  // move left
            width = -dx;          // make width positive
        }

        // If dy < 0, shift rectY and flip height
        if (dy < 0) {
            rectY = startY + dy;  // move up
            height = -dy;         // make height positive
        }

        g2d.setColor(GameColors.SELECTION_BOX);
        g2d.drawRect((int)(rectX - p.camera.getX()), (int)(rectY - p.camera.getY()), width, height);
    }
	
    public void startSelection(int x, int y) {
		startX = (int) (x + p.camera.getX());
		startY = (int) (y + p.camera.getY());
		endX = (int) (x + p.camera.getX());
		endY = (int) (y + p.camera.getY());
		dragging = true;
    }
	
	public void updateSelection(int x, int y) {
		endX = (int) (x + p.camera.getX());
	    endY = (int) (y + p.camera.getY());	
	}
	
	public void finishSelection(int x, int y) {
		endX = (int) (x + p.camera.getX());
	    endY = (int) (y + p.camera.getY());	
        dragging = false;
	}
	   
	// replace arguments for bounding box in unit class 
    public boolean intersects(Rectangle.Float unitHitbox) {
        // Build the selection rectangle from start/end
        int dx = endX - startX;
        int dy = endY - startY;

        int rectX = startX;
        int rectY = startY;
        int width = dx;
        int height = dy;

        if (dx < 0) {
            rectX = startX + dx;
            width = -dx;
        }
        if (dy < 0) {
            rectY = startY + dy;
            height = -dy;
        }
        
        // This selection’s bounding box
        Rectangle selectionBounds = new Rectangle(rectX, rectY, width, height);

        // Return true if they intersect
        return selectionBounds.intersects(unitHitbox);
    }
}
