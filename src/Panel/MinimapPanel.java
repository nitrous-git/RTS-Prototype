package Panel;

import javax.swing.*;

import Unit.AbstractUnit;
import Unit.EnemyUnit;
import Manager.EnemyUnitManager;
import Manager.PlayerUnitManager;
import Util.Camera;
import Util.GameColors;
import Util.TileMap;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MinimapPanel extends JPanel {
    private GamePanel GP;
    private TileMap map;
    private Camera camera;

    private final BufferedImage terrainImg;
    private final int mapRows, mapCols;
    private final float tileSize; // world pixels per tile

    public MinimapPanel(GamePanel GP, TileMap map, Camera camera, int miniWidth, int miniHeight) {
        this.GP = GP;
        this.map = map;
        this.camera = camera;
        this.mapRows  = map.row;
        this.mapCols  = map.column;
        this.tileSize = GamePanel.TILE_SIZE;    
        setPreferredSize(new Dimension(miniWidth, miniHeight)); 
        
        // 1px-per-tile offscreen buffer
        terrainImg = new BufferedImage(mapCols, mapRows, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < mapRows; y++) {
            for (int x = 0; x < mapCols; x++) {
                // choose colors based on your tile data
                boolean walkable = map.isWalkable(x, y);
                terrainImg.setRGB(x, y, walkable ? Color.LIGHT_GRAY.getRGB() : Color.DARK_GRAY.getRGB());
                // change all of this... just check != 0 no isWalkable BS
                if (map.intArr[y][x] == 9
                        || map.intArr[y][x] == 8
                        || map.intArr[y][x] == 5) {
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
        g.setColor(Color.BLUE);
        for (AbstractUnit u : PlayerUnitManager.unitList) {
            int px = (int)(u.getX() * sx);
            int py = (int)(u.getY() * sy);
            g.fillOval(px-2, py-2, 4, 4);
        }


        // draw enemy units
        g.setColor(Color.RED);
        for (AbstractUnit e : EnemyUnitManager.unitList) {
            int px = (int)(e.getX() * sx);
            int py = (int)(e.getY() * sy);
            g.fillOval(px-2, py-2, 4, 4);
        }

        // draw camera viewport
        int vx = (int)(camera.getX() * sx);
        int vy = (int)(camera.getY() * sy);
        int vw = (int)((GamePanel.WIDTH  / camera.scaleX) * sx);
        int vh = (int)((GamePanel.HEIGHT / camera.scaleY) * sy);
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
        float viewW = GamePanel.WIDTH / camera.scaleX;
        float viewH = GamePanel.HEIGHT / camera.scaleY;

        // center the camera on the clicked point
        float camX = wx - viewW  / 2f;
        float camY = wy - viewH  / 2f;
        
        // clamp so camera stays within [0 ... worldSize – viewSize]
        camX = Math.max(0, Math.min(camX, worldW - viewW));
        camY = Math.max(0, Math.min(camY, worldH - viewH));

        // set the camera position
        camera.setX(camX);
        camera.setY(camY);
        
        GP.repaint();      // main view
        this.repaint();    // minimap panel
    }
}
