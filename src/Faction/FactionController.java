package Faction;

import Panel.CommandPanel;
import Panel.GamePanel;
import Util.Camera;
import Util.SelectionBox;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface FactionController {
    void init(Faction faction);

    void init(GamePanel GP,
              CommandPanel CP,
              SelectionBox SB,
              Faction playerFaction,
              Camera camera);

    void update();

    CommandPanel getCP();
}
