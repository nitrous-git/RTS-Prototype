package Faction;

import EnemyAI.AIManager;
import Manager.GameContext;
import Panel.CommandPanel;
import Panel.GamePanel;
import Util.Camera;
import Util.SelectionBox;

public class AIController implements FactionController {
    private AIManager aiManager;
    private Faction faction;
    private int tick = 0;

    @Override
    public void init(Faction faction) {
        this.faction = faction;
        aiManager = new AIManager(faction);
    }

    @Override
    public void update() {
        aiManager.update();
    }

    // ------------------------------------------------------------------------------
    @Override
    public CommandPanel getCP() { return null; } // AI control have null command panel

    @Override
    public void init(GamePanel GP,
                     CommandPanel CP,
                     SelectionBox SB,
                     GameContext GC,
                     Faction playerFaction,
                     Camera camera) {}        // no-op allowed

}
