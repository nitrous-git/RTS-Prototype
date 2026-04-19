package Main;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import Panel.CommandPanel;
import Panel.GamePanel;
import Panel.MinimapPanel;
import Panel.SelectionPanel;

public class GameFrame extends JFrame  {

    private final GamePanel        gamePanel;
    private final MinimapPanel     minimapPanel;
    private final CommandPanel     commandPanel;
    private final SelectionPanel   selectionPanel;

    public JPanel gameContainer = new JPanel();
    public JPanel frameContainer =  new JPanel();
        
    //MinimapPanel minimapPanel = new MinimapPanel(gamePanel, 150 , 150);
    private JPanel minimapContainer =  new JPanel();
    
    //CommandPanel commandPanel = new CommandPanel(gamePanel);
    private JPanel commandContainer =  new JPanel();
    
    //SelectionPanel selectionPanel = new SelectionPanel(gamePanel);
    private JPanel selectionContainer =  new JPanel();

    // Constructor
    GameFrame(GamePanel gamePanel,
              MinimapPanel minimapPanel,
              CommandPanel commandPanel,
              SelectionPanel selectionPanel){
        this.gamePanel      = gamePanel;
        this.minimapPanel   = minimapPanel;
        this.commandPanel   = commandPanel;
        this.selectionPanel = selectionPanel;

        initLayout();
    }

    private void initLayout() {
        setSize(GamePanel.WIDTH, GamePanel.HEIGHT);
        setTitle("RTS Prototype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ----- GAME ---- //
        TitledBorder border_Game = new TitledBorder("Battle Ground");
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
        frameContainer.setLayout(new BorderLayout(0, 8));
        frameContainer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        frameContainer.add(gameContainer, BorderLayout.CENTER);
        frameContainer.add(uiBar,     BorderLayout.SOUTH);

        int uiBarHeight = 200;
        setSize(GamePanel.WIDTH, GamePanel.HEIGHT + uiBarHeight);
        add(frameContainer);

        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
