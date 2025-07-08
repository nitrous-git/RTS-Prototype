package Panel;

import javax.swing.*;
import java.util.List;

import Building.AbstractBuilding;
import Building.Barracks;
import EventHandler.CommandActionListener;
import EventHandler.MouseEventHandler;
import GameObjects.IEntity;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import Unit.WorkerUnit;

import java.awt.*;
import java.awt.event.ActionListener;


public class CommandPanel extends JPanel {

	private final GamePanel gp;
    public MouseEventHandler MH;
	CommandActionListener CAL;
	
    private final JButton[][] buttons = new JButton[3][3];
    private static final int BUTTON_SIZE = 48;
    
    private IEntity selectedEntity;
    private List<? extends IEntity> selectedEntityList;

    public JButton moveTo, stop, attack, repair, gather, cancel;
    public JButton combatUnitTraining, workerUnitTraining, setWaypoint, cancelLastQueue, cancelConstruction;
    public JButton buildMenu, barracksConstruct, supplyDepotConstruct;


    public CommandPanel(GamePanel gp) {
    	this.gp = gp;
    	setPreferredSize(new Dimension(150, 150));
        setLayout(new GridLayout(3, 3, 4, 4));
        initButtons();
        CAL = new CommandActionListener(gp, this);
    }

    public void setCommandsForUnit(List<AbstractUnit> selectedUnitList) {
        clearAll();

        if (selectedUnitList.size() == 1){
            AbstractUnit selected = selectedUnitList.get(0);
            this.selectedEntity = selected;
            if (selected instanceof WorkerUnit){ setWorkerUnitCommand(); }
            if (selected instanceof CombatUnit){ setCombatUnitCommand(); }
        }
        else if (selectedUnitList.size() > 1){
            this.selectedEntityList = selectedUnitList;
            setMultipleUnitCommand();
        }
        else {
            clearAll();
        }

        revalidate();
        repaint();
    }

    public void setCommandsForBuilding(AbstractBuilding selected) {
        clearAll();

        if (selected instanceof Barracks){
            this.selectedEntity = selected;
            setBarracksCommand();
        }
        else {
            clearAll();
        }

        revalidate();
        repaint();
    }





    public void setWorkerUnitCommand() {
        // MoveTo
        moveTo = configureButton(0, 0, "M", CAL);
        // Stop
        stop = configureButton(0, 1, "S", CAL);
        // Attack
        attack = configureButton(0, 2, "A", CAL);
        // Repair
        repair = configureButton(1, 0, "R", CAL);
        // Gather
        gather = configureButton(1, 1, "G", CAL);
        // To BuildMenu
        buildMenu = configureButton(2, 0, "B", CAL);
    }

    public void setBuildCommand() {
        clearAll();
        // Barracks
        barracksConstruct = configureButton(0, 0, "B", CAL);
        // Supply Depot
        supplyDepotConstruct = configureButton(0, 1, "S", CAL);
        // Cancel
        cancel = configureButton(2, 2, "C", CAL);
    }

    public void setCombatUnitCommand() {
        // MoveTo
        moveTo = configureButton(0, 0, "M", CAL);
        // Stop
        stop = configureButton(0, 1, "S", CAL);
        // Attack
        attack = configureButton(0, 2, "A", CAL);
    }

    public void setMultipleUnitCommand() {
        // MoveTo
        moveTo = configureButton(0, 0, "M", CAL);
        // Stop
        stop = configureButton(0, 1, "S", CAL);
        // Attack
        attack = configureButton(0, 2, "A", CAL);
    }

    public void setWaitCommand() {
        clearAll();
        // Cancel
        cancel = configureButton(2, 2, "C", CAL);
    }

    public void setWaitConstructionCommand() {
        clearAll();
        // Cancel construction of the building 
        cancelConstruction = configureButton(2, 2, "C", CAL);
    }

    public void setBarracksCommand() {
        // Combat unit training
        combatUnitTraining = configureButton(0, 0, "C", CAL);
        // Set Waypoint
        setWaypoint = configureButton(1, 2, "W", CAL);
        // Cancel
        cancelLastQueue = configureButton(2, 2, "C", CAL);
    }

    public void setComCenterCommand() {
        // Worker unit training
        workerUnitTraining = configureButton(0, 0, "W", CAL);
        // Set Waypoint
        setWaypoint = configureButton(1, 2, "W", CAL);
        // Cancel
        cancel = configureButton(2, 2, "C", CAL);
    }










    
    // Utility -----------------------------
    
    private void initButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton btn = new JButton();
                btn.setEnabled(false);
                btn.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                buttons[row][col] = btn;
                add(btn);
            }
        }
    }
    
    private JButton configureButton(int row, int col, String text, ActionListener listener) {
        JButton btn = buttons[row][col];
        btn.setText(text);
        btn.setEnabled(true);
        btn.addActionListener(listener);
        return btn;
    }

    private void clearAll() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton btn = buttons[row][col];
                btn.setText("");
                btn.setEnabled(false);
                for (ActionListener al : btn.getActionListeners()) {
                    btn.removeActionListener(al);
                }
            }
        }
        moveTo = stop = attack = repair = gather = cancel = null;
        combatUnitTraining = workerUnitTraining = setWaypoint = cancelLastQueue = null;
        buildMenu = barracksConstruct = supplyDepotConstruct = null;
        // we should just put all button in a list and loop through it to clear all ...
    }
    
    public IEntity getSelectedEntity()
    {
    	return selectedEntity;
    }

    public void setMouseEventHandler(MouseEventHandler MH){
        this.MH = MH;
    }
}