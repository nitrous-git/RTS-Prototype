package Panel;

import javax.swing.*;

import Manager.GameContext;
import Unit.AbstractUnit;
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
    private GameContext GC;

    private final BufferedImage terrainImg;
    private final int mapRows, mapCols;
    private final float tileSize; // world pixels per tile

    // minimap palette - dark theme friendly
    private static final Color MINI_WALKABLE = new Color(58, 62, 70);     // soft slate
    private static final Color MINI_BLOCKED  = new Color(24, 27, 32);     // deep charcoal
    private static final Color MINI_MARKER   = new Color(90, 96, 108);    // occupied/special cells

    private static final Color MINI_PLAYER   = new Color(100, 170, 255);  // softened blue
    private static final Color MINI_ENEMY    = new Color(255, 110, 110);  // softened red
    private static final Color MINI_VIEWPORT = new Color(230, 235, 245);  // off-white
    private static final Color MINI_BG       = new Color(18, 20, 24);     // panel background
    private static final Color MINI_BORDER   = new Color(52, 56, 64);     // subtle frame

    public MinimapPanel(GamePanel GP, TileMap map, Camera camera, GameContext GC, int miniWidth, int miniHeight) {
        this.GP = GP;
        this.map = map;
        this.camera = camera;
        this.GC = GC;

        this.mapRows = map.row;
        this.mapCols = map.column;
        this.tileSize = GamePanel.TILE_SIZE;

        setPreferredSize(new Dimension(miniWidth, miniHeight));
        setBackground(MINI_BG);
        setBorder(BorderFactory.createLineBorder(MINI_BORDER, 1));

        terrainImg = new BufferedImage(mapCols, mapRows, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < mapRows; y++) {
            for (int x = 0; x < mapCols; x++) {

                boolean walkable = map.isWalkable(x, y);
                terrainImg.setRGB(x, y, walkable ? MINI_WALKABLE.getRGB() : MINI_BLOCKED.getRGB());

                // player/enemy/spawn/special markers baked into terrain
                if (map.intArr[y][x] == 9 || map.intArr[y][x] == 8 || map.intArr[y][x] == 5) {
                    terrainImg.setRGB(x, y, MINI_MARKER.getRGB());
                }
            }
        }

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
        Graphics2D g = (Graphics2D) g0;

        int w = getWidth();
        int h = getHeight();

        g.setColor(MINI_BG);
        g.fillRect(0, 0, w, h);

        g.drawImage(terrainImg, 0, 0, w, h, null);

        float worldW = mapCols * tileSize;
        float worldH = mapRows * tileSize;
        float sx = w / worldW;
        float sy = h / worldH;

        for (AbstractUnit u : GC.getAllUnits()) {
            if (u.getOwnerFaction().getName().equals("Player")) {
                g.setColor(MINI_PLAYER);
            } else {
                g.setColor(MINI_ENEMY);
            }

            int px = (int) (u.getX() * sx);
            int py = (int) (u.getY() * sy);
            g.fillOval(px - 2, py - 2, 4, 4);
        }

        int vx = (int) (camera.getX() * sx);
        int vy = (int) (camera.getY() * sy);
        int vw = (int) ((GamePanel.WIDTH / camera.scaleX) * sx);
        int vh = (int) ((GamePanel.HEIGHT / camera.scaleY) * sy);

        g.setColor(MINI_VIEWPORT);
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
        float camX = wx - viewW / 2f;
        float camY = wy - viewH / 2f;
        
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
