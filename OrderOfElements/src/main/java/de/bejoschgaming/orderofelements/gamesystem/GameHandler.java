package de.bejoschgaming.orderofelements.gamesystem;

import java.util.ArrayList;
import java.util.List;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;

public class GameHandler {

	private static List<Game> runningGames = new ArrayList<Game>();
	
	public static void registerNewGame(ClientSession player1, ClientSession player2, GameType type) {
		
		Game game;
		if(type == GameType.CUSTOM) {
			game = new CustomGame(player1, player2);
		}else {
			game = new QueueGame(player1, player2, type);
		}
		runningGames.add(game);
		
	}
	
	public static void unregisterGame(Game game) {
		
		runningGames.remove(game);
		
	}
	
	public static Game getGame(int gameID) {
		for(Game game : runningGames) {
			if(game.getGameID() == gameID) {
				return game;
			}
		}
		return null;
	}
	public static Game getGame(ClientSession session) {
		for(Game game : runningGames) {
			if(game.containsPlayer(session)) {
				return game;
			}
		}
		return null;
	}
	public static List<Game> getRunningGames() {
		return runningGames;
	}
	
}
