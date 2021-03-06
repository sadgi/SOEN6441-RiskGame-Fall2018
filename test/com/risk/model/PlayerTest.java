package com.risk.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.risk.helper.CardEnum;
import com.risk.helper.Common;
import com.risk.helper.IOHelper;
import com.risk.helper.PhaseEnum;
import com.risk.model.strategies.Aggressive;
import com.risk.model.strategies.Benevolent;
import com.risk.model.strategies.Cheater;
import com.risk.model.strategies.Human;
import com.risk.model.strategies.PlayerStrategy;
import com.risk.model.strategies.Random;

/**
 * Test class for testing game functionality
 *
 */
public class PlayerTest {

	Map map;
	Game game;
	Game game2;
	CardEnum cardEnum;
	String mapToTest = "Africa.map";
	Integer playerCount = 5;
	Player p1;
	Country c1;
	Country c2;

	/**
	 * Test Method for assign countries to player after reading the map
	 */
	@Before
	public void readMapAndAssignCountries() {
		IOHelper.print("Test: creating maps and generating player randomly");
		map = new Map(mapToTest);
		map.readMap();

		game = new Game(map);
		for (int i = 0; i < playerCount; i++) {
			String playerName = "player " + i;
			Player player = new Player(i, playerName);
			game.addPlayer(player);
		}
		game.startUpPhase();

		// Loop until all armies are assigned for all players
		while (game.getGamePhase() == PhaseEnum.Startup) {
			// Randomly increase army for the country of player
			ArrayList<Country> playerCountries = game.getCurrentPlayer().getAssignedCountryList();

			int id = Common.getRandomNumberInRange(0, playerCountries.size() - 1);

			game.addArmyToCountry(playerCountries.get(id).getCountryName());
		}
	}

	/**
	 * Test Method for Reinforcement for Benevolent Player
	 */
	@Test
	public void testReinforceBenevolent() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Benevolent();
		currentPlayer.setPlayerStrategy(playerStrategy);

		int minArmies = Integer.MAX_VALUE;
		ArrayList<Country> assignedCountryList = currentPlayer.getAssignedCountryList();
		for (Country c : assignedCountryList) {
			if (c.getnoOfArmies() < minArmies)
				minArmies = c.getnoOfArmies();
		}

		HashMap<Country, Integer> CountryArmyMap = new HashMap<Country, Integer>();
		ArrayList<Country> weakestCountries = new ArrayList<Country>();
		for (Country country : currentPlayer.getAssignedCountryList()) {
			if (country.getnoOfArmies() == minArmies) {
				weakestCountries.add(country);
				CountryArmyMap.put(country, country.getnoOfArmies());
			}
		}

		currentPlayer.setNoOfReinforcedArmies(weakestCountries.size());

		currentPlayer.reinforce();

		for (Country country : weakestCountries) {
			assertEquals(country.getnoOfArmies(), CountryArmyMap.get(country) + 1);
		}

