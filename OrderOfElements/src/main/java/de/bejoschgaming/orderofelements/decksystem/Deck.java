package de.bejoschgaming.orderofelements.decksystem;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;

public class Deck {

	private int deckID;
	private int ownerID;
	private String name = "LOADING";
	private String data = "LOADING"; //IF DATA IS TO LONG TO SEND IN ONE PACKAGE THEN SPLIT IT IN PARTS WITH SUBSTRING OR SPLIT AND LABEL THE PACKAGE ORDER
	
	public Deck(int deckID, int ownerID) {
		
		this.deckID = deckID;
		this.ownerID = ownerID;
		
		this.name = DatabaseHandler.selectString(DatabaseHandler.tabellName_decks, "Name", "ID", ""+this.deckID);
		this.data = DatabaseHandler.selectString(DatabaseHandler.tabellName_decks, "Data", "ID", ""+this.deckID);
		
	}
	
	public int getDeckID() {
		return deckID;
	}
	public int getOwnerID() {
		return ownerID;
	}
	public String getName() {
		return name;
	}
	public String getData() {
		return data;
	}
	
}
