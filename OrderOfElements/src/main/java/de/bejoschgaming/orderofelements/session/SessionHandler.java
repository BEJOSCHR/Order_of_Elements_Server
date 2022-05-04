package de.bejoschgaming.orderofelements.session;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import de.bejoschgaming.orderofelements.connection.ClientConnection;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.players.PlayerProfile;

public class SessionHandler {

	private static List<ClientSession> connectedSessions = new ArrayList<>();
	
	public static boolean registerNewSession(ClientSession session) {
		
		if(isSessionConnected(session) == false) {
			//NOT CONNECTED
			connectedSessions.add(session);
			ConsoleHandler.printMessageInConsole("Client "+session.getSessionID()+" connected!", true);
			return true;
		}else {
			//ALREADY CONNECTED
			return false;
		}
		
	}
	
	public static boolean unregisterSession(ClientSession session) {
		
		if(isSessionConnected(session) == true) {
			//CONNECTED
			connectedSessions.remove(session);
			if(session.isProfileLoaded()) {
				ConsoleHandler.printMessageInConsole("Client "+session.getSessionID()+" ("+session.getProfile().getName()+"-"+session.getProfile().getID()+") disconnected!", true);
			}else {
				ConsoleHandler.printMessageInConsole("Client "+session.getSessionID()+" disconnected!", true);
			}
			return true;
		}else {
			//NOT CONNECTED
			return false;
		}
		
	}
	
	public static void disconnectAllSessions() {
		
		while(connectedSessions.isEmpty() == false) {
			//AS LONG AS NOT EMPTY
			connectedSessions.get(0).disconnect();
		}
		
	}
	
	//---
	public static ClientSession getSession(ClientSession session_) {
		
		for(ClientSession session : connectedSessions) {
			if(session.getSessionID() == session_.getSessionID()) {
				return session;
			}
		}
		return null;
		
	}
	public static boolean isSessionConnected(ClientSession session_) { return getSession(session_) != null;	}
	//---
	public static ClientSession getSession(int sessionID) {
		
		for(ClientSession session : connectedSessions) {
			if(session.getSessionID() == sessionID) {
				return session;
			}
		}
		return null;
		
	}
	public static boolean isSessionConnected(int sessionID) { return getSession(sessionID) != null;	}
	//---
	public static ClientSession getSession(IoSession ioSession) {
		
		for(ClientSession session : connectedSessions) {
			if(session.getConnection().getID() == ioSession.getId()) {
				return session;
			}
		}
		return null;
		
	}
	public static boolean isSessionConnected(IoSession ioSession) { return getSession(ioSession) != null;	}
	//---
	public static ClientSession getSession(String name) {
		
		for(ClientSession session : connectedSessions) {
			if(session.getProfile().getName().equals(name)) {
				return session;
			}
		}
		return null;
		
	}
	public static boolean isSessionConnected(String name) { return getSession(name) != null;	}
	//---
	public static ClientSession getSession(PlayerProfile profile) {
		
		for(ClientSession session : connectedSessions) {
			if(session.getProfile().getID() == profile.getID()) {
				return session;
			}
		}
		return null;
		
	}
	public static boolean isSessionConnected(PlayerProfile profile) { return getSession(profile) != null;	}
	//---
	public static ClientSession getSession(ClientConnection connection) {
		
		for(ClientSession session : connectedSessions) {
			if(session.getConnection().getID() == connection.getID()) {
				return session;
			}
		}
		return null;
		
	}
	public static boolean isSessionConnected(ClientConnection connection) { return getSession(connection) != null;	}
	//---
}
