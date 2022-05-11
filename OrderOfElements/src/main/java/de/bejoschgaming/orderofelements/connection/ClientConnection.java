package de.bejoschgaming.orderofelements.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.mina.core.session.IoSession;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.session.ClientSession;
import de.bejoschgaming.orderofelements.session.SessionHandler;

public class ClientConnection {

	private IoSession connection; 
	private List<String> sendPackets = new ArrayList<>();
	
	public ClientConnection(IoSession connection_) {
		
		this.connection = connection_;
		SessionHandler.registerNewSession(new ClientSession(this));
		
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
	
}
