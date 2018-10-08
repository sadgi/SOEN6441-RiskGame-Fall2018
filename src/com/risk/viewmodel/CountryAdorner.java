package com.risk.viewmodel;

import javax.swing.JLabel;

import com.risk.helper.EnumColor;
import com.risk.model.Country;

public class CountryAdorner extends Country {
	public CountryAdorner(int countryId, String countryName) {
		super(countryId, countryName);
	}

	private EnumColor playerColor;
	private int NoOfArmies;
	private JLabel pointInMapLabel = null;
	
	public EnumColor getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(EnumColor playerColor) {
		this.playerColor = playerColor;
	}
	
	public int getNoOfArmies() {
		return NoOfArmies;
	}

	public void setNoOfArmies(int noOfArmies) {
		NoOfArmies = noOfArmies;
	}

	public void setPointInMapLabel(JLabel pointInMapLabel) {
	    this.pointInMapLabel = pointInMapLabel;
	  }
	 
    public JLabel getPointInMapLabel() {
	    return pointInMapLabel;
	  }
	
}