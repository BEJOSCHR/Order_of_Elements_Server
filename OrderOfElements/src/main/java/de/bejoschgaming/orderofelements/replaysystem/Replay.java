package de.bejoschgaming.orderofelements.replaysystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

public class Replay {

	private int gameID;
	
	private List<GameAction> actions = new LinkedList<GameAction>();
	
	public Replay(int gameID) {
		
		this.gameID = gameID;
		
	}
	
	public void addAction(GameAction action) {
		
		this.actions.add(action);
		try {
			DatabaseHandler.insertData(DatabaseHandler.tabellName_replays, "GameID,Round,Turn,ActionType,ActionData", action.getGameID()+"','"+action.getRound()+"','"+action.getTurn()+"','"+action.getType()+"','"+action.getData());
		} catch (SQLException error) {
			error.printStackTrace();
			ConsoleHandler.printMessageInConsole("Couldn't add gameaction to replay DB! (ID: "+action.getGameID()+", Round: "+action.getRound()+", Turn: "+action.getTurn()+", Type: "+action.getType()+", Data: "+action.getData()+")", true);
		}
		
	}
	
	public void loadReplayData() {
		
		if(DatabaseHandler.connectedToDB == false) {
			ConsoleHandler.printMessageInConsole("Can't load replayData without DB connection!", true);
			return;
		}
		
		try {
			
			String query = "SELECT "+"*"+" FROM "+DatabaseHandler.tabellName_replays+" where ("+"GameID"+")=('"+this.gameID+"')";
			PreparedStatement stmt = DatabaseHandler.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			
			do {
				
				int round = rs.getInt("Round");
				int turn = rs.getInt("Turn");
				GameActionType type = GameActionType.valueOf(rs.getString("ActionType"));
				String data = rs.getString("ActionData");
				GameAction action = new GameAction(this.gameID, type, round, turn, data);
				this.actions.add(action);
				
			} while(rs.next());
			
			rs.close();
			stmt.close();
			
			ConsoleHandler.printMessageInConsole("Loaded ", true);
			
		} catch (SQLException error) {
			error.printStackTrace();
		}
		
	}
	
	public int getGameID() {
		return gameID;
	}
	public List<GameAction> getActions() {
		return actions;
	}
	
}
