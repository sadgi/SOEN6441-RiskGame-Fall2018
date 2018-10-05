package com.risk.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.risk.helper.IOHelper;


/**
 * Map Class
 * 
 * @author sadgi
 * @version 1.0.0
 * @since 27-September-2018
 */
public class Map {
	Map map;
	private String mapName;
	private String mapPath = "assets/maps/";
	private ArrayList<Continent> continentsList = new ArrayList<>();
	private ArrayList<String> visitedList = new ArrayList<String>();

	/**
	 * This is a constructor of Map Class which sets mapId and mapName.
	 * 
	 * //@param mapName
	 */
	public Map() {
		super();
	}

	/**
	 * getters and setters
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * This function sets the map name.
	 */
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	/**
	 * This function loads the Map into the memory
	 */
	public void readMap() {

		try {
			boolean captureContinents = false;
			boolean captureCountries = false;
			File file = new File(mapPath + mapName);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String readLine;
			int continentID = 0;
			int countryID = 0;

			while ((readLine = bufferedReader.readLine()) != null) {
				IOHelper.print(readLine);
				if (readLine.trim().length() == 0)
					continue;
				else if ((readLine.trim()).equals("[Continents]")) {
					captureContinents = true;
					continue;
				} else if ((readLine.trim()).equals("[Territories]")) {
					captureContinents = false;
					captureCountries = true;
					continue;
				}

				if (captureContinents) {
					String[] parsedControlValuesByContinentsArray = readLine.split("=");
					Continent continent = new Continent(continentID++, parsedControlValuesByContinentsArray[0],
							Integer.parseInt(parsedControlValuesByContinentsArray[1]));
					continentsList.add(continent);
				} else if (captureCountries) {
					String[] parsedTerritoriesArray = readLine.split(",");
					String continentName = parsedTerritoriesArray[3];
					Country country = new Country(countryID++, parsedTerritoriesArray[0]);
					country.setxCoordiate(Integer.parseInt(parsedTerritoriesArray[1]));
					country.setyCoordiate(Integer.parseInt(parsedTerritoriesArray[2]));
					int k = 0;
					for (String neighborCountry : parsedTerritoriesArray) {
						if (k > 3) {
							country.addNeighboursString(neighborCountry);
						}
						k++;
					}

					for (int i = 0; i < continentsList.size(); i++) {
						if (continentsList.get(i).getContName().equals(continentName)) {
							continentsList.get(i).addCountry(country);
							country.setContId(continentsList.get(i).getContId());
							break;
						}
					}
				}
			}
			bufferedReader.close();
		} catch (Exception e) {
			IOHelper.printException(e);
		}

	}
	/**
	 * This adds a continent to the continent list
	 * 
	 * @param Continent 
	 */
	public void addContinent(Continent continent) {
		continentsList.add(continent);
	}

