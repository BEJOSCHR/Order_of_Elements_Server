package de.bejoschgaming.orderofelements.players;

public class PlayerProfile {

	private int ID;
	private String name;
	//...
	
	public PlayerProfile(int playerID, String playerName) {
		
		this.ID = playerID;
		this.name = playerName;
		
		loadPlayerData();
		
	}
	
	private void loadPlayerData() {
		
		//LOAD STATS ETC FROM DB
		
	}



	public int getID() {
		return ID;
	}
	public String getName() {
		return name;
	}
	
}
