package de.bejoschgaming.orderofelements.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.mina.core.session.IoSession;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.decksystem.Deck;
import de.bejoschgaming.orderofelements.mapsystem.Map;
import de.bejoschgaming.orderofelements.mapsystem.MapHandler;
import de.bejoschgaming.orderofelements.session.ClientSession;
import de.bejoschgaming.orderofelements.session.SessionHandler;

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
				DatabaseHandler.insertData(DatabaseHandler.tabellName_decks, "OwnerID,Name,Data", ownerID+","+deckName+","+deckData);
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
			//FRIENDREQUEST ACCEPT
			//SYNTAX: 241-acceptedID
			//TODO remove and add db entries and reload profile friendlist
			break;
		case 242:
			//FRIENDREQUEST DECLINE
			//SYNTAX: 242-deniedID
			//TODO remove db entry
			break;
		case 245:
			//FRIENDLIST SEND REQUEST
			//SYNTAX: 245-
			for(int friendID : this.clientSession.getProfile().getFriendList().keySet()) {
				//245-friendID;friendshipStartDate
				String friendDate = this.clientSession.getProfile().getFriendList().get(friendID);
				sendPacket(245, friendID+";"+friendDate);
			}
			break;
		case 246:
			//FRIEND REMOVE
			//SYNTAX: 246-removedFriendID
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
