package de.bejoschgaming.orderofelements.gamesystem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.mapsystem.Map;
import de.bejoschgaming.orderofelements.replaysystem.GameAction;
import de.bejoschgaming.orderofelements.replaysystem.GameActionType;
import de.bejoschgaming.orderofelements.replaysystem.Replay;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;

public class Game {

	private int initGameID = -1;
	private int gameID = -1;
	
	private ClientSession player1;
	private ClientSession player2;
	private GameType type;
	private Map map;
	
	private int round = 1;
	private int activePlayerNumber = 1;
	private int turn = 1;
	private Replay replay = null;
	
	private boolean gameStarted = false;
	
	private List<ClientSession> spectators = new ArrayList<ClientSession>();
	
	public Game(ClientSession player1, ClientSession player2, GameType type) {
		
		this.player1 = player1;
		this.player2 = player2;
		this.type = type;
		
		//GET TEMP UNIQUE GAME ID
		Random r = new Random();
		do {
			this.initGameID = (r.nextInt(90000)+10000) * -1; //NEGATIVE 
		} while(GameHandler.getGame(this.getGameID()) != null);
		
		ConsoleHandler.printMessageInConsole("Created game "+this.getGameID()+"-"+this.getType()+": "+this.getPlayer1().getProfile().getName()+" vs "+this.getPlayer2().getProfile().getName(), true);
		ConsoleHandler.printMessageInConsole(this.getGameID(), "Created game "+this.getGameID()+"-"+this.getType()+": "+this.getPlayer1().getProfile().getName()+" vs "+this.getPlayer2().getProfile().getName(), true);
		
	}
	
	protected void startGame() {
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europa/Berlin"));
		String date = DatabaseHandler.doubleWriteNumber(cal.get(Calendar.DAY_OF_MONTH))+"_"+DatabaseHandler.doubleWriteNumber(cal.get(Calendar.MONTH)+1)+"_"+DatabaseHandler.doubleWriteNumber(cal.get(Calendar.YEAR));
		try {
			DatabaseHandler.insertData(DatabaseHandler.tabellName_games, "PlayerID_1,PlayerID_2,WinnerID,Map,Datum", this.player1.getProfile().getID()+"','"+this.player2.getProfile().getID()+"','"+this.player1.getProfile().getID()+"','"+this.map.getName()+"','"+date);
		}catch (SQLException error) {
			error.printStackTrace();
			ConsoleHandler.printMessageInConsole("Couldn't register new game!", true);
			endGame(null, GameFinishType.ERROR);
			return;
		}
		this.gameID = DatabaseHandler.selectInt(DatabaseHandler.tabellName_games, "ID", "PlayerID_1,PlayerID_2", this.player1.getProfile().getID()+"','"+this.player2.getProfile().getID());
		this.replay = new Replay(this.gameID);
		this.gameStarted = true;
		//SYNTAX: 500-gameID;playerID_1;playerID_2;MapName
		String data = this.getGameID()+";"+this.player1.getProfile().getName()+";"+this.player2.getProfile().getName()+";"+this.map.getName();
		this.sendPacketToAllPlayer(500, data);
		ConsoleHandler.printMessageInConsole("Started game "+this.getGameID()+"-"+this.getType()+": "+player1.getProfile().getName()+" vs "+player2.getProfile().getName(), true);
		ConsoleHandler.printMessageInConsole(this.getGameID(), "Started game "+this.getGameID()+"-"+this.getType()+": "+player1.getProfile().getName()+" vs "+player2.getProfile().getName(), true);
		
	}
	
	public void performAction(GameActionType type, String data) {
		
		this.replay.addAction(new GameAction(this.gameID, type, this.round, this.turn, data));
		ConsoleHandler.printMessageInConsole(this.getGameID(), "Perform ACTION "+type+" - ["+data+"] at R:"+this.round+"-T:"+this.turn+" by "+this.getActivePlayer().getProfile().getName(), true);
		
	}
	
	public void nextTurn() {
		
		this.turn++;
		ConsoleHandler.printMessageInConsole(this.getGameID(), "Next TURN "+this.turn+"/2 for "+this.getActivePlayer().getProfile().getName(), true);
		sendPacketToAllPlayer(501, ""+this.turn);
		
	}
	
