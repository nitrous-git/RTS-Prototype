package Main;

import Faction.ColorPalette.FactionPalettes;
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
	public void launch() {

		// --- instantiate all game related content ---
		Camera camera = new Camera(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		TileMap map = new TileMap();
		SelectionBox SB = new SelectionBox(camera);

		ResourceNodeRepository RNR = new ResourceNodeRepository(map);
		SpawnSeedRepository SSR = new SpawnSeedRepository(map);
		GameContext GC = new GameContext();

		// --- faction setup ---
		// -------------------------------------------------------
		FactionManager FM = new FactionManager();

		// Controller
		PlayerController PC_FactionA = new PlayerController();
		AIController AIC_FactionB = new AIController(camera);
		AIController AIC_FactionC = new AIController(camera);

		// Faction A
		UnitManager FA_UM = new PlayerUnitManager(map, GC);
		BuildingManager FA_BM = new BuildingManager(map, GC);
		ResourceManager FA_RM = new ResourceManager(map, RNR); //Rangers Alliance
		Faction player_FA = new Faction("Rangers Alliance", PC_FactionA, FA_UM, FA_BM, FA_RM, map, FactionPalettes.FACTION_A);

		player_FA.setSpawnSeed(SSR, 0);
		FA_UM.setOwnerFaction(player_FA);
		FA_BM.setOwnerFaction(player_FA);
		FA_UM.buildUnitSquad();

		// Faction B
		UnitManager FB_UM = new EnemyUnitManager(map, GC);
		BuildingManager FB_BM = new BuildingManager(map, GC);
		ResourceManager FB_RM = new ResourceManager(map, RNR);
		Faction FB = new Faction("Faction B", AIC_FactionB, FB_UM, FB_BM, FB_RM, map, FactionPalettes.FACTION_B);

		FB.setSpawnSeed(SSR, 0);
		FB_UM.setOwnerFaction(FB);
		FB_BM.setOwnerFaction(FB);
		//FB_UM.buildUnitSquad();

		// Faction C
		UnitManager FC_UM = new EnemyUnitManager(map, GC);
		BuildingManager FC_BM = new BuildingManager(map, GC);
		ResourceManager FC_RM = new ResourceManager(map, RNR);
		Faction FC = new Faction("Faction C", AIC_FactionC, FC_UM, FC_BM, FC_RM, map, FactionPalettes.FACTION_C);

		FC.setSpawnSeed(SSR, 1);
		FC_UM.setOwnerFaction(FC);
		FC_BM.setOwnerFaction(FC);
		//FC_UM.buildUnitSquad();

        // Adding factions to FactionManager
		FM.addFaction(player_FA);
		//FM.addFaction(FB);  // comment this faction for 1v1
		FM.addFaction(FC);

		// --- panels ---
		GamePanel GP = new GamePanel(map, camera, SB, FM, RNR);
		MinimapPanel MP = new MinimapPanel(GP, map, camera, GC, 150, 150);
		CommandPanel CP = new CommandPanel(GP, player_FA);
		SelectionPanel SP = new SelectionPanel(player_FA, GC);

		// --- controller init ---
		PC_FactionA.init(GP, CP, SB, GC, player_FA, camera);

		// --- frame ---
		GameFrame frame = new GameFrame(GP, MP, CP, SP);
		frame.setTitle("RTS Prototype");
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// --- loop ---
		GameLoop loop = new GameLoop(GP, MP, SP);
		loop.start();
	}
}