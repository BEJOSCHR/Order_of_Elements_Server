package de.bejoschgaming.orderofelements.gamesystem;

import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;

public class QueueGame extends Game {

	protected int acceptedPlayer = 0;
	
	public QueueGame(ClientSession player1, ClientSession player2, GameType type) {
		super(player1, player2, type);
		
		sendGameAcceptRequest();
		
	}

	private void sendGameAcceptRequest() {
		
		this.getPlayer1().sendPacket(311, ""+this.getGameID());
		this.getPlayer2().sendPacket(311, ""+this.getGameID());
		
	}
	
	public void playerAcceptGame(ClientSession acceptSession) {
		
		acceptedPlayer++;
		if(acceptedPlayer == 2) {
			this.startGame();
		}
		
	}
	
	public void playerDeclineGame(ClientSession declineSession) {
		
		//ABORT GAME
		this.getPlayer1().sendPacket(311, "Game got declined!");
		this.getPlayer2().sendPacket(311, "Game got declined!");
		
		
		
	}
	
}
