package de.bejoschgaming.orderofelements.playersystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.decksystem.Deck;
import de.bejoschgaming.orderofelements.decksystem.DeckHandler;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;
import de.bejoschgaming.orderofelements.sessionsystem.SessionHandler;
import de.bejoschgaming.orderofelements.unitsystem.UnitHandler;

public class PlayerProfile {

	private ClientSession clientSession;
	private int ID;
	private String name;
	
	private PlayerStats stats;
	private List<Deck> decks = new LinkedList<Deck>();
	private List<Integer> friendRequests = new ArrayList<>();
	private HashMap<Integer, String> friendList = new HashMap<Integer, String>(); //FRIEND ID - DATE
	
	
	public PlayerProfile(ClientSession clientSession, int playerID, String playerName) {
		
		this.clientSession = clientSession;
		this.ID = playerID;
		this.name = playerName;
		
		//LOAD AND SEND PLAYER STATS
		this.stats = new PlayerStats(playerID);
		this.stats.updateStatus("Online");
		this.clientSession.sendPacket(200, this.ID+"-"+this.name+"-"+this.stats.getDataAsString(";"));
		
		loadPlayerData();
		
	}
	
	private void loadPlayerData() {
		
		loadDecks();
		loadFriendRequests();
		sendInfoAboutFriendRequests();
		loadFriendList();
		sendInfoAboutAndToFriends();
		UnitHandler.sendUnitData(this);
		
	}
	
	public void loadDecks() {
		this.decks = DeckHandler.getDecks(this.ID);
		DeckHandler.sendDecksToPlayer(this.decks, this.clientSession.getConnection());
	}
	public void loadFriendRequests() {
		this.friendRequests.clear();
		//ALLE REQUESTS (ID1) WO DIESE ID DAS ZIEL (ID2) IST
		this.friendRequests = DatabaseHandler.getAllWhereEqual_Int(DatabaseHandler.tabellName_friendRequests, "ID1", "ID2", ""+this.ID);
	}
	private void sendInfoAboutFriendRequests() {
		for(int friendRequestID : this.friendRequests) {
			String requestName = DatabaseHandler.selectString(DatabaseHandler.tabellName_profile, "Name", "ID", ""+friendRequestID);
			clientSession.getConnection().sendPacket(241, friendRequestID+";"+requestName);
		}
	}
	public void loadFriendList() {
		this.friendList.clear();
		//ALLE IDs (ID2) DIE DIESE ID (ID1) HABEN
		List<Integer> friendListIDs = DatabaseHandler.getAllWhereEqual_Int(DatabaseHandler.tabellName_friendList, "ID2", "ID1", ""+this.ID);
		for(int friendID : friendListIDs) {
			String date = DatabaseHandler.selectString(DatabaseHandler.tabellName_friendList, "Datum", "ID1,ID2", this.ID+"','"+friendID);
			this.friendList.put(friendID, date);
		}
	}
	private void sendInfoAboutAndToFriends() {
		//SEND ONLINE INFO TO ALL FRIENDS WHICH ARE ONLINE
		for(int friendID : this.friendList.keySet()) {
			ClientSession friendSession = SessionHandler.getSession(friendID);
			if(friendSession != null) {
				//FRIEND IS CONNECTED
				friendSession.sendPacket(205, ""+this.ID);
				friendSession.sendPacket(207, this.ID+";"+this.stats.getStatus());
				//SEND INFO THAT FRIEND IS ONLINE
				clientSession.sendPacket(205, ""+friendID);
			}else {
				//SEND INFO THAT FRIEND IS OFFLINE
				clientSession.sendPacket(206, ""+friendID);
			}
		}
	}
	public void disconnect() {
		
		//SEND OFFLINE INFO TO ALL FRIENDS WHICH ARE ONLINE
		for(int friendID : this.friendList.keySet()) {
			ClientSession friendSession = SessionHandler.getSession(friendID);
			if(friendSession != null) {
				//FRIEND IS CONNECTED
				friendSession.sendPacket(207, this.ID+";"+"Offline");
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
	public PlayerStats getStats() {
		return stats;
	}
	
}
