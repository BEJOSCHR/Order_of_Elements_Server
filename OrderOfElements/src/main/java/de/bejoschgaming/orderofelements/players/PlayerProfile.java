package de.bejoschgaming.orderofelements.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.decksystem.Deck;
import de.bejoschgaming.orderofelements.decksystem.DeckHandler;
import de.bejoschgaming.orderofelements.session.ClientSession;
import de.bejoschgaming.orderofelements.session.SessionHandler;

public class PlayerProfile {

	private ClientSession clientSession;
	private int ID;
	private String name;
	
	private List<Deck> decks = new LinkedList<Deck>();
	private List<Integer> friendRequests = new ArrayList<>();
	private HashMap<Integer, String> friendList = new HashMap<Integer, String>(); //FRIEND ID - DATE
	
	
	public PlayerProfile(ClientSession clientSession, int playerID, String playerName) {
		
		this.clientSession = clientSession;
		this.ID = playerID;
		this.name = playerName;
		
		loadPlayerData();
		
	}
	
	private void loadPlayerData() {
		
		loadDecks();
		loadFriendRequests();
		loadFriendList();
		
	}
	
	public void loadDecks() {
		this.decks = DeckHandler.getDecks(this.ID);
	}
	public void loadFriendRequests() {
		//ALLE REQUESTS (ID1) WO DIESE ID DAS ZIEL (ID2) IST
		this.friendRequests = DatabaseHandler.getAllWhereEqual_Int(DatabaseHandler.tabellName_friendRequests, "ID1", "ID2", ""+this.ID);
	}
	public void loadFriendList() {
		//ALLE IDs (ID2) DIE DIESE ID (ID1) HABEN
		List<Integer> friendListIDs = DatabaseHandler.getAllWhereEqual_Int(DatabaseHandler.tabellName_friendList, "ID2", "ID1", ""+this.ID);
		for(int friendID : friendListIDs) {
			String date = DatabaseHandler.selectString(DatabaseHandler.tabellName_friendList, "Datum", "ID1,ID2", this.ID+"','"+friendID);
			this.friendList.put(friendID, date);
		}
		//SEND ONLINE INFO TO ALL FRIENDS WHICH ARE ONLINE
		for(int friendID : this.friendList.keySet()) {
			ClientSession friendSession = SessionHandler.getSession(friendID);
			if(friendSession != null) {
				//FRIEND IS CONNECTED
				friendSession.sendPacket(205, ""+this.ID);
			}
		}
	}
	
	public void disconnect() {
		
		//SEND OFFLINE INFO TO ALL FRIENDS WHICH ARE ONLINE
		for(int friendID : this.friendList.keySet()) {
			ClientSession friendSession = SessionHandler.getSession(friendID);
			if(friendSession != null) {
				//FRIEND IS CONNECTED
				friendSession.sendPacket(206, ""+this.ID);
			}
		}
		
	}
	
	public int getID() {
		return ID;
	}
	public String getName() {
		return name;
	}
	public ClientSession getClientSession() {
		return clientSession;
	}
	public List<Deck> getDecks() {
		return decks;
	}
	public List<Integer> getFriendRequests() {
		return friendRequests;
	}
	public HashMap<Integer, String> getFriendList() {
		return friendList;
	}
	
}
