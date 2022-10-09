package de.bejoschgaming.orderofelements.playersystem;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

public class PlayerStats {

	private boolean loaded = false;
	
	private int level = 1, XP = 0;
	private int wins = 0, loses = 0, playedGames = 0;
	private int winstreak = 0; //CAN BE NEGATIVE FOR LOSESTREAK
	private RankingType ranking = RankingType.UNRANKED;
	private int crowns = 0;
	private String rawDisplayColorName = "WHITE";
	private Color displayColor = Color.getColor(this.rawDisplayColorName);
	private String titel = "None";
	private int maxDecks = 3;
	
	private String status = "Offline";
	
	private int playerID;
	
	public PlayerStats(int playerID) {
		
		this.playerID = playerID;
		this.updateDataFromDB();
		
	}
	
	public void updateDataFromDB() {
		
		ResultSet rs = DatabaseHandler.selectRawResultSet(DatabaseHandler.tabellName_playerStats, "*", "ID", ""+playerID);
		
		try {
			//SELECT FIRST IF POSSIBLE
			if(rs.first() == false) {
				//NO RESULTS
				ConsoleHandler.printMessageInConsole("Loaded playerstats with empty result! (ID: "+this.playerID+")", true);
				return;
			}
		} catch (SQLException error) {
			error.printStackTrace();
			ConsoleHandler.printMessageInConsole("Loaded playerstats with empty result! (ID: "+this.playerID+")", true);
			return;
		}
		
		try {
			//1 is ID
			this.level = rs.getInt(2);
			this.XP = rs.getInt(3);
			this.wins = rs.getInt(4);
			this.loses = rs.getInt(5);
			this.playedGames = this.wins+this.loses;
			this.winstreak = rs.getInt(6);
			this.ranking = RankingType.valueOf(rs.getString(7));
			this.crowns = rs.getInt(8);
			this.rawDisplayColorName = rs.getString(9);
			this.displayColor = Color.getColor(rawDisplayColorName, Color.PINK);
			this.titel = rs.getString(10);
			this.maxDecks = rs.getInt(11);
			this.loaded = true;
			rs.close();
		} catch (SQLException error) {
			error.printStackTrace();
		}
		
	}
	
	public void updateStatus(String newStatus) {
		
		this.status = newStatus;
		
	}
	
	public String getDataAsString(String splitSymbol) {
		
		//LEVEL;XP;WINS;LOSES;GAMES;WINSTREAK;RANKING;CROWNS;COLOR;TITEL;MAXDECKS;STATUS
		//EXAMPLE with split=";" - 8;62;1;3;4;-2;PAWN;12;WHITE;Veteran;3;InGame
		return level+splitSymbol+XP+splitSymbol+wins+splitSymbol+loses+splitSymbol+playedGames+splitSymbol+winstreak+splitSymbol+ranking+splitSymbol+crowns+splitSymbol+rawDisplayColorName+splitSymbol+titel+splitSymbol+maxDecks+splitSymbol+status;
		
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
	
	public boolean isLoaded() {
		return loaded;
	}
}
