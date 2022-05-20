package de.bejoschgaming.orderofelements.gamesystem;

import java.util.Random;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;

public class Game {

	private int initGameID = -1;
	private int gameID = -1;
	
	private ClientSession player1;
	private ClientSession player2;
	private GameType type;
	
	private boolean gameStarted = false;
	
	public Game(ClientSession player1, ClientSession player2, GameType type) {
		
		this.player1 = player1;
		this.player2 = player2;
		this.type = type;
		
		//GET TEMP UNIQUE GAME ID
		Random r = new Random();
		do {
			this.initGameID = (r.nextInt(90000)+10000) * -1; //NEGATIVE 
		}while(GameHandler.getGame(this.getGameID()) != null);
		
		ConsoleHandler.printMessageInConsole("Created game "+this.getGameID()+"-"+this.getType()+": "+this.getPlayer1().getProfile().getName()+" vs "+this.getPlayer2().getProfile().getName(), true);
		
	}
	
	protected void startGame() {
		
		//TODO register in DB and get perma GameID by this
		this.gameStarted = true;
		
	}
	
	protected void removeGame() {
		
		GameHandler.unregisterGame(this);
		
	}
	
	
	public int getGameID() {
		if(gameStarted) {
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
	public GameType getType() {
		return type;
	}
	public boolean hasGameStarted() {
		return gameStarted;
	}
	
}
