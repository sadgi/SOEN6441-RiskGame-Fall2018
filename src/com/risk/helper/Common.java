package com.risk.helper;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author sadgi
 *
 */
public class Common {

	/**
	 * Method to convert the enum color into color object of swings
	 * @param selectedColor
	 * @return Color based on the objects
	 */
	public static Color getColor(EnumColor selectedColor) {
		if (selectedColor.equals(EnumColor.BLACK)) {
			return Color.BLACK;
		}
		if (selectedColor.equals(EnumColor.RED)) {
			return Color.RED;
		}
		if (selectedColor.equals(EnumColor.GREEN)) {
			return Color.GREEN;
		}
		if (selectedColor.equals(EnumColor.BLUE)) {
			return Color.BLUE;
		}
		if (selectedColor.equals(EnumColor.GRAY)) {
			return Color.GRAY;
		}

		if (selectedColor.equals(EnumColor.YELLOW)) {
			return Color.yellow;
		}
		return null;

	}

	/**
	 * This will generate the random integers between min(inclusive) and
	 * max(inclusive)
	 * 
	 * @param min
	 *            integer
	 * @param max
	 *            integer
	 * @return random integer
	 */
	public static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("Max value must be greater than Min value!");
		}
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

}
