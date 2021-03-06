package com.risk.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.risk.helper.GameMode;
import com.risk.helper.IOHelper;
import com.risk.helper.PhaseEnum;
import com.risk.model.*;
import com.risk.model.strategies.*;
import com.risk.view.CardExchangeView;
import com.risk.view.GameView;
import com.risk.view.MapCreateView;

/**
 * This class is used to handle operations related to MAP.
 * 
 * @author Binay Kumar
 * @version 1.0.0
 * @since 27-September-2018
 *
 */
public class GameController {

	Game game;
	GameView gameView;
	CardExchangeView cardExchangeView;
	public static final String ANSI_RED = "\u001B[31m";

	/**
	 * This function asks user either to createmap or edit map, the user can also
	 * start the game form here.
	 */
	public void startGame() {
		int input = -1;
		while (input != 4) {
			try {
				IOHelper.print("+__________________________________________________________+");
				IOHelper.print("|=====_==============================================_=====|");
				IOHelper.print("|    (_)                                            (_)    |");
				IOHelper.print("|   (___)            WELCOME TO RISK GAME          (___)   |");
				IOHelper.print("|   _}_{_                                          _}_{_   |");
				IOHelper.print("|__[_____]________________________________________[_____]__|");
				IOHelper.print("+==========================================================+");
				IOHelper.print("+======_Game Menu_======+");
				IOHelper.print("1. Create Map");
				IOHelper.print("2. Edit Map");
				IOHelper.print("3. Play Game");
				IOHelper.print("4. Load Game");
				IOHelper.print("5. Exit");
				input = IOHelper.getNextInteger();

				switch (input) {
				case 1:
					createMap();
					break;
				case 2:
					editMap();
					break;
				case 3:
					initializeGame();
					break;
				case 4:
					loadSavedGame();
					break;
				case 5:
					System.exit(0);
				default:
					IOHelper.print("\nInvalid choice. Select Again!\n");
					break;
				}
			} catch (Exception e) {

				IOHelper.print(e.getMessage());
				IOHelper.print("Please try again with the correct input format");
				System.out.println(e.getCause());
				e.printStackTrace();
			} finally {
				System.out.println("Do you want to continue with game? (1/0)");
				int doCont = IOHelper.getNextInteger();
				if (doCont == 0)
					break;
			}
		}
	}

	/**
	 * This function gives the user an editor to create the map and it saves the map
	 * to the disk.
	 */
	private void createMap() {
		MapCreateView mapView = new MapCreateView();
		mapView.showCreateView();
		mapView.saveMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuffer mapContent = new StringBuffer(mapView.returnTextAreaText());
				String mapname = mapView.returnMapNameText();
				Map map = new Map(mapname);
				boolean isMapCreated = map.validateAndCreateMap(mapContent, mapname);
				if (isMapCreated) {
					IOHelper.print("Map Created successfully!");
				} else {
					IOHelper.print("Map is not valid.Please try again");
				}
				mapView.killFrame();
			}
		});
	}

	/**
	 * Method for edit map functionality for all the cases
	 */
	private void editMap() throws NumberFormatException {
		IOHelper.print("List of Maps :- ");
		ArrayList<String> mapList = getListOfMaps();
		int i = 1;
		for (String nameOfMap : mapList) {
			IOHelper.print(i + ")" + nameOfMap);
			i++;
		}
		IOHelper.print("\nEnter Map_Number that you want to edit from above list:");
		int mapNumber = IOHelper.getNextInteger();
		String selectedMapName = mapList.get(mapNumber - 1);
		Map map = new Map(selectedMapName);
		IOHelper.print("'" + selectedMapName + "'");
		map.readMap();
		if (!map.isMapValid()) {
			IOHelper.print("Map is Invalid !");
		}
		int input = -1;
		while (input != 6) {
			IOHelper.print("+------------------------------+");
			IOHelper.print("|________ Edit Map Menu________|");
			IOHelper.print("|    1. Delete Continent       |");
			IOHelper.print("|    2. Delete Country         |");
			IOHelper.print("|    3. Add Continent          |");
			IOHelper.print("|    4. Add Country            |");
			IOHelper.print("|    5. Save Map               |");
			IOHelper.print("|    6. Back                   |");
			IOHelper.print("+------------------------------+");
			IOHelper.print(" Enter option:");
			input = IOHelper.getNextInteger();
			switch (input) {
			case 1:
				IOHelper.print("List of Continents:");
				ArrayList<Continent> continentList = map.getContinentList();
				for (Continent nameOfContinent : continentList) {
					IOHelper.print("->" + nameOfContinent.getContName());
				}
				IOHelper.print("Enter name of the Continent you want to delete:");
				String continentToDelete = IOHelper.getNextString();
				map.deleteContinent(continentToDelete);
				IOHelper.print("Continent '" + continentToDelete + "' is deleted successfuly!");
				break;
			case 2:
				IOHelper.print("List of Countries:");
				ArrayList<Country> countryList = map.getCountryList();
				for (Country nameOfCountry : countryList) {
					IOHelper.print("->" + nameOfCountry.getCountryName());
				}
				IOHelper.print("Enter name of the Country you want to delete from the list given below:");
				String countryToDelete = IOHelper.getNextString();
				map.deleteCountry(countryToDelete);
				IOHelper.print("Country '" + countryToDelete + "' is deleted successfuly!");
				break;
			case 3:
				map.addContinentToMap();
				IOHelper.print("Continent added successfully!");
				break;
			case 4:
				IOHelper.print("List of Continents:-");
				ArrayList<Continent> continentsList = map.getContinentList();
				int continentID = 0;
				for (Continent continent : continentsList) {
					IOHelper.print("-> " + continent.getContName());
					continentID = continent.getContId();
				}
				IOHelper.print("Enter name of the continent where you want to add new country(from above list): ");
				String continentName = IOHelper.getNextString();
				map.addCountryToContinent(continentName, continentID);
				IOHelper.print("Country added successfuly!");
				break;
			case 5:
				if (map.isMapValid()) {
					map.saveMap();
					IOHelper.print("Map saved!");
				} else {
					IOHelper.print("Map saved is invalid!");
				}
				break;
			case 6:
				break;
			default:
				IOHelper.print("Option not Available. Select Again!");
				break;
			}
		}
	}

	/**
	 * This function validates the map and initializes the map.
	 * 
	 * @return map
	 */
	private Map initializeMap(boolean showList) throws NumberFormatException {
		int i = 1;
		ArrayList<String> maps = getListOfMaps();
		if (showList) {
			IOHelper.print("List of Maps:-");
			for (String file : maps) {
				IOHelper.print(i + ")" + file);
				i++;
			}
			IOHelper.print("\nEnter Map number to load Map file:\n");
		}
		int mapNumber = IOHelper.getNextInteger();
		String selectedMapName = maps.get(mapNumber - 1);
		Map map = new Map(selectedMapName);
		map.readMap();

		if (!map.isMapValid()) {
			IOHelper.print("\nInvalid Map. Select Again!");
			map = initializeMap(showList);
		}
		return map;
	}

	/**
	 * This function creates the player objects for initializing Game
	 * 
	 * @param map,
	 *            Map
	 * @throws InterruptedException
	 */
	private void initializeGame() throws NumberFormatException, InterruptedException {

		int gameMode = 5;
		while (gameMode != 1 && gameMode != 2) {
			IOHelper.print("\nWhich mode do you want to play?");
			IOHelper.print("1 - Single Game Mode \n2 - Tournament Mode");
			gameMode = IOHelper.getNextInteger();

			if (gameMode == 1) {
				Map map = initializeMap(true);
				game = new Game(map);
				game.setGameMode(GameMode.SingleGameMode);

				cardExchangeView = new CardExchangeView();
				gameView = new GameView();

				game.addObserver(gameView);
				inputPlayerInformation();
				game.startUpPhase();

				game.singleGameMode();

				gameView.gameInitializer();
				activateListenersOnView();
				game.addObserver(cardExchangeView);

			} else if (gameMode == 2) {

				int M = 0, P = 0, G = 0, D = 0;
				ArrayList<Map> maps = new ArrayList<>();
				ArrayList<PlayerStrategy> stratergies = new ArrayList<>();
				// Enter number of maps
				while (true) {
					IOHelper.print("Enter number of maps you want to play (1-5)?");

					int count = IOHelper.getNextInteger();
					if (count >= 1 && count <= 5) {
						M = count;
						break;
					}
				}

				for (int i = 0; i < M; i++) {
					maps.add(initializeMap(i == 0));
				}

				while (true) {
					IOHelper.print("Enter number of player strategy you want to play (2-4)?");

					int count = IOHelper.getNextInteger();
					if (count >= 2 && count <= 4) {
						P = count;
						break;
					}
				}

				IOHelper.print("Enter " + P + " different strargies from following list");
				IOHelper.print("1- Aggressive");
				IOHelper.print("2- Benevolent");
				IOHelper.print("3- Random");
				IOHelper.print("4- Cheater");
				for (int i = 0; i < P; i++) {
					int playerstrategy = IOHelper.getNextInteger();

					if (playerstrategy == 1) {
						stratergies.add(new Aggressive());
					} else if (playerstrategy == 2) {
						stratergies.add(new Benevolent());
					} else if (playerstrategy == 3) {
						stratergies.add(new Random());
					} else if (playerstrategy == 4) {
						stratergies.add(new Cheater());
					} else {
						IOHelper.print("Enter valid strargy");
						i--;
						continue;
					}

				}

				while (true) {
					IOHelper.print("Enter number of games you want to play in each map (1-5)?");

					int count = IOHelper.getNextInteger();
					if (count >= 1 && count <= 5) {
						G = count;
						break;
					}
				}

				// Enter maximum number of turns
				while (true) {
					IOHelper.print("Enter maximum number turns for for each game (10 - 50)");

					int count = IOHelper.getNextInteger();
					if (count >= 10 && count <= 50) {
						D = count;
						break;
					}
				}
				HashMap<String, ArrayList<String>> tournamentResult = new HashMap<>();

				for (int i = 0; i < M; i++) {
					ArrayList<String> result = new ArrayList<>();
					for (int j = 0; j < G; j++) {
						game = new Game(maps.get(i));
						game.setGameMode(GameMode.TournamentMode);
						game.setMaxTurnsForTournament(D);
						for (int ps = 0; ps < stratergies.size(); ps++) {
							Player player = new Player(ps, stratergies.get(ps).getStrategyName());
							player.setPlayerStrategy(stratergies.get(ps));
							game.addPlayer(player);
						}

						game.startUpPhase();

						game.tournamentMode();

						// add result
						if (game.getGamePhase() == PhaseEnum.GameDraw) {
							result.add("DRAW");
						} else {
							result.add(game.getCurrentPlayer().getName());
						}
					}
					tournamentResult.put(maps.get(i).getMapName(), result);
				}
				printTournamentResult(M, G, D, tournamentResult, stratergies);

			} else {
				IOHelper.print("Enter a Valid Value for game mode");
			}
		}
	}

	private void inputPlayerInformation() throws NumberFormatException {
		IOHelper.print("\nEnter the number of Players between 3 to 5");
		int playerCount = IOHelper.getNextInteger();

		if (playerCount < 3 && playerCount > 5) {
			IOHelper.print("Players count cannot be less than 3 and more than 5");
			inputPlayerInformation();
		} else {
			for (int i = 0; i < playerCount; i++) {
				IOHelper.print("\nEnter the name of Player " + (i + 1));
				String playerName = IOHelper.getNextString();
				IOHelper.print("\nEnter Strategy of the Player ");
				IOHelper.print("1- Human");
				IOHelper.print("2- Aggressive");
				IOHelper.print("3- Benevolent");
				IOHelper.print("4- Random");
				IOHelper.print("5- Cheater");
				int playerstrategy = IOHelper.getNextInteger();

				Player player = new Player(i, playerName);
				if (playerstrategy == 1)
					player.setPlayerStrategy(new Human());
				else if (playerstrategy == 2)
					player.setPlayerStrategy(new Aggressive());
				else if (playerstrategy == 3)
					player.setPlayerStrategy(new Benevolent());
				else if (playerstrategy == 4)
					player.setPlayerStrategy(new Random());
				else if (playerstrategy == 5)
					player.setPlayerStrategy(new Cheater());
				else {
					IOHelper.print("Enter valid stratergy number");
					i--;
				}
				game.addPlayer(player);
			}
		}
	}

	private void loadSavedGame() {
		ArrayList<String> savedGameList = this.getListOfSavedGames();
		int i = 1;
		for (String GameTitle : savedGameList) {
			IOHelper.print(i + ")" + GameTitle);
			i++;
		}
		IOHelper.print("\nEnter Game that you want to load:");
		int gameNumber = IOHelper.getNextInteger();
		String GameTitle = savedGameList.get(gameNumber - 1);
		game = Game.loadGame(GameTitle);

		Map map = game.getMap();
		cardExchangeView = new CardExchangeView();
		gameView = new GameView();
		game.addObserver(gameView);
		game.addObserver(cardExchangeView);
		game.notifyObserversLocal();
		gameView.mapPath = map.getMapPath() + map.getMapName() + ".bmp";
		gameView.gameInitializer();
		activateListenersOnView();
		game.notifyObserversLocal();

		IOHelper.print("Game Successfully Loaded");
	}

	/**
	 * This method will activate all listeners on the View
	 */
	private void activateListenersOnView() {
		addArmyImageClickListener();
		addAttackButtonListener();
		addAllOutButtonListener();
		addEndAttackButtonListener();
		addSourceCountriesListener();
		addMoveArmyButtonListener();
		addAttackerCountryListener();
		addDefenderCountryListener();
		addAttackArmyMoveButtonListner();
		addSkipFortificationButtonListener();
		addSaveButtonListener();
	}

	/**
	 * to add listener on the Add Army labels
	 */
	public void addArmyImageClickListener() {
		gameView.addActionListenToMapLabels(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				JLabel jLabel = (JLabel) e.getSource();
				String string = jLabel.getToolTipText();
				if (game.getGamePhase() == PhaseEnum.Startup || game.getGamePhase() == PhaseEnum.Reinforcement)
					game.addArmyToCountry(string);
			}
		});
	}

	/**
	 * to add listeners on the Attacker Country List
	 */
	public void addAttackerCountryListener() {
		gameView.addActionListenToAttackerCountryList(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String countryName = gameView.getAttackerCountry();
				if (countryName != null) {
					ArrayList<String> neighborCountries = game.getCurrentPlayer()
							.getUnAssignedNeighbouringCountries(countryName);
					gameView.setDefenderCountryComboBox(neighborCountries);
					int diceCount = game.getMaximumAllowableDices(countryName, "Attacker");
					gameView.setAttackingDiceComboBox(diceCount);
				}
			}
		});
	}

	/**
	 * to add listeners on the Defender Country List
	 */
	public void addDefenderCountryListener() {
		gameView.addActionListenToDefendingCountryList(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String countryName = gameView.getDefenderCountry();
				if (countryName != null) {
					int diceCount = game.getMaximumAllowableDices(countryName, "Defender");
					gameView.setDefendingDiceComboBox(diceCount);
				}
			}
		});
	}

	/**
	 * to add listeners on the Source Country list in Fortification Phase
	 */
	public void addSourceCountriesListener() {
		gameView.addActionListenToSourceCountryList(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String countryName = gameView.getSourceCountry();
				if (countryName != null) {
					ArrayList<String> neighborCountries = game.getCurrentPlayer()
							.getAssignedNeighbouringCountries(countryName);
					int armyCount = game.getArmiesAssignedToCountry(countryName);
					gameView.setDestinationCountryComboBox(neighborCountries);
					gameView.setNoOfArmyToMoveJcomboBox(armyCount);
				}
			}
		});
	}

	/**
	 * to add listener on the Attack Button
	 */
	public void addAttackButtonListener() {
		gameView.addActionListenToAttackButton(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String attackerCountry = gameView.getAttackerCountry();
				String defenderCountry = gameView.getDefenderCountry();
				if (attackerCountry != null && defenderCountry != null) {
					if (game.getGamePhase() == PhaseEnum.Attack) {
						Integer attackerDiceCount = Integer.parseInt(GameView.getAttackerNoOfDice());
						Integer defenderDiceCount = Integer.parseInt(GameView.getDefenderNoOfDice());
						game.attackPhase(attackerCountry, defenderCountry, attackerDiceCount, defenderDiceCount);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selecting attacking and defending countries");
				}
			}
		});
	}

	/**
	 * to add listener on the Move Army button in Attack Phase
	 */
	public void addAttackArmyMoveButtonListner() {
		gameView.addActionListenToAttackMoveArmiesButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (GameView.getAttackMoveArmies() != null
						&& game.getCurrentPlayer().getAllowableArmiesMoveFromAttackerToDefender() >= 0) {
					game.moveArmyAfterAttack(Integer.parseInt(GameView.getAttackMoveArmies()));
				} else {
					JOptionPane.showMessageDialog(null, "Cannot perform action");
				}
			}
		});
	}

	/**
	 * to add listener on the All Out Army button in Attack Phase
	 */
	public void addAllOutButtonListener() {
		gameView.addActionListenToAllOutButton(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (game.getGamePhase() == PhaseEnum.Attack) {
					String attackerCountry = gameView.getAttackerCountry();
					String defenderCountry = gameView.getDefenderCountry();
					game.attackAllOutPhase(attackerCountry, defenderCountry);
				}
			}
		});
	}

	/**
	 * to add listener on the End Attack button in Attack Phase
	 */
	public void addEndAttackButtonListener() {
		gameView.addActionListenToEndAttackButton(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (game.getGamePhase() == PhaseEnum.Attack) {
					game.updatePhase();
				}
			}
		});
	}

	/**
	 * to add listener on the Move Armies button in Fortification Phase
	 */
	public void addMoveArmyButtonListener() {
		gameView.addActionListenToMoveArmyButton(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (game.getGamePhase() == PhaseEnum.Fortification)
					game.fortificationPhase(gameView.getSourceCountry(), gameView.getDestinationCountry(),
							gameView.getNoOfArmyToMoveJcomboBox());
			}
		});
	}

	/**
	 * to add listener on the Skip button in Fortification Phase
	 */
	public void addSkipFortificationButtonListener() {
		gameView.addActionListenTofortificationSkipButton(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (game.getGamePhase() == PhaseEnum.Fortification)
					game.updatePhase();
				if (game.getGameMode() == GameMode.SingleGameMode) {
					game.singleGameMode();
				}
			}
		});
	}

	/**
	 * This function returns the list of all the maps in the assets/map directory.
	 * 
	 * @return List of all the map files
	 */
	private ArrayList<String> getListOfMaps() {
		ArrayList<String> fileNames = new ArrayList<String>();
		File folder = new File("assets/maps");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().toLowerCase().contains(".map"))
					fileNames.add(listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
			}
		}
		return fileNames;
	}

	/**
	 * to add listener on the Skip button in Fortification Phase
	 */
	public void addSaveButtonListener() {
		gameView.addActionListenToSaveButton(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				game.saveGame();
			}
		});
	}

	private ArrayList<String> getListOfSavedGames() {
		ArrayList<String> fileNames = new ArrayList<String>();
		File folder = new File("assets/Saved_Games/");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().toLowerCase().contains(".txt"))
					fileNames.add(listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
			}
		}
		return fileNames;
	}

	private void printTournamentResult(int M, int G, int D, HashMap<String, ArrayList<String>> result,
			ArrayList<PlayerStrategy> stratergies) {

		String[] mapStrings = result.keySet().toArray(new String[result.keySet().size()]);
		IOHelper.print("===========================================================================================");
		IOHelper.print("=================================TOURNAMENT RESULT=========================================");
		IOHelper.print("===========================================================================================");
		
		StringBuffer mapNameString = new StringBuffer();
		for (int i = 0; i < mapStrings.length; i++) {
			mapNameString.append(mapStrings[i]+ ",");
		}
		IOHelper.print("M:" + mapNameString);
		
		StringBuffer stratergiesNameString = new StringBuffer();
		for (int i = 0; i < stratergies.size(); i++) {
			stratergiesNameString.append(stratergies.get(i).getStrategyName() + ",");
		}
		IOHelper.print("P:" + stratergiesNameString);
		IOHelper.print("G:" + G);
		IOHelper.print("D:" + D);
		IOHelper.print("\n");
		IOHelper.print("\n");
		StringBuilder sb = new StringBuilder();
		sb.append("|");
		sb.append(getFormattedString(" "));
		for (int i = 0; i < G; i++) {
			sb.append("|");
			sb.append(getFormattedString("Game " + (i + 1)));
		}
		sb.append("|");
		IOHelper.print(getRepeatedFormattedString("-", sb.length()));
		IOHelper.print(sb.toString());
		IOHelper.print(getRepeatedFormattedString("-", sb.length()));

		for (int i = 0; i < mapStrings.length; i++) {

			StringBuilder sbMap = new StringBuilder();
			sbMap.append("|");
			sbMap.append(getFormattedString(mapStrings[i]));

			ArrayList<String> gameResults = result.get(mapStrings[i]);
			for (int j = 0; j < G; j++) {
				sbMap.append("|");
				sbMap.append(getFormattedString(gameResults.get(j)));
			}
			sbMap.append("|");
			IOHelper.print(sbMap.toString());
			IOHelper.print(getRepeatedFormattedString("-", sb.length()));
		}
	}

	private String getRepeatedFormattedString(String input, int length) {
		StringBuilder str = new StringBuilder(input);
		for (int i = input.length(); i <= length - 1; i++)
			str.append(input);
		return str.toString();
	}

	private String getFormattedString(String input) {
		int length = 14;

		StringBuilder str = new StringBuilder(" " + input);
		for (int i = input.length(); i <= length; i++)
			str.append(" ");
		return str.toString();
	}
}
