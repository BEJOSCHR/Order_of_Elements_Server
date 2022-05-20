package de.bejoschgaming.orderofelements.gamesystem;

import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;

public class CustomGame extends Game {

	public CustomGame(ClientSession player1, ClientSession player2) {
		super(player1, player2, GameType.CUSTOM);
	}

}
