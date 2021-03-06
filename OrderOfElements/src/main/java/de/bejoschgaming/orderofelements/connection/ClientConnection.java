package de.bejoschgaming.orderofelements.connection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.mina.core.session.IoSession;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.decksystem.Deck;
import de.bejoschgaming.orderofelements.gamesystem.GameHandler;
import de.bejoschgaming.orderofelements.mapsystem.Map;
import de.bejoschgaming.orderofelements.mapsystem.MapHandler;
import de.bejoschgaming.orderofelements.queuesystem.QueueHandler;
import de.bejoschgaming.orderofelements.queuesystem.QueueType;
import de.bejoschgaming.orderofelements.replaysystem.GameAction;
import de.bejoschgaming.orderofelements.replaysystem.GameActionType;
import de.bejoschgaming.orderofelements.replaysystem.Replay;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;
import de.bejoschgaming.orderofelements.sessionsystem.SessionHandler;

public class ClientConnection {

	private ClientSession clientSession;
	private IoSession connection; 
	private List<String> sendPackets = new ArrayList<>();
	
	public ClientConnection(IoSession connection_) {
		
		this.connection = connection_;
		this.clientSession = new ClientSession(this);
		SessionHandler.registerNewSession(this.clientSession);
		
	}
	
	public void sendPacket(int signal, String message) {
		
		String sendString = signal+ConnectionHandler.packetDivider+message;
		connection.write(sendString);
		this.sendPackets.add(sendString);
		
	}
	