		assertEquals(currentPlayer.getNoOfReinforcedArmies(), 0);

	}

	/**
	 * Test Method for Reinforcement for Cheater Player
	 */
	@Test
	public void testReinforcePhaseCheater() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Cheater();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<Country> assignedCountryList = currentPlayer.getAssignedCountryList();
		HashMap<Country, Integer> CountryArmyMap = new HashMap<Country, Integer>();

		for (Country country : assignedCountryList) {
			CountryArmyMap.put(country, country.getnoOfArmies());
		}

		currentPlayer.reinforce();

		for (Country country : assignedCountryList) {
			assertEquals(country.getnoOfArmies(), CountryArmyMap.get(country) * 2);
		}

	}

	/**
	 * Test Method for Reinforcement for Random Player
	 */
	@Test
	public void testReinforcePhaseRandom() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Random();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<Country> assignedCountryList = currentPlayer.getAssignedCountryList();
		int totalOldArmies = 0;
		for (Country country : assignedCountryList) {
			totalOldArmies = totalOldArmies + country.getnoOfArmies();
		}

		int reinforcedArmies = 5;

		currentPlayer.setNoOfReinforcedArmies(reinforcedArmies);
		assertEquals(currentPlayer.getNoOfReinforcedArmies(), reinforcedArmies);
		currentPlayer.reinforce();
		int totalNewArmies = 0;
		for (Country country : assignedCountryList) {
			totalNewArmies = totalNewArmies + country.getnoOfArmies();
		}

		assertEquals(totalOldArmies + reinforcedArmies, totalNewArmies);
		assertEquals(currentPlayer.getNoOfReinforcedArmies(), 0);

	}

	/**
	 * Test Method for Reinforcement for Aggressive Player
	 */
	@Test
	public void testReinforcePhaseAggressive() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Aggressive();
		currentPlayer.setPlayerStrategy(playerStrategy);

		Country strongestCountry = null;
		int armiesCount = 0;

		for (Country c : currentPlayer.getAssignedCountryList()) {
			if (c.getnoOfArmies() > armiesCount) {
				armiesCount = c.getnoOfArmies();
				strongestCountry = c;
			}
		}

		int oldArmiesCount = strongestCountry.getnoOfArmies();
		int reinforcedArmies = 5;
		currentPlayer.setNoOfReinforcedArmies(reinforcedArmies);
		assertEquals(currentPlayer.getNoOfReinforcedArmies(), reinforcedArmies);

		currentPlayer.reinforce();

		assertEquals(strongestCountry.getnoOfArmies(), oldArmiesCount + reinforcedArmies);
		assertEquals(currentPlayer.getNoOfReinforcedArmies(), 0);

	}

	/**
	 * Test Method for Reinforcement for Human Player
	 */
	@Test
	public void testReinforcePhaseHuman() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Human();
		currentPlayer.setPlayerStrategy(playerStrategy);

		Country country = currentPlayer.getAssignedCountryList().get(0);
		int oldArmiesCount = country.getnoOfArmies();
		currentPlayer.setToCountry(country);
		int reinforcedArmies = 5;
		currentPlayer.setNoOfReinforcedArmies(reinforcedArmies);
		assertEquals(currentPlayer.getNoOfReinforcedArmies(), reinforcedArmies);

		currentPlayer.reinforce();
		assertEquals(country.getnoOfArmies(), oldArmiesCount + 1);
		assertEquals(currentPlayer.getNoOfReinforcedArmies(), reinforcedArmies - 1);

	}

	/**
	 * Test Method for Attack for Benevolent Player
	 */
	@Test
	public void testAttackPhaseBenevolent() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Benevolent();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<Country> assignedCountryList = currentPlayer.getAssignedCountryList();
		int totalOldArmies = 0;
		for (Country country : assignedCountryList) {
			totalOldArmies = totalOldArmies + country.getnoOfArmies();
		}

		currentPlayer.attackPhase();

		int totalNewArmies = 0;
		for (Country country : assignedCountryList) {
			totalNewArmies = totalNewArmies + country.getnoOfArmies();
		}

		assertEquals(totalOldArmies, totalNewArmies);

	}

	/**
	 * Test Method for Attack for Cheater Player
	 */
	@Test
	public void testAttackPhaseCheater() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Cheater();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<Country> assignedCountries = currentPlayer.getAssignedCountryList();
		HashMap<Country, Integer> neighbourCountries = new HashMap<Country, Integer>();

		for (Country country : assignedCountries) {
			if (!neighbourCountries.containsKey(country)
					&& country.getPlayer().getPlayerId() != currentPlayer.getPlayerId())
				neighbourCountries.put(country, country.getnoOfArmies());
		}

		currentPlayer.attackPhase();

		for (Country country : neighbourCountries.keySet()) {
			assertEquals(currentPlayer.getPlayerId(), country.getPlayer().getPlayerId());
		}

	}

	/**
	 * Test Method for Attack for Random Player
	 */
	@Test
	public void testAttackPhaseRandom() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Random();
		currentPlayer.setPlayerStrategy(playerStrategy);

		currentPlayer.setIsConquered(false);
		currentPlayer.attackPhase();

		if (currentPlayer.isConquered()) {
			Country fromCountry = currentPlayer.getFromCountry();
			Country toCountry = currentPlayer.getToCountry();

			assertEquals(currentPlayer.getPlayerId(), toCountry.getPlayer().getPlayerId());
		}

	}

	/**
	 * Test Method for Attack for Aggressive Player
	 */
	@Test
	public void testAttackPhaseAggressive() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Aggressive();
		currentPlayer.setPlayerStrategy(playerStrategy);

		Country strongestCountry = null;
		int armiesCount = 0;
		for (Country c : currentPlayer.getAssignedCountryList()) {
			if (c.getnoOfArmies() > armiesCount) {
				armiesCount = c.getnoOfArmies();
				strongestCountry = c;
			}
		}

		ArrayList<Country> CountriesToAttack = currentPlayer
				.getUnAssignedNeighbouringCountriesObject(strongestCountry.getCountryName());

		currentPlayer.setIsConquered(false);
		currentPlayer.attackPhase();

		if (currentPlayer.isConquered()) {
			Country fromCountry = currentPlayer.getFromCountry();
			Country toCountry = currentPlayer.getToCountry();
			assertEquals(currentPlayer.getPlayerId(), toCountry.getPlayer().getPlayerId());
		} else {
			for (Country country : CountriesToAttack) {
				assertNotEquals(currentPlayer.getPlayerId(), country.getPlayer().getPlayerId());
			}
		}

	}

	/**
	 * Test Method for Attack for Human Player
	 */
	@Test
	public void testAttackPhaseHuman() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Human();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<String> attackingCountryList = currentPlayer.getCountriesWithArmiesGreaterThanOne();
		ArrayList<String> attackedCountryList;
		Country attackingCountry, defendingCountry;
		int attackingDiceCount, defendingDiceCount, attackingCountryArmyCount, defendingCountryArmyCount;
		Player defenderPlayer;

		for (String attackingCountryName : attackingCountryList) {
			attackedCountryList = currentPlayer.getUnAssignedNeighbouringCountries(attackingCountryName);
			attackingCountry = game.getCountryFromName(attackingCountryName);
			attackingCountryArmyCount = attackingCountry.getnoOfArmies();
			for (String attackedCountryName : attackedCountryList) {
				defendingCountry = game.getCountryFromName(attackedCountryName);
				defenderPlayer = defendingCountry.getPlayer();

				defendingCountryArmyCount = defendingCountry.getnoOfArmies();

				attackingDiceCount = 1;
				defendingDiceCount = 1;

				currentPlayer.setFromCountry(attackingCountry);
				currentPlayer.setToCountry(defendingCountry);
				currentPlayer.setAttackedPlayer(defenderPlayer);
				currentPlayer.rollDice(attackingDiceCount);
				defenderPlayer.rollDice(defendingDiceCount);

				currentPlayer.attackPhase();

				if (defendingCountryArmyCount > defendingCountry.getnoOfArmies()) {
					assertEquals(defendingCountryArmyCount, defendingCountry.getnoOfArmies() + 1);
					assertEquals(attackingCountryArmyCount, attackingCountry.getnoOfArmies());
				} else if (attackingCountryArmyCount > attackingCountry.getnoOfArmies()) {
					if (currentPlayer.isConquered()) {
						assertEquals(defendingCountry.getnoOfArmies(), 1);
						assertEquals(attackingCountryArmyCount, attackingCountry.getnoOfArmies() + 1);
						assertEquals(defendingCountry.getPlayer().getPlayerId(), currentPlayer.getPlayerId());
					} else {
						assertEquals(defendingCountryArmyCount, defendingCountry.getnoOfArmies());
						assertEquals(attackingCountryArmyCount, attackingCountry.getnoOfArmies() + 1);
					}

				}
				break;
			}
			break;
		}
	}

	/**
	 * Test Method for Fortification for Benevolent Player
	 */
	@Test
	public void testFortificationPhaseBenevolent() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Benevolent();
		currentPlayer.setPlayerStrategy(playerStrategy);

	}

	/**
	 * Test Method for Fortification for Random Player
	 */
	@Test
	public void testFortificationPhaseRandom() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Random();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<Country> sourceCountryList = currentPlayer.getCountriesObjectWithArmiesGreaterThanOne();
		HashMap<Country, Integer> neighbourCountryArmyMap = new HashMap<Country, Integer>();
		ArrayList<Country> neigbouringCountries = null;
		int oldArmiesCount = 0;

		for (Country country : sourceCountryList) {
			neigbouringCountries = currentPlayer.getConnectedCountriesRecursively(country,
					(ArrayList<Country>) currentPlayer.getAssignedCountryList().clone(), new ArrayList<Country>());
			if (!neighbourCountryArmyMap.containsKey(country)) {
				neighbourCountryArmyMap.put(country, country.getnoOfArmies());
				oldArmiesCount = oldArmiesCount + country.getnoOfArmies();
			}
			for (Country neighbourCountry : neigbouringCountries) {
				if (!neighbourCountryArmyMap.containsKey(neighbourCountry)) {
					neighbourCountryArmyMap.put(neighbourCountry, neighbourCountry.getnoOfArmies());
					oldArmiesCount = oldArmiesCount + neighbourCountry.getnoOfArmies();
				}
			}
		}

		currentPlayer.fortificationPhase();

		int newArmyCount = 0;
		for (Country country : neighbourCountryArmyMap.keySet()) {
			newArmyCount = newArmyCount + country.getnoOfArmies();
		}

		assertEquals(oldArmiesCount, newArmyCount);

	}

	/**
	 * Test Method for Fortification for Cheater Player
	 */
	@Test
	public void testFortificationPhaseCheater() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Cheater();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<Country> assignedCountryList = currentPlayer.getAssignedCountryList();
		HashMap<Country, Integer> neighbourCountryArmyMap = new HashMap<Country, Integer>();

		for (Country country : assignedCountryList) {
			ArrayList<Country> assignedCountryListTemp = currentPlayer
					.getUnAssignedNeighbouringCountriesObject(country.getCountryName());
			for (Country neighbourCountry : assignedCountryListTemp) {
				if (neighbourCountry.getPlayer().getPlayerId() == currentPlayer.getPlayerId()
						&& !neighbourCountryArmyMap.containsKey(neighbourCountry)) {
					neighbourCountryArmyMap.put(neighbourCountry, neighbourCountry.getnoOfArmies());
				}
			}
		}

		currentPlayer.fortificationPhase();

		Iterator it = neighbourCountryArmyMap.entrySet().iterator();

		while (it.hasNext()) {
			HashMap.Entry<Country, Integer> pair = (HashMap.Entry<Country, Integer>) it.next();
			assertEquals((Integer) pair.getValue() * 2, pair.getKey().getnoOfArmies());

		}
	}

	/**
	 * Test Method for Fortification for Aggressive Player
	 */
	@Test
	public void testFortificationPhaseAggressive() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Aggressive();
		currentPlayer.setPlayerStrategy(playerStrategy);

		ArrayList<Country> assignedCountryList = currentPlayer.getAssignedCountryList();
		HashMap<Country, Integer> neighbourCountryArmyMap = new HashMap<Country, Integer>();
		ArrayList<Country> neighborCountries = null;

		int oldArmiesCount = 0;
		for (Country c : assignedCountryList) {
			if (!neighbourCountryArmyMap.containsKey(c)) {

				neighbourCountryArmyMap.put(c, c.getnoOfArmies());
				oldArmiesCount = oldArmiesCount + c.getnoOfArmies();
			}
			neighborCountries = currentPlayer.getConnectedCountriesRecursively(c,
					(ArrayList<Country>) currentPlayer.getAssignedCountryList().clone(), new ArrayList<Country>());

			for (Country c2 : neighborCountries) {
				if (!neighbourCountryArmyMap.containsKey(c2)) {
					neighbourCountryArmyMap.put(c2, c2.getnoOfArmies());
					oldArmiesCount = oldArmiesCount + c2.getnoOfArmies();
				}
			}

		}

		currentPlayer.fortificationPhase();

		int newArmiesCount = 0;
		Iterator it = neighbourCountryArmyMap.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry<Country, Integer> pair = (HashMap.Entry<Country, Integer>) it.next();
			newArmiesCount = newArmiesCount + pair.getKey().getnoOfArmies();
		}

		assertEquals(oldArmiesCount, newArmiesCount);

	}

	/**
	 * Test Method for Fortification for Human Player
	 */
	@Test
	public void testFortificationPhaseHuman() {
		Player currentPlayer = game.getCurrentPlayer();
		PlayerStrategy playerStrategy = new Human();
		currentPlayer.setPlayerStrategy(playerStrategy);
		ArrayList<Country> fromCountryList = currentPlayer.getCountriesObjectWithArmiesGreaterThanOne();
		Country fromCountry = fromCountryList.get(1);

		for (int i = 0; i < fromCountryList.size(); i++) {
			fromCountry = fromCountryList.get(i);
			if (currentPlayer.getAssignedNeighbouringCountries(fromCountry.getCountryName()).size() > 1) {
				break;
			}
		}

		ArrayList<Country> toCountryList = fromCountry.getNeighbourCountries();

		Country toCountry = null;
		for (int i = 0; i < toCountryList.size(); i++) {
			if (currentPlayer.getPlayerId() == toCountryList.get(i).getPlayer().getPlayerId()) {
				toCountry = toCountryList.get(i);
				break;
			}
		}
		if (fromCountry != null && toCountry != null) {
			int fromCountryArmyCount = fromCountry.getnoOfArmies();
			int noOfArmiesToMove = fromCountryArmyCount - 1;
			int toCountryArmyCount = toCountry.getnoOfArmies();
			currentPlayer.setFromCountry(fromCountry);
			currentPlayer.setToCountry(toCountry);
			currentPlayer.setNoOfArmiesToMove(noOfArmiesToMove);
			currentPlayer.fortificationPhase();

			assertEquals(fromCountry.getnoOfArmies(), 1);
			assertEquals(toCountryArmyCount + noOfArmiesToMove, toCountry.getnoOfArmies());
		}
	}

	/**
	 * This is used to test Move armies after attack
	 */
	@Test
	public void moveArmiesAfterAttackTest() {
		Player currentPlayer = game.getCurrentPlayer();
		ArrayList<String> attackingCountryList = currentPlayer.getCountriesWithArmiesGreaterThanOne();
		ArrayList<String> attackedCountryList;
		Country attackingCountry, defendingCountry;
		int attackingCountryArmyCount, defendingCountryArmyCount;
		Player defenderPlayer;
		for (String attackingCountryName : attackingCountryList) {
			attackedCountryList = currentPlayer.getUnAssignedNeighbouringCountries(attackingCountryName);
			attackingCountry = game.getCountryFromName(attackingCountryName);
			attackingCountry.setNoOfArmies(5);
			attackingCountryArmyCount = attackingCountry.getnoOfArmies();

			for (String attackedCountryName : attackedCountryList) {
				defenderPlayer = game.getAllPlayers().stream()
						.filter(p -> p.getAssignedCountryList().contains(attackedCountryName)).findAny().orElse(null);
				defendingCountry = game.getCountryFromName(attackedCountryName);
				defendingCountry.setNoOfArmies(1);
				defendingCountryArmyCount = defendingCountry.getnoOfArmies();

				currentPlayer.setToCountry(defendingCountry);
				currentPlayer.setFromCountry(attackingCountry);
				currentPlayer.setIsConquered(true);
				currentPlayer.moveArmyAfterAttack(3);
				assertEquals(defendingCountryArmyCount + 3, defendingCountry.getnoOfArmies());
				assertEquals(attackingCountryArmyCount - 3, attackingCountry.getnoOfArmies());
				break;
			}
			break;
		}

	}

	/**
	 * This will test getMaximumAllowableDices
	 */
	@Test
	public void getMaximumAllowableDicesTest() {
		Player player = game.getCurrentPlayer();
		String countryName = player.getCountriesWithArmiesGreaterThanOne().get(0);
		Country country = game.getCountryFromName(countryName);
		country.setNoOfArmies(5);
		game.setGamePhase(PhaseEnum.Attack);

		int diceCount = player.getMaximumAllowableDices(country, "Attacker");
		assertEquals(3, diceCount);

		country.setNoOfArmies(2);
		diceCount = player.getMaximumAllowableDices(country, "Attacker");

		assertEquals(1, diceCount);
	}

	/**
	 * This will test getAllowableArmiesMoveFromAttackerToDefender
	 */
	@Test
	public void getAllowableArmiesMoveFromAttackerToDefenderTest() {
		Player player = game.getCurrentPlayer();
		String countryName = player.getCountriesWithArmiesGreaterThanOne().get(0);
		Country country = game.getCountryFromName(countryName);
		country.setNoOfArmies(5);
		player.setFromCountry(country);
		player.setIsConquered(true);
		int armies = player.getAllowableArmiesMoveFromAttackerToDefender();
		assertEquals(4, armies);

	}

	@Test
	public void testSetReinforcementArmies() {
		int MINIMUM_REINFORCEMENT_PLAYERS = 3;
		game.getCurrentPlayer().addCardToPlayer(cardEnum.Infantry);
		game.getCurrentPlayer().addCardToPlayer(cardEnum.Artillery);
		game.getCurrentPlayer().addCardToPlayer(cardEnum.Cavalry);
		game.getCurrentPlayer().addCardToPlayer(cardEnum.Cavalry);
		PlayerStrategy strategy = new Human();
		game.getCurrentPlayer().setPlayerStrategy(strategy);
		String strategyName = game.getCurrentPlayer().getPlayerStrategy().getStrategyName();
		int cardSize = game.getCurrentPlayer().getCards().size();
		boolean allowed = game.getCurrentPlayer().isAssigningReinforcementArmiesAllowed();
		game.getCurrentPlayer().setNoOfTradedArmies(3);
		int countriesCount = (int) Math.floor(game.getCurrentPlayer().getAssignedCountryList().size() / 3);

		List<Integer> assignedCountryIds = game.getCurrentPlayer().getAssignedCountryList().stream()
				.map(c -> c.getCountryId()).collect(Collectors.toList());

		for (Continent continent : map.getContinentList()) {
			List<Integer> continentCountryIds = continent.getCountryList().stream().map(c -> c.getCountryId())
					.collect(Collectors.toList());
			boolean hasPlayerAllCountries = assignedCountryIds.containsAll(continentCountryIds);
			if (hasPlayerAllCountries)
				countriesCount += continent.getControlValue();
		}

		countriesCount += game.getCurrentPlayer().getNoOfTradedArmies();
		countriesCount = countriesCount < MINIMUM_REINFORCEMENT_PLAYERS ? MINIMUM_REINFORCEMENT_PLAYERS
				: countriesCount;
		game.getCurrentPlayer().setNoOfReinforcedArmies(countriesCount);

		if (!allowed && strategyName == "Human") {
			boolean value = game.getCurrentPlayer().setReinformcementArmies(map.getContinentList());
			assertEquals(value, false);
		} else {
			boolean value = game.getCurrentPlayer().setReinformcementArmies(map.getContinentList());
			assertEquals(value, true);
		}

	}

	/**
	 * Test Method to add Army to Country for Startup Phase
	 */
	@Test
	public void addArmyToCountryForStartup() {
		Player player = game.getCurrentPlayer();
		String countryName = player.getCountriesWithArmiesGreaterThanOne().get(0);
		Country country = game.getCountryFromName(countryName);
		country.setNoOfArmies(5);
		game.setGamePhase(PhaseEnum.Startup);
		player.setNoOfUnassignedArmies(10);
		int unassignedArmyCountOld = player.getNoOfUnassignedArmies();
		int countryAssignedArmyCountOld = country.getnoOfArmies();

		Boolean result = player.addArmyToCountryForStartup(countryName);

		int unassignedArmyCountNew = player.getNoOfUnassignedArmies();
		int countryAssignedArmyCountNew = country.getnoOfArmies();

		assertEquals(result, true);
		assertEquals(unassignedArmyCountNew, unassignedArmyCountOld - 1);
		assertEquals(countryAssignedArmyCountNew, countryAssignedArmyCountOld + 1);

	}

	/**
	 * This method will tear down variables.
	 */
	@After
	public void tearDown() {
		map = null;
		game = null;
		game2 = null;
		mapToTest = null;
		playerCount = null;
		p1 = null;
		c1 = null;
		c2 = null;

	}

}