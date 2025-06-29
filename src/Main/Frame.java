package Main;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import EventHandler.KeyEventHandler;
import EventHandler.MouseEventHandler;
import Panel.CommandPanel;
import Panel.GamePanel;
import Panel.MinimapPanel;
import Panel.SelectionPanel;

@SuppressWarnings("serial")
public class Frame extends JFrame implements Runnable {

    GamePanel gamePanel = new GamePanel();
    public JPanel gameContainer = new JPanel();
    public JPanel frameContainer =  new JPanel();
        
    MinimapPanel minimapPanel = new MinimapPanel(gamePanel, 150 , 150);
    private JPanel minimapContainer =  new JPanel();
    
    CommandPanel commandPanel = new CommandPanel(gamePanel);
    private JPanel commandContainer =  new JPanel();
    
    SelectionPanel selectionPanel = new SelectionPanel(gamePanel);
    private JPanel selectionContainer =  new JPanel();
    
    public Thread gameThread = new Thread(this);
    private boolean isRunning;
	private final int FPS_SET = 60;
	private final int UPS_SET = 100;
	
    Frame(){
        setSize(gamePanel.WIDTH, gamePanel.HEIGHT);
        setTitle("RTS Prototype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLnF();

        // ----- GAME ---- //
        TitledBorder border_Game = new TitledBorder("Level Editor");
        border_Game.setTitleJustification(TitledBorder.LEFT);
        border_Game.setTitlePosition(TitledBorder.TOP);
        gameContainer.setBorder(border_Game);

        // gameContainer layout
        GridBagLayout gb_gameContainer = new GridBagLayout();
        GridBagConstraints c_gameContainer = new GridBagConstraints();
        gameContainer.setLayout(gb_gameContainer);       
        
        c_gameContainer.gridx = 1;
        c_gameContainer.gridy = 0;
        c_gameContainer.weightx = 1;
        c_gameContainer.weighty = 1;
        c_gameContainer.fill = GridBagConstraints.BOTH;
        gameContainer.add(gamePanel, c_gameContainer);

        // ----- UI ---- //
        JPanel uiBar = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy     = 0;                          // all in row 0
        gbc.fill      = GridBagConstraints.BOTH;    // make them stretch to fill
        gbc.weighty   = 1.0;               
        
        TitledBorder border_Menu = new TitledBorder("MiniMap");
        border_Menu.setTitleJustification(TitledBorder.LEFT);
        border_Menu.setTitlePosition(TitledBorder.TOP);
        minimapContainer.setBorder(border_Menu);
        
        TitledBorder border_Tile = new TitledBorder("Selection");
        border_Tile.setTitleJustification(TitledBorder.LEFT);
        border_Tile.setTitlePosition(TitledBorder.TOP);
        selectionContainer.setBorder(border_Tile);
        
        TitledBorder border_Layer = new TitledBorder("Command");
        border_Layer.setTitleJustification(TitledBorder.LEFT);
        border_Layer.setTitlePosition(TitledBorder.TOP);
        commandContainer.setBorder(border_Layer);

        // ---- Minimap Panel ----
        GridBagLayout gb_minimapContainer = new GridBagLayout();
        GridBagConstraints c_minimapContainer = new GridBagConstraints();
        minimapContainer.setLayout(gb_minimapContainer);   
        
        c_minimapContainer.gridx = 0;
        c_minimapContainer.gridy = 0;
        c_minimapContainer.weightx = 1;
        c_minimapContainer.weighty = 1;
        c_minimapContainer.fill = GridBagConstraints.BOTH;
        minimapContainer.add(minimapPanel, c_minimapContainer);
        
        gbc.gridx     = 0;                          
        gbc.weightx   = 1.0;                        // relative width = 1
        gbc.insets    = new Insets(0, 0, 0, 1);     // top, left, bottom, right
        uiBar.add(minimapContainer, gbc);
        

        // ---- Selection Panel ----
        GridBagLayout gb_selectionContainer = new GridBagLayout();
        GridBagConstraints c_selectionContainer = new GridBagConstraints();
        selectionContainer.setLayout(gb_selectionContainer);   
        
        c_minimapContainer.gridx = 0;
        c_minimapContainer.gridy = 0;
        c_minimapContainer.weightx = 1;
        c_minimapContainer.weighty = 1;
        c_minimapContainer.fill = GridBagConstraints.BOTH;
        selectionContainer.add(selectionPanel, c_selectionContainer);
        
        gbc.gridx     = 1;
        gbc.weightx   = 1.0;                        
        gbc.insets    = new Insets(0, 1, 0, 1);
        uiBar.add(selectionContainer, gbc);

        // ---- Command Panel ----
        GridBagLayout gb_commandContainer = new GridBagLayout();
        GridBagConstraints c_commandContainer = new GridBagConstraints();
        commandContainer.setLayout(gb_commandContainer);   
        
        c_minimapContainer.gridx = 0;
        c_minimapContainer.gridy = 0;
        c_minimapContainer.weightx = 1;
        c_minimapContainer.weighty = 1;
        c_minimapContainer.fill = GridBagConstraints.BOTH;
        commandContainer.add(commandPanel, c_commandContainer);
        
        gbc.gridx     = 2;
        gbc.weightx   = 1.0;                        
        gbc.insets    = new Insets(0, 1, 0, 0);
        uiBar.add(commandContainer, gbc);
        
        // frame layout: game on top, uiBar at bottom
        frameContainer.setLayout(new BorderLayout());
        frameContainer.add(gameContainer, BorderLayout.CENTER);
        frameContainer.add(uiBar,     BorderLayout.SOUTH);

        int uiBarHeight = 200;
        setSize(GamePanel.WIDTH, GamePanel.HEIGHT + uiBarHeight);
        add(frameContainer);
        
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
 
        MouseEventHandler MH = new MouseEventHandler(gamePanel, commandPanel);
        gamePanel.addMouseListener(MH);
        gamePanel.addMouseMotionListener(MH);
        
        KeyEventHandler KH = new KeyEventHandler(gamePanel);
        gamePanel.addKeyListener(KH);
        
        // Thread start 
        gameThread.start();
    }

	@Override
	public void run() {
		double timePerFrame = 1000000000.0/FPS_SET; //in nanoseconds 
		double timePerUpdate = 1000000000.0/UPS_SET; //in nanoseconds 
		
		long previousTime = System.nanoTime();
		
		int frames = 0;
		int updates = 0;
		long lastCheck = System.currentTimeMillis();
		
		double deltaU = 0;
		double deltaF = 0;
		
		isRunning = true;
		while (isRunning) {
	
			long currentTime = System.nanoTime();
			
			// when dU is >= 1, we remove 1 and add the remainder to dU 
			// dU is percentage of frame lost when (currentTime - previousTime) / timePerFrame
			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;
			
			if (deltaU >= 1) {
				// update the game
				gamePanel.update();
				updates++;
				deltaU--;
			}
			
			if (deltaF >= 1) {
				// render the game
				gamePanel.repaint();
		        minimapPanel.repaint();
		        selectionPanel.repaint();
				deltaF--;
				frames++;
			}
			
			if (System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				//System.out.println("FPS : " + frames + " | UPS : " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}

	// --- SET LOOK AND FEEL --- //
	public void setLnF() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
