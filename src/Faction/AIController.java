package Faction;

import Panel.CommandPanel;
import Panel.GamePanel;
import Util.Camera;
import Util.SelectionBox;

public class AIController implements FactionController {
    private Faction faction;
    private int tick = 0;

    @Override
    public void init(Faction faction) {
        this.faction = faction;
    }

    @Override
    public void update() {
        tick++;
        if (tick % 300 == 0) {
            // Basic AI behavior goes here ...
        }
    }


    // ------------------------------------------------------------------------------
    @Override
    public CommandPanel getCP() { return null; } // AI control have null command panel

    @Override
    public void init(GamePanel GP,
                     CommandPanel CP,
                     SelectionBox SB,
                     Faction playerFaction,
                     Camera camera) {}        // no-op allowed

}
