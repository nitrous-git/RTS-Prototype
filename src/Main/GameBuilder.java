package Main;

import EventHandler.KeyEventHandler;
import EventHandler.MouseEventHandler;
import Faction.Faction;
import Faction.PlayerController;
import Faction.AIController;
import Faction.FactionManager;
import Faction.SpawnSeedRepository;
import Manager.*;
import Panel.CommandPanel;
import Panel.GamePanel;
import Panel.MinimapPanel;
import Panel.SelectionPanel;
import Resource.ResourceNodeRepository;
import Util.Camera;
import Util.SelectionBox;
import Util.TileMap;


public class GameBuilder {
	public static void main(String[] args) {

		// — instantiate all game related content —
		Camera camera = new Camera(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		TileMap map = new TileMap();
		SelectionBox SB = new SelectionBox(camera);
		ResourceNodeRepository RNR = new ResourceNodeRepository(map);
		SpawnSeedRepository SSR = new SpawnSeedRepository(map);
		GameContext GC = new GameContext();

		/// Faction Setup start -> instantiate inside lobby
		FactionManager FM = new FactionManager();

		PlayerController playerController = new PlayerController();
		AIController aiController = new AIController(camera);

		UnitManager PUM = new PlayerUnitManager(map);
		BuildingManager PBM = new BuildingManager(map, GC);
		ResourceManager PRM = new ResourceManager(map, RNR);
		Faction playerFaction = new Faction("Player", playerController, PUM, PBM, PRM, map);
		playerFaction.setSpawnSeed(SSR, 0);
		PUM.setOwnerFaction(playerFaction);
		PBM.setOwnerFaction(playerFaction);
		PUM.buildUnitSquad(); // not such a good idea, should be initialized inside UM class

		UnitManager EUM = new EnemyUnitManager(map);
		BuildingManager EBM = new BuildingManager(map, GC);
		ResourceManager ERM = new ResourceManager(map, RNR);
		Faction aiFaction = new Faction("AI", aiController, EUM, EBM, ERM, map);
		aiFaction.setSpawnSeed(SSR, 2);
		EUM.setOwnerFaction(aiFaction);
		EBM.setOwnerFaction(aiFaction);
		EUM.buildUnitSquad(); // same comment

		FM.addFaction(playerFaction);
		FM.addFaction(aiFaction);
		/// Faction Setup end

		// — instantiate the core panels —
		GamePanel GP = new GamePanel(map, camera, SB, FM, RNR);
		MinimapPanel MP   = new MinimapPanel(GP, map, camera, 150, 150);
		CommandPanel CP   = new CommandPanel(GP, playerFaction);
		SelectionPanel SP = new SelectionPanel(playerFaction, GC);

		// - Prepare user controller -
		playerController.init(GP, CP, SB, GC, playerFaction, camera);


		// — inject cross-panel references now they’re built —
		//GP.setSP(SP);
		//GP.setCP(CP);

		// — create the frame, passing in already-wired panels only —
		GameFrame frame = new GameFrame(GP, MP, CP, SP);
		frame.setTitle("RTS Prototype");
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// — start the main game loop —
		GameLoop loop = new GameLoop(GP, MP, SP);
		loop.start();
	}
}
