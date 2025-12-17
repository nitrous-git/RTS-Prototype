package Faction;

import EventHandler.KeyEventHandler;
import EventHandler.MouseEventHandler;
import Manager.GameContext;
import Panel.CommandPanel;
import Panel.GamePanel;
import Util.Camera;
import Util.SelectionBox;

public class PlayerController implements FactionController {
    private MouseEventHandler MH;
    private KeyEventHandler KH;
    private GameContext GC;
    private Faction playerFaction;
    private GamePanel GP;
    private CommandPanel CP;
    private SelectionBox SB;
    private Camera camera;

    public PlayerController() { }

    @Override
    public void init(GamePanel GP,
                     CommandPanel CP,
                     SelectionBox SB,
                     GameContext GC,
                     Faction playerFaction,
                     Camera camera) {
        this.GP = GP;
        this.CP = CP;
        this.SB = SB;
        this.GC = GC;
        this.playerFaction = playerFaction;
        this.camera = camera;

        MH = new MouseEventHandler(GP, CP, SB, GC, playerFaction, camera);
        KH   = new KeyEventHandler(GP);

        GP.addMouseListener(MH);
        GP.addMouseMotionListener(MH);
        GP.addKeyListener(KH);

        CP.setMouseEventHandler(MH);
    }

    @Override
    public void init(Faction faction) { } // no-op allowed

    @Override
    public void update() { } // no-op allowed

    public CommandPanel getCP() { return CP; }
    public GameContext getGC() { return GC; }
    public Camera getCamera() { return camera; }
}
