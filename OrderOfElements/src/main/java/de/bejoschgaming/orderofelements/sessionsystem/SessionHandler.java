package de.bejoschgaming.orderofelements.sessionsystem;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

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
	
	public static String checkLoginData(String name, String passwordHash) {
		
		if(DatabaseHandler.connectedToDB) {
			//CHECK VIA DB
			
			String selectedPW = DatabaseHandler.selectString(DatabaseHandler.tabellName_profile, "Password", "Name", name);
			if(selectedPW == null) {
				//NO ENTRY FOUND AT ALL
				return "Wrong username or password!";
			}else {
				//CHECK IF ALREADY ONLINE
				if(SessionHandler.isSessionConnected(name) == true) {
					return "Already online!";
				}else {
					//NOT ONLINE
					if(passwordHash.equals(selectedPW)) {
						//PW RIGHT
						return null;
					}else {
						//PW WRONG
						return "Wrong username or password!";
					}
				}
			}
			
		}else {
			//NO DB ERROR MESSAGE (none specific for security reasons)
			return "Wrong username or password!";
		}
		
	}
	
	/**
	 * Null as result means the login data is free for register and valid
	 * @param name
	 * @param password
	 * @return null if all is good, else the cause for failure as String
	 */
	public static String checkRegisterData(String name, String password) {
		
		if(DatabaseHandler.connectedToDB) {
			//CHECK VIA DB
			
			String selectedPW = DatabaseHandler.selectString(DatabaseHandler.tabellName_profile, "Password", "Name", name);
			if(selectedPW == null) {
				//NO ENTRY FOUND AT ALL, so valid for register
				return null;
			}else {
				return "Username already used!";
			}
			
		}else {
			//NO DB CON
			return "Invalid username or password!";
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
			if(session.isProfileLoaded() && session.getProfile().getName().equals(name)) {
				return session;
			}
		}
		return null;
		
	}
	public static boolean isSessionConnected(String name) { return getSession(name) != null;	}
	//---
	
	public static List<ClientSession> getConnectedSessions() {
		return connectedSessions;
	}
	public static int numberOfConnectedSessions() {
		return connectedSessions.size();
	}
	
}