	/**
	 * This function deletes the Continent from the Map.
	 */
	public void deleteContinent(String continentToDelete){

		ArrayList<Country> countriesListOfCurrentContinent = new ArrayList<>();
		Continent currentContinent = continentsList.stream()
								.filter(x-> x.getContName().equalsIgnoreCase(continentToDelete))
								.findAny()
								.orElse(null);

		countriesListOfCurrentContinent = currentContinent.getCountryList();
		for ( Continent continent: continentsList){
			for (Country country : continent.getCountryList()) {
				//IOHelper.print("neighbour"+country.getNeighboursString());
				for (int i = 0; i < country.getNeighboursString().size() ; i++) {
					if (country.getNeighboursString().get(i).equalsIgnoreCase(countriesListOfCurrentContinent.get(i).getCountryName())){
						//IOHelper.print("got neighbour");
						country.getNeighboursString().remove(i);
					}
					else{
						//IOHelper.print("neighbor not found");
					}
				}
			}
		}
		continentsList.remove(currentContinent);

		this.getContinentList();
	}
	/**
	 * This function checks and returns if the map is a valid map or not.
	 * 
	 * return true
	 */
	public boolean isMapValid() {
		boolean oneCountryInTwoContinents = false;
		boolean atLeastOneCountryInAllContinents = true;
		ArrayList<String> listOfAllCountries = new ArrayList<String>();
		ArrayList<String> listOfMainCountries = new ArrayList<String>();
		for (Continent singleContinent : this.continentsList) {
			if(singleContinent.getCountryList().isEmpty()) {
				atLeastOneCountryInAllContinents = false;
			}
			for (Country singleCountry : singleContinent.getCountryList()) {
				if (!listOfAllCountries.contains(singleCountry.getCountryName())) {
					listOfAllCountries.add(singleCountry.getCountryName());
				}
				if (listOfMainCountries.contains(singleCountry.getCountryName())) {
					oneCountryInTwoContinents = true;
					if(oneCountryInTwoContinents) {
						System.out.println("Same country cannot be in two continents.");
						return false;
					}
				}else {
					listOfMainCountries.add(singleCountry.getCountryName());
				}
				for (String eachNeighbourCountry : singleCountry.getNeighboursString()) {
					if (listOfAllCountries.contains(eachNeighbourCountry)) {
						// nada
					} else {
						listOfAllCountries.add(eachNeighbourCountry);
					}
				}
			}
		}
		Collections.sort(listOfAllCountries);

		Country sourceCountry = ((this.continentsList.get(0)).getCountryList()).get(0);
		DfsRecursive(sourceCountry);
		// 1.check if the graph is connected or not
		Collections.sort(visitedList);
		if(!atLeastOneCountryInAllContinents) {
			System.out.println("Each continent should have atleast one country");
			return false;
		}
		if (isTwoArrayListsWithSameValues(visitedList, listOfAllCountries)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Checks if two array lists are same or not
	 * 
	 * @param list arraylist1
	 * @param list arraylist2
	 * 
	 * @return boolean
	 */
	public boolean isTwoArrayListsWithSameValues(ArrayList<String> list1, ArrayList<String> list2) {
		if (list1 == null && list2 == null)
			return true;
		if ((list1 == null && list2 != null) || (list1 != null && list2 == null))
			return false;

		if (list1.size() != list2.size())
			return false;
		for (String itemList1 : list1) {
			if (!list2.contains(itemList1))
				return false;
		}
		return true;
	}
	/**
	 * This function checks if the map is connected or not ; it is a recursive funciton.
	 * 
	 * @param Country
	 */
	public void DfsRecursive(Country sourceCountry) {
		visitedList.add(sourceCountry.getCountryName());
		for (String neighbourCountry : sourceCountry.getNeighboursString()) {
			if (visitedList.contains(neighbourCountry)) {
				// nada
			} else {
				Country countryyy = null;
				for (Continent cont : this.continentsList) {
					for (Country countryy : cont.getCountryList()) {
						if (countryy.getCountryName().equals(neighbourCountry)) {
							countryyy = countryy;
						}
					}
				}
				if (countryyy != null) {
					DfsRecursive(countryyy);
				}
			}
		}
	}
	/**
	 * This function saves the map into the disk.
	 * 
	 */
	public void saveMap() {
		StringBuffer content = new StringBuffer();
		content.append("[Continents]\r\n");
		for (Continent induvidualContinentObject : this.continentsList) {
			content.append(induvidualContinentObject.getContName() + "=" + induvidualContinentObject.getControlValue()
					+ "\r\n");
		}
		content.append("\n[Territories]\r\n");
		for (Continent induvidualContinentObject : this.continentsList) {
			for (Country induvidualCountry : induvidualContinentObject.getCountryList()) {
				content.append(induvidualCountry.getCountryName() + "," + induvidualCountry.getxCoordiate() + ","
						+ induvidualCountry.getyCoordiate() + "," + induvidualContinentObject.getContName());
				for (String neighbouringCountry : induvidualCountry.getNeighboursString()) {
					content.append("," + neighbouringCountry);
				}
				content.append("\r\n");
			}
		}
		writeMapToDisk(content, this.mapName);

		System.out.print(content);
		
	}
	/**
	 * This writes the content to the disk with the name passed to the funciton.
	 * 
	 * @param String content
	 * @param String nameOfTheMap
	 */
	public void writeMapToDisk(StringBuffer content, String nameOfTheMap) {
		final Path path = Paths.get(this.mapPath + nameOfTheMap + ".map");
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			writer.write(new String(content));
			writer.close();
			System.out.println("\nFile Saved");
		} catch (Exception e) {
			IOHelper.printException(e);
		}
	}
	/**
	 * This function returns the country list.
	 * 
	 * @param ArrayList
	 */
	public ArrayList<Country> getCountryList()
	{
		ArrayList<Country> countriesList = new ArrayList<>();
		for(Iterator<Continent> continents= continentsList.iterator(); continents.hasNext(); )
		{
			countriesList.addAll(continents.next().getCountryList());
		}
		return countriesList;
	}
	/**
	 * This function returns the continent list.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Continent> getContinentList()
	{
		return continentsList;
	}

	/**
	 *
	 * This function deletes the Country present in the map.
	 */
	public void deleteCountry(String countryToDelete) {
		int index=0;
		ArrayList<Country> countriesList = map.getCountryList();
		for (Country country: countriesList) {

		}


		/*int index=0;
		for(int i=0;i<countriesList.size();i++){
			if(countriesList.get(i).getCountryName().equals(countryToDelete)){
				index=i;
			}
		}
		if (index!=0)
			countriesList.remove(index);
		else
			IOHelper.print("Country not found!");*/

	}

	public String getMapPath() {
		return mapPath;
	}

	public void setMapPath(String mapPath) {
		this.mapPath = mapPath;
	}
}
