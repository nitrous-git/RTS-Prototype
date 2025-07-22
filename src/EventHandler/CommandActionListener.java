package EventHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import Building.Barracks;
import Building.CommandCenter;
import Command.CommandType;
import Faction.Faction;
import Manager.PlayerUnitManager;
import Panel.CommandPanel;
import Building.BuildingType;
import Unit.UnitType;
import Unit.AbstractUnit;
import Unit.IControllable;
import Util.Logger;

public class CommandActionListener implements ActionListener {

	private final Faction playerFaction;
	private final CommandPanel CP;

	private PlayerUnitManager PUM;

	public CommandActionListener(Faction playerFaction, CommandPanel CP) {
		this.CP = CP;
	    this.playerFaction = playerFaction;

		PUM = ((PlayerUnitManager)playerFaction.getUnitManager());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// ----------------------------------------

		// Barracks Event
		if (source == CP.combatUnitTraining) {
	        if (CP.getSelectedEntity() instanceof Barracks) { // don't even need to check...
	        	((Barracks)CP.getSelectedEntity()).produce(UnitType.COMBAT);
	        }
        }

		// Command Center Event
		if (source == CP.workerUnitTraining) {
			if (CP.getSelectedEntity() instanceof CommandCenter) { // don't even need to check...
				((CommandCenter)CP.getSelectedEntity()).produce(UnitType.WORKER);
			}
		}

		// AbstractBuilding Shared Event
		if (source == CP.setWaypoint) {
			Logger.log("Set Waypoint - to implement");
			System.out.println("Set Waypoint - to implement");
		}
	    if (source == CP.cancelLastQueue) {
			Logger.log("Cancel Last Queued Unit - to implement");
		    System.out.println("Cancel Last Queued Unit - to implement");
	    }

		// ----------------------------------------

		// AbstractUnit shared Event
		if (source == CP.moveTo) {
			CP.setWaitCommand();
			CP.MH.setMode(MouseEventHandler.Mode.MOVE);
		}
		if (source == CP.stop) {
			// don't wait, just pause the unit movement
			List<AbstractUnit> units = PlayerUnitManager.getSelectedUnitList();
			// (we don't need to check is isSelected, clean it up later...)
			for (AbstractUnit unit : units) {
				if (unit instanceof IControllable controllable && controllable.isSelected()) {
					controllable.issueCommand(CommandType.HOLD_POSITION, null);
				}
			}
		}
		if (source == CP.attack) {
			CP.setWaitCommand();
			// do attack
		}
		if (source == CP.cancel) {
			CP.MH.setMode(MouseEventHandler.Mode.SELECTION);
			CP.setNoSelectionCommand();
			PUM.clearMovementHelper();
			playerFaction.getBuildingManager().clearPlacementHelper();
		}

		// Worker Event
		if (source == CP.repair) {
			CP.setWaitCommand();
			CP.MH.setMode(MouseEventHandler.Mode.SELECTION);
		}
		if (source == CP.gather) {
			CP.setWaitCommand();
			// do gather
		}
 		if (source == CP.buildMenu) {
			CP.setBuildCommand();
			 // to build menu
		}

		// BuildingMenu Event (accessible from WorkerUnit)
		if (source == CP.barracksConstruct) {
			CP.setWaitCommand();
			CP.MH.setMode(MouseEventHandler.Mode.PLACEMENT);
			CP.MH.setCurrentBuildingType(BuildingType.BARRACKS);

		}
		if (source == CP.supplyDepotConstruct) {
			CP.setWaitCommand();
			CP.MH.setMode(MouseEventHandler.Mode.PLACEMENT);
			CP.MH.setCurrentBuildingType(BuildingType.SUPPLY_DEPOT);
		}

		if (source == CP.commandCenterConstruct) {
			CP.setWaitCommand();
			CP.MH.setMode(MouseEventHandler.Mode.PLACEMENT);
			CP.MH.setCurrentBuildingType(BuildingType.COMMAND_CENTER);
		}

		if (source == CP.cancelConstruction) {
			Logger.log("Cancel Construction - to implement");
			System.out.println("Cancel Construction - to implement - from CommandActionListener");
		}


	}
}	 