	public void nextRound() {
		
		this.round++;
		this.turn = 1;
		this.activePlayerNumber = (this.activePlayerNumber == 1 ? 2 : 1);
		ConsoleHandler.printMessageInConsole(this.getGameID(), "New ROUND "+this.round+" for "+this.getActivePlayer().getProfile().getName(), true);
		ConsoleHandler.printMessageInConsole(this.getGameID(), "Next TURN "+this.turn+"/2 for "+this.getActivePlayer().getProfile().getName(), true);
		sendPacketToAllPlayer(502, ""+this.round);
		
	}
	
	public void sendPacketToAllPlayer(int signal, String message) { sendPacketToAllPlayer(signal, message, true); }
	public void sendPacketToAllPlayer(int signal, String message, boolean sendToSpecs) {
		
		this.player1.sendPacket(signal, message);
		this.player2.sendPacket(signal, message);
		
		if(sendToSpecs == true) {
			for(ClientSession spec : this.spectators) {
				spec.sendPacket(signal, message);
			}
		}
		
	}
	
	protected void endGame(ClientSession winner, GameFinishType cause) {
		
		//winner can be null!
		//TODO
		switch(cause) {
		case DEFAULT:
			
			break;
		case ERROR:
			
			break;
		case LOST_CONNECTION:
			
			break;
		case SURRENDER:
			
			break;
		}
		
		ConsoleHandler.printMessageInConsole("Ended game "+this.getGameID()+"-"+this.getType()+": "+player1.getProfile().getName()+" vs "+player2.getProfile().getName()+" with finish-type "+cause, true);
		ConsoleHandler.printMessageInConsole(this.getGameID(), "Ended game "+this.getGameID()+"-"+this.getType()+": "+player1.getProfile().getName()+" vs "+player2.getProfile().getName()+" with finish-type "+cause, true);
		this.removeGame();
		
	}
	
	protected void removeGame() {
		
		GameHandler.unregisterGame(this);
		ConsoleHandler.printMessageInConsole("Removed game "+this.getGameID()+"-"+this.getType()+": "+this.getPlayer1().getProfile().getName()+" vs "+this.getPlayer2().getProfile().getName(), true);
		ConsoleHandler.printMessageInConsole(this.getGameID(), "Removed game "+this.getGameID()+"-"+this.getType()+": "+this.getPlayer1().getProfile().getName()+" vs "+this.getPlayer2().getProfile().getName(), true);
		
	}
	
	public void addSpectator(ClientSession newSpec) {
		
		this.spectators.add(newSpec);
		for(GameAction action : this.replay.getActions()) {
			//SYNTAX: 260-GameID;Round;Turn;Type;Data
			newSpec.sendPacket(260, action.getGameID()+";"+action.getRound()+";"+action.getTurn()+";"+action.getType()+";"+action.getData());
		}
		newSpec.sendPacket(261, this.gameID+";"+this.replay.getActions().size());
		
	}
	
	public void removeSpectator(ClientSession spec) {
		
		this.spectators.remove(spec);
		
	}
	
	public String getShortInfo() {
		return "ID: "+this.getGameID()+" - Type: "+this.getType()+" - "+player1.getProfile().getName()+" vs "+player2.getProfile().getName()+" - Started: "+this.hasGameStarted()+" - ActivePlayer: "+this.getActivePlayerNumber()+" - Round: "+this.getRound()+" - Turn: "+this.getTurn()+" - Specs: "+this.spectators.size();
	}
	
	public int getGameID() {
		if(gameStarted == true) {
			return gameID;
		}else {
			return initGameID;
		}
	}
	public ClientSession getPlayer1() {
		return player1;
	}
	public ClientSession getPlayer2() {
		return player2;
	}
	public boolean containsPlayer(ClientSession session) { return containsPlayer(session.getSessionID()); }
	public boolean containsPlayer(int sessionID) {
		if(player1.getSessionID() == sessionID) {
			return true;
		}else if(player2.getSessionID() == sessionID) {
			return true;
		}else {
			return false;
		}
	}
	public GameType getType() {
		return type;
	}
	public Map getMap() {
		return map;
	}
	public boolean hasGameStarted() {
		return gameStarted;
	}
	public int getRound() {
		return round;
	}
	public int getTurn() {
		return turn;
	}
	public int getActivePlayerNumber() {
		return activePlayerNumber;
	}
	public ClientSession getActivePlayer() {
		return (this.activePlayerNumber == 1 ? this.player1 : this.player2);
	}
	
}
