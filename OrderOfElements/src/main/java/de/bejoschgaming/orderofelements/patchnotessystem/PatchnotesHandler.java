package de.bejoschgaming.orderofelements.patchnotessystem;

import de.bejoschgaming.orderofelements.connection.ClientConnection;
import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;

public class PatchnotesHandler {

	private static String patchnotesData;
	
	public static void loadPatchnotesData() {
		
		patchnotesData = DatabaseHandler.getPatchNotesAsPacketString();
//		ConsoleHandler.printMessageInConsole("Loaded patchnotes: "+patchnotesData, true);
		
	}
	
	public static void sendPatchnotesData(ClientSession clientSession) { sendPatchnotesData(clientSession.getConnection()); }
	public static void sendPatchnotesData(ClientConnection clientConnection) {
		
		if(patchnotesData == null) {
			clientConnection.sendPacket(180, "No patchnotes!");
		}else {
			clientConnection.sendPacket(180, patchnotesData);
		}
		
	}
	
	public static String getPatchnotesData() {
		return patchnotesData;
	}
	
}
