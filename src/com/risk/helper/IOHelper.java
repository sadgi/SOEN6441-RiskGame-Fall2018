package com.risk.helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Helper class to print data
 * 
 * @author sadgi
 *
 */
public class IOHelper {

	public static Logger logger = Logger.getLogger("IOHelper");
	public static FileHandler fileHandler;

	static {
		try {
			//Logger logger = Logger.getLogger("IOHelper");
			FileHandler fileHandler;
			String timeStamp = new SimpleDateFormat().format(new Date());
			CustomLogFormatter formatter = new CustomLogFormatter();

			fileHandler = new FileHandler("assets/Logs.txt");
			logger.addHandler(fileHandler);
			fileHandler.setFormatter(formatter);
			fileHandler.setLevel(Level.INFO);
			logger.setUseParentHandlers(false);
			String today= Calendar.getInstance()
					.getTime()
					.toString()
					.replaceAll(" ", "_")
					.replaceAll(":", "");
			System.setProperty("logfilename", today);
			logger.info("Log File created - TimeStamp : "+timeStamp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to print output on the command line
	 * 
	 * @param string,
	 *            string parameter to print
	 */
	public static void print(String string) {
		System.out.println(string);
		logger.setUseParentHandlers(false);
		logger.log(Level.INFO,string);
		Common.PhaseActions.add(string);
	}

	static Scanner sc = new Scanner(System.in);

	/**
	 * Method to take string input from user on command line
	 * 
	 * @return string input
	 */
	public static String getNextString() {
		return sc.nextLine();
	}

	/**
	 * Method to take Integer input from user on command line
	 * 
	 * @return int, next integer
	 */
	public static int getNextInteger() {
		String s = sc.nextLine();
		return Integer.parseInt(s);
	}

	/**
	 * Method to print and handle Exceptions
	 * 
	 * @param exception,object
	 *            of exception class
	 */
	public static void printException(Exception exception) {
		System.out.println("Exception: " + exception.getMessage());
	}
}
