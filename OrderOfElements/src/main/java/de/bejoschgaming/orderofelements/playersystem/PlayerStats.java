package de.bejoschgaming.orderofelements.playersystem;

import java.awt.Color;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;

public class PlayerStats {

	private int level = 1, XP = 0;
	private int wins = 0, loses = 0, playedGames = 0;
	private int winstreak = 0; //CAN BE NEGATIVE FOR LOSESTREAK
	private RankingType ranking = RankingType.UNRANKED;
	private int crowns = 0;
	private Color displayColor = Color.getColor("WHITE");
	private String titel = "None";
	
	private String status = "Offline";
	
	private int playerID;
	
	public PlayerStats(int playerID) {
		
		this.playerID = playerID;
		this.updateDataFromDB();
		
	}
	
	public void updateDataFromDB() {
		
		this.level = DatabaseHandler.selectInt(DatabaseHandler.tabellName_playerStats, "Level", "ID", ""+playerID);
		this.XP = DatabaseHandler.selectInt(DatabaseHandler.tabellName_playerStats, "XP", "ID", ""+playerID);
		this.wins = DatabaseHandler.selectInt(DatabaseHandler.tabellName_playerStats, "Siege", "ID", ""+playerID);
		this.loses = DatabaseHandler.selectInt(DatabaseHandler.tabellName_playerStats, "Niederlagen", "ID", ""+playerID);
		this.playedGames = this.wins+this.loses;
		this.winstreak = DatabaseHandler.selectInt(DatabaseHandler.tabellName_playerStats, "Winstreak", "ID", ""+playerID);
		this.ranking = RankingType.valueOf(DatabaseHandler.selectString(DatabaseHandler.tabellName_playerStats, "Rang", "ID", ""+playerID));
		this.crowns = DatabaseHandler.selectInt(DatabaseHandler.tabellName_playerStats, "Kronen", "ID", ""+playerID);
		this.displayColor = Color.getColor(DatabaseHandler.selectString(DatabaseHandler.tabellName_playerStats, "AnzeigeFarbe", "ID", ""+playerID));
		this.titel = DatabaseHandler.selectString(DatabaseHandler.tabellName_playerStats, "Titel", "ID", ""+playerID);
		
	}
	
	public void updateStatus(String newStatus) {
		
		this.status = newStatus;
		
	}
	
	public String getDataAsString(String splitSymbol) {
		
		//LEVEL;XP;WINS;LOSES;GAMES;WINSTREAK;RANKING;CROWNS;COLOR;TITEL;STATUS
		//EXAMPLE with split=";" - 8;62;1;3;4;-2;PAWN;12;WHITE;Veteran;InGame
		return level+splitSymbol+XP+splitSymbol+wins+splitSymbol+loses+splitSymbol+playedGames+splitSymbol+winstreak+splitSymbol+ranking+splitSymbol+crowns+splitSymbol+displayColor+splitSymbol+titel+splitSymbol+status;
		
	}

	public int getLevel() {
		return level;
	}

	public int getXP() {
		return XP;
	}
	
	public int getWins() {
		return wins;
	}

	public int getLoses() {
		return loses;
	}

	public int getPlayedGames() {
		return playedGames;
	}

	public int getWinstreak() {
		return winstreak;
	}

	public RankingType getRanking() {
		return ranking;
	}

	public int getCrowns() {
		return crowns;
	}

	public Color getDisplayColor() {
		return displayColor;
	}

	public String getTitel() {
		return titel;
	}
	
	public String getStatus() {
		return status;
	}
	
}
