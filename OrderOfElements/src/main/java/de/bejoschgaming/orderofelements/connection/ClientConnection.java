package de.bejoschgaming.orderofelements.connection;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;

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
