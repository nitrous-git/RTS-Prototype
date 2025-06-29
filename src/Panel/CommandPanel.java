package Panel;

import javax.swing.*;

import Building.Barracks;
import EventHandler.CommandActionListener;
import GameObjects.IEntity;

import java.awt.*;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class CommandPanel extends JPanel {

	private final GamePanel gp;
	CommandActionListener CAL;
	
    private final JButton[][] buttons = new JButton[3][3];
    private static final int BUTTON_SIZE = 48;
    
    private IEntity selectedEntity;

    public CommandPanel(GamePanel gp) {
    	this.gp = gp;
    	setPreferredSize(new Dimension(150, 150));
        setLayout(new GridLayout(3, 3, 4, 4));
        initButtons();
        CAL = new CommandActionListener(gp, this);
    }
    
    public void setCommandsFor(IEntity selected) {
        clearAll();

        if (selected instanceof Barracks) {
        	selectedEntity = (Barracks) selected;
        	//System.out.println(selectedEntity.getID());
            // combat unit train button
            configureButton(0, 0, "U", CAL);
            // combat unit waypoint button
            configureButton(2, 2, "W", CAL);
        }else {
        	clearAll();
		}

        revalidate();
        repaint();
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
    
    private void configureButton(int row, int col, String text, ActionListener listener) {
        JButton btn = buttons[row][col];
        btn.setText(text);
        btn.setEnabled(true);
        btn.addActionListener(listener);
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
    }
    
    public IEntity getSelectedEntity()
    {
    	return selectedEntity;
    }
}