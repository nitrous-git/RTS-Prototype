package Panel;

import javax.swing.*;

import Unit.AbstractUnit;
import Unit.EnemyUnit;
import Manager.EnemyUnitManager;
import Manager.PlayerUnitManager;
import Util.Camera;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MinimapPanel extends JPanel {
    private final GamePanel gp;
    private final BufferedImage terrainImg;
    private final int mapRows, mapCols;
    private final float tileSize; // world pixels per tile

    public MinimapPanel(GamePanel gp, int miniWidth, int miniHeight) {
        this.gp = gp;
        this.mapRows  = gp.map.row;
        this.mapCols  = gp.map.column;
        this.tileSize = GamePanel.TILE_SIZE;    
        setPreferredSize(new Dimension(miniWidth, miniHeight)); 
        
        // 1px-per-tile offscreen buffer
        terrainImg = new BufferedImage(mapCols, mapRows, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < mapRows; y++) {
            for (int x = 0; x < mapCols; x++) {
                // choose colors based on your tile data
                boolean walkable = gp.map.isWalkable(x, y);
                terrainImg.setRGB(x, y, walkable ? Color.LIGHT_GRAY.getRGB() : Color.DARK_GRAY.getRGB());
                if (gp.map.intArr[y][x] == 9 || gp.map.intArr[y][x] == 8) {
                    terrainImg.setRGB(x, y, Color.LIGHT_GRAY.getRGB());
				}
            }
        }

        // click to recenter
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                recenterCamera(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D)g0;
        int w = getWidth(), h = getHeight();

        // draw the terrain, scaled up
        g.drawImage(terrainImg, 0, 0, w, h, null);

        // compute world => mini scale
        float worldW = mapCols  * tileSize;
        float worldH = mapRows  * tileSize;
        float sx = w / worldW;
        float sy = h / worldH;
        
        // draw player units
        g.setColor(Color.CYAN);
        for (AbstractUnit u : PlayerUnitManager.unitList) {
            int px = (int)(u.getX() * sx);
            int py = (int)(u.getY() * sy);
            g.fillOval(px-2, py-2, 4, 4);
        }

        // draw enemy units
        g.setColor(Color.RED);
        for (EnemyUnit e : EnemyUnitManager.unitList) {
            int px = (int)(e.getX() * sx);
            int py = (int)(e.getY() * sy);
            g.fillOval(px-2, py-2, 4, 4);
        }

        // draw camera viewport
        Camera cam = gp.camera;
        int vx = (int)(cam.getX() * sx);
        int vy = (int)(cam.getY() * sy);
        int vw = (int)((gp.WIDTH  / cam.scaleX) * sx);
        int vh = (int)((gp.HEIGHT / cam.scaleY) * sy);
        g.setColor(Color.WHITE);
        g.drawRect(vx, vy, vw, vh);
    }

    
    public void recenterCamera(int mx, int my) {
        // world size in pixels
        float worldW = mapCols  * tileSize;
        float worldH = mapRows  * tileSize;

        // minimap → world scale
        float sx = getWidth()  / worldW;
        float sy = getHeight() / worldH;

        // clicked point in world coordinates
        float wx = mx / sx;
        float wy = my / sy;

        // size of viewport in world coords
        float viewW = gp.WIDTH  / gp.camera.scaleX;
        float viewH = gp.HEIGHT / gp.camera.scaleY;

        // center the camera on the clicked point
        float camX = wx - viewW  / 2f;
        float camY = wy - viewH  / 2f;
        
        // clamp so camera stays within [0 .. worldSize – viewSize]
        camX = Math.max(0, Math.min(camX, worldW - viewW));
        camY = Math.max(0, Math.min(camY, worldH - viewH));

        // set the camera position
        gp.camera.setX(camX);
        gp.camera.setY(camY);
        
        gp.repaint();      // main view
        this.repaint();    // minimap panel
    }
}
