package de.bejoschgaming.orderofelements.replaysystem;

public class GameAction {

	private int gameID;
	private GameActionType type;
	private int round;
	private int turn;
	private String data;
	
	public GameAction(int gameID, GameActionType type, int round, int turn, String data) {
		
		this.gameID = gameID;
		this.type = type;
		this.round = round;
		this.turn = turn;
		this.data = data;
		
	}
	
	public int getGameID() {
		return gameID;
	}
	public GameActionType getType() {
		return type;
	}
	public int getRound() {
		return round;
	}
	public int getTurn() {
		return turn;
	}
	public String getData() {
		return data;
	}
	
}
