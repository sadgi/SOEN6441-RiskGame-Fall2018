package com.risk.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;

/**
 * This class will test the country methods.
 */
public class CountryTest {
	Country c1;
	String neighbourToadd;
	ArrayList<String> neighbourStringList;

	/**
	 * This method will setup the variables for the test methods.
	 */
	@Before
	public void setUp() {
		c1 = new Country(12, "TheBestCountry", 2);
		neighbourToadd = "neighbourCountry";
	}

	/**
	 * This method tests if the addneighbour method actually adds the country
	 * into the list or not.
	 */
	@Test
	public void checkAddNeighbour() {
		c1.addNeighboursString(neighbourToadd);
		neighbourStringList = c1.getNeighboursString();
		assertTrue(neighbourStringList.contains(neighbourToadd));
	}

}