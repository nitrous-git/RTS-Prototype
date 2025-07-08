package EventHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import Building.Barracks;
import Command.CommandType;
import Manager.PlayerUnitManager;
import Panel.CommandPanel;
import Panel.GamePanel;
import Resource.BuildingType;
import Resource.UnitType;
import Unit.AbstractUnit;
import Unit.IControllable;

public class CommandActionListener implements ActionListener {

	private final GamePanel gp;
	private final CommandPanel cp;

	public CommandActionListener(GamePanel gp, CommandPanel cp) {
		this.cp = cp;
	    this.gp = gp;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// ----------------------------------------

		// Barracks Event
		if (source == cp.combatUnitTraining) {
	        if (cp.getSelectedEntity() instanceof Barracks) { // don't even need to check...
	        	((Barracks)cp.getSelectedEntity()).produce(UnitType.COMBAT);
	        }
        }

		// Command Center Event
		if (source == cp.workerUnitTraining) { }

		// AbstractBuilding Shared Event
		if (source == cp.setWaypoint) {
			System.out.println("Set Waypoint - to implement");
		}
	    if (source == cp.cancelLastQueue) {
		    System.out.println("Cancel Last Queued Unit - to implement");
	    }

		// ----------------------------------------

		// AbstractUnit shared Event
		if (source == cp.moveTo) {
			cp.setWaitCommand();
			cp.MH.setMode(MouseEventHandler.Mode.MOVE);
		}
		if (source == cp.stop) {
			// don't wait, just pause the unit movement
			List<AbstractUnit> units = PlayerUnitManager.getSelectedUnitList();
			// (we don't need to check is isSelected, clean it up later...)
			for (AbstractUnit unit : units) {
				if (unit instanceof IControllable controllable && controllable.isSelected()) {
					controllable.issueCommand(CommandType.HOLD_POSITION, null);
				}
			}
		}
		if (source == cp.attack) {
			cp.setWaitCommand();
			// do attack
		}
		if (source == cp.cancel) {
			cp.MH.setMode(MouseEventHandler.Mode.SELECTION);
			cp.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
			gp.PUM.clearMovementHelper();
			gp.BM.clearPlacementHelper();
		}

		// Worker Event
		if (source == cp.repair) {
			cp.setWaitCommand();
			cp.MH.setMode(MouseEventHandler.Mode.SELECTION);
		}
		if (source == cp.gather) {
			cp.setWaitCommand();
			// do gather
		}
 		if (source == cp.buildMenu) {
			 cp.setBuildCommand();
			 // to build menu
		}

		// BuildingMenu Event (accessible from WorkerUnit)
		if (source == cp.barracksConstruct) {
			cp.setWaitCommand();
			cp.MH.setMode(MouseEventHandler.Mode.PLACEMENT);
			cp.MH.setCurrentBuildingType(BuildingType.BARRACKS);

		}
		if (source == cp.supplyDepotConstruct) {
			cp.setWaitCommand();
		}

	}
}	 

