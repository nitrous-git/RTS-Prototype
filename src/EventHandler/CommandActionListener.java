package EventHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Building.Barracks;
import Panel.CommandPanel;
import Panel.GamePanel;

public class CommandActionListener implements ActionListener {

	private final GamePanel gp;
	private final CommandPanel cp;

	public CommandActionListener(GamePanel gp, CommandPanel cp) {
		this.cp = cp;
	    this.gp = gp;
	}
	
	 @Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("U")) {
	        if (cp.getSelectedEntity() instanceof Barracks) {
	        	((Barracks)cp.getSelectedEntity()).enqueueUnit();
	        }
        }
	
		if (e.getActionCommand().equals("W")) {
			 System.out.println("Set Waypoint");
		}
	}
}	 