	public void handlePacket(ClientSession clientSession, int signal, String message) {
		
		ConsoleHandler.printMessageInConsole(0, "["+clientSession.getProfile().getName()+"-"+clientSession.getSessionID()+"] "+signal+" - "+message, true);
		String[] data = message.split(";");
		
		//SERVER recieve
		switch(signal) {
		case 100:
			//LOGIN
			//SYNTAX: 100-Name;Password
			String name = data[0];
			String password = data[1];
			boolean correctData = SessionHandler.checkLoginData(name, password);
			if(correctData) {
				int playerID;
				if(DatabaseHandler.connectedToDB) {
					playerID = DatabaseHandler.selectInt(DatabaseHandler.tabellName_profile, "ID", "Name", name);
				}else {
					playerID = new Random().nextInt(99)+1;
				}
				clientSession.login(playerID, name);
				clientSession.sendPacket(100, playerID+";"+"Successfully logged in!");
			}else {
				clientSession.sendPacket(101, "Wrong username or password!");
			}
			break;
		case 200:
			//PLAYERDATA SEND REQUEST
			//SYNTAX: 200-PlayerID
			//Sends all data of the given playerID to this client
			int targetPlayerID = Integer.parseInt(message);
			//TODO Send Name, Level, Status, Title etc...
			break;
		case 205:
			//ONLY SEND: ONLINE INFO TO ALL FRIENDS 
			break;
		case 206:
			//ONLY SEND: OFFLINE INFO TO ALL FRIENDS 
			break;
		case 220:
			//DECK SEND REQUEST
			//SYNTAX: 220-
			for(Deck deck : this.clientSession.getProfile().getDecks()) {
				//220-DeckID;DeckOwnerID;DeckName;DeckData
				sendPacket(220, deck.getDeckID()+";"+deck.getOwnerID()+";"+deck.getName()+";"+deck.getData());
			}
			break;
		case 221:
			//SAVE DECK
			//SYNTAX: 221-DeckID;DeckName;DeckData
			//IF DeckID is -1 its a new created deck else its an update
			int deckID = Integer.parseInt(data[0]);
			String deckName = data[1];
			String deckData = data[2];
			if(deckID == -1) {
				//NEW DECK
				int ownerID = this.clientSession.getProfile().getID();
				try {
					DatabaseHandler.insertData(DatabaseHandler.tabellName_decks, "OwnerID,Name,Data", ownerID+"','"+deckName+"','"+deckData);
				} catch (SQLException error) {
					break; //ALREADY DECK THERE
				}
			}else {
				//UPDATE EXISTING DECK
				DatabaseHandler.updateString(DatabaseHandler.tabellName_decks, "Name", deckName, "ID", ""+deckID);
				DatabaseHandler.updateString(DatabaseHandler.tabellName_decks, "Data", deckData, "ID", ""+deckID);
			}
			this.clientSession.getProfile().loadDecks();
			break;
		case 230:
			//MAP SEND REQUEST
			//SYNTAX: 230-
			for(Map map : MapHandler.getLoadedMaps().values()) {
				//230-MapName;MapData
				sendPacket(230, map.getName()+";"+map.getData());
			}
			break;
		case 240:
			//FRIENDREQUEST SEND REQUEST
			//SYNTAX: 240-
			for(int requestID : this.clientSession.getProfile().getFriendRequests()) {
				//240-requestID;requestName
				String requestName = DatabaseHandler.selectString(DatabaseHandler.tabellName_profile, "Name", "ID", ""+requestID);
				sendPacket(240, requestID+";"+requestName);
			}
			break;
		case 241:
			//FRIENDREQUEST ADD
			//SYNTAX: 241-youWantToAddID
			int newFriendTargetID = Integer.parseInt(message);
			try {
				DatabaseHandler.insertData(DatabaseHandler.tabellName_friendRequests, "ID1,ID2", clientSession.getProfile().getID()+"','"+newFriendTargetID);
			} catch (SQLException error) {
				break; //ALREADY REQUEST THERE
			}
			ClientSession friendTargetSession = SessionHandler.getSession(newFriendTargetID);
			if(friendTargetSession != null) {
				//TARGET IS CONNECTED
				friendTargetSession.getProfile().loadFriendRequests();
				friendTargetSession.sendPacket(240, clientSession.getProfile().getID()+";"+clientSession.getProfile().getName());
			}
			break;
		case 242:
			//FRIENDREQUEST ACCEPT
			//SYNTAX: 242-acceptedID
			int acceptedID = Integer.parseInt(message);
			DatabaseHandler.deleteData(DatabaseHandler.tabellName_friendRequests, "ID1,ID2", acceptedID+"','"+clientSession.getProfile().getID());
			DatabaseHandler.registerNewFriendship(acceptedID, clientSession.getProfile().getID());
			clientSession.getProfile().loadFriendRequests();
			clientSession.getProfile().loadFriendList();
			String friendshipDate = DatabaseHandler.selectString(DatabaseHandler.tabellName_friendList, "Datum", "ID1,ID2", acceptedID+"','"+clientSession.getProfile().getID());
			clientSession.sendPacket(245, acceptedID+";"+friendshipDate);
			ClientSession friendSession = SessionHandler.getSession(acceptedID);
			if(friendSession != null) {
				//FRIEND IS ONLINE
				clientSession.sendPacket(205, ""+acceptedID);
				friendSession.sendPacket(245, clientSession.getProfile().getID()+";"+friendshipDate);
				friendSession.sendPacket(205, ""+clientSession.getProfile().getID());
			}
			break;
		case 243:
			//FRIENDREQUEST DECLINE
			//SYNTAX: 243-deniedID
			int deniedID = Integer.parseInt(message);
			DatabaseHandler.deleteData(DatabaseHandler.tabellName_friendRequests, "ID1,ID2", deniedID+"','"+clientSession.getProfile().getID());
			clientSession.getProfile().loadFriendRequests();
			break;
		case 245:
			//FRIENDLIST SEND REQUEST
			//SYNTAX: 245-
			//Send all friendships...
			for(int friendID : this.clientSession.getProfile().getFriendList().keySet()) {
				//245-friendID;friendshipStartDate
				String friendDate = this.clientSession.getProfile().getFriendList().get(friendID);
				sendPacket(245, friendID+";"+friendDate);
			}
			//...and then who is online atm
			for(int friendID : this.clientSession.getProfile().getFriendList().keySet()) {
				if(SessionHandler.isSessionConnected(friendID)) {
					//FRIEND IS CONNECTED
					sendPacket(205, ""+friendID);
				}
			}
			break;
		case 246:
			//FRIEND REMOVE
			//SYNTAX: 246-removeFriendID
			int removeID = Integer.parseInt(message);
			DatabaseHandler.unregisterFriendship(removeID, clientSession.getProfile().getID());
			clientSession.getProfile().loadFriendList();
			ClientSession friendRemoveSession = SessionHandler.getSession(removeID);
			if(friendRemoveSession != null) {
				//FRIEND TO REMOVE IS ONLINE, so inform about remove
				friendRemoveSession.sendPacket(246, ""+clientSession.getProfile().getID());
				friendRemoveSession.getProfile().loadFriendList();
			}
			break;
		case 260:
			//REQUEST REPLAY DATA
			//SYNTAX: 260-gameID
			int replayLoad_gameID = Integer.parseInt(message);
			Replay replay = new Replay(replayLoad_gameID);
			replay.loadReplayData();
			for(GameAction action : replay.getActions()) {
				//SYNTAX: 260-GameID;Round;Turn;Type;Data
				clientSession.sendPacket(260, action.getGameID()+";"+action.getRound()+";"+action.getTurn()+";"+action.getType()+";"+action.getData());
			}
			clientSession.sendPacket(261, replayLoad_gameID+";"+replay.getActions().size());
			break;
		case 261:
			//ONLY SEND: FINISHED SENDING ALL REPLAY DATA FOR THIS GAME
			//SYNTAX: 261-gameID;amountOfActions
			break;
		case 300:
			//JOIN QUEUE
			//SYNTAX: 300-queueType (ENUM!)
			QueueType queueType_Join = QueueType.valueOf(message);
			QueueHandler.addToQueue(clientSession, queueType_Join);
			break;
		case 301:
			//LEAVE QUEUE
			//SYNTAX: 301-queueType (ENUM!)
			QueueType queueType_Leave = QueueType.valueOf(message);
			QueueHandler.removeFromQueue(clientSession, queueType_Leave);
			break;
		case 310:
			//ACCEPT GAME
			//SYNTAX: 310-gameID
			int gameAccept_gameID = Integer.parseInt(message);
			//TODO
			break;
		case 311:
			//DECLINE GAME
			//SYNTAX: 311-gameID
			int gameDecline_gameID = Integer.parseInt(message);
			//TODO
			break;
		case 500:
			//ONLY SEND: GAME START
			//SYNTAX: 500-gameID;playerID_1;playerID_2;MapName
			break;
		case 501:
			//NEXT TURN
			//SYNTAX: 501-gameID
			int gameID_1 = Integer.parseInt(message);
			GameHandler.getGame(gameID_1).nextTurn();
			break;
		case 502:
			//NEXT ROUND
			//SYNTAX: 503-gameID
			int gameID_2 = Integer.parseInt(message);
			GameHandler.getGame(gameID_2).nextRound();
			break;
		case 503:
			//PERFORM ACTION
			//SYNTAX: 503-gameID;ActionType (ENUM!);ActionData
			int gameID_3 = Integer.parseInt(data[0]);
			GameActionType actionType = GameActionType.valueOf(data[1]);
			String actionData = data[2];
			GameHandler.getGame(gameID_3).performAction(actionType, actionData);
			break;
		case 510:
			//GAME FINISH (SURRENDER)
			//SYNTAX: 510-gameID;cause
			int gameID_10 = Integer.parseInt(data[0]);
			String finishCause = data[1];
			//TODO
			break;
			
		}
		
	}
	
	public void disconnect() {
		
		connection.closeNow();
		
	}
	
	public int getID() {
		return (int) connection.getId();
	}
	public List<String> getSendPackets() {
		return sendPackets;
	}
	public ClientSession getClientSession() {
		return clientSession;
	}
	
}
