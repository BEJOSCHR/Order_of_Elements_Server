package de.bejoschgaming.orderofelements.session;

import java.util.Random;

import de.bejoschgaming.orderofelements.connection.ClientConnection;
import de.bejoschgaming.orderofelements.connection.ConnectionHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.players.PlayerProfile;

public class ClientSession {

	private int initSessionID = new Random().nextInt(90000)+10000;
	private long connectTimestamp;
	
	private boolean profileLoaded = false;
	private PlayerProfile profile = null;;
	private ClientConnection connection;
	
	public ClientSession(ClientConnection connection_) {
		
		this.connection = connection_;
		this.connectTimestamp = System.currentTimeMillis();
		
	}
	
	
	public void login(int playerID, String playerName) {
		
		this.profile = new PlayerProfile(this, playerID, playerName);
		profileLoaded = true;
		ConsoleHandler.printMessageInConsole("Client "+this.initSessionID+" logged in as ("+playerName+"-"+playerID+")!", true);
		
	}
	
	public void sendPacket(int signal, String message) {
		
		connection.sendPacket(signal, message);
		ConsoleHandler.printMessageInConsole(0, "Packet send from "+profile.getName()+": "+signal+ConnectionHandler.packetDivider+message, false);
		
	}
	
	public void disconnect() {
		
		boolean worked = SessionHandler.unregisterSession(this);
		if(worked) {
			//ONLY DISCONNECT IF UNREGISTER WORKED
			profile.disconnect();
			connection.disconnect();
		}
		
	}
	
	public String getShortInfo() {
		if(this.profileLoaded) {
			return "ID: "+this.getSessionID()+" - Name: "+this.profile.getName()+" - Decks: "+this.profile.getDecks().size()+" - Packets send: "+this.connection.getSendPackets().size()+" - Connected: "+(System.currentTimeMillis()-this.connectTimestamp)/1000/60+" min";
		}else { 
			return "ID: "+this.getSessionID()+" - Not logged in yet!"+" - Packets send: "+this.connection.getSendPackets().size()+" - Connected: "+(System.currentTimeMillis()-this.connectTimestamp)/1000/60+" min";
		}
	}
	
	public int getSessionID() {
		if(this.isProfileLoaded()) {
			return profile.getID();
		}else {
			return this.initSessionID;
		}
	}
	public ClientConnection getConnection() {
		return connection;
	}
	public PlayerProfile getProfile() {
		return profile;
	}
	public boolean isProfileLoaded() {
		return profileLoaded;
	}
	public long getConnectTimestamp() {
		return connectTimestamp;
	}
	
}
