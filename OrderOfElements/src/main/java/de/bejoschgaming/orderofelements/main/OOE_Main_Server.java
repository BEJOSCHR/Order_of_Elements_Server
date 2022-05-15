package de.bejoschgaming.orderofelements.main;

import java.util.Timer;
import java.util.TimerTask;

import de.bejoschgaming.orderofelements.connection.ConnectionHandler;
import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.filesystem.FileHandler;
import de.bejoschgaming.orderofelements.mapsystem.MapHandler;
import de.bejoschgaming.orderofelements.session.SessionHandler;

public class OOE_Main_Server {

	public static long startMillis = 0;
	
	public static void main(String[] args) {
		
		ConsoleHandler.printMessageInConsole("Starting OrderOfElements_Server [OOE_S]...", true);
		
		FileHandler.firstWrite();
		
		ConsoleHandler.startUserInputScanner();
		
		DatabaseHandler.connectToDB();
		
		ConnectionHandler.startServerConnection();
		
		MapHandler.loadMapsFromDB();
		ConsoleHandler.printMessageInConsole("Loaded "+MapHandler.getLoadedMaps().size()+" maps from DB!", true);
		
		startMillis = System.currentTimeMillis();
		ConsoleHandler.printMessageInConsole("Startup finished!", true);
		
	}

	public static void terminateProgramm() {
		
		ConsoleHandler.printMessageInConsole("Stopping OrderOfElements_Server [OOE_S]...", true);
		
		ConsoleHandler.stopUserInputScanner();
		
		SessionHandler.disconnectAllSessions();
		
		ConnectionHandler.closeServerConnection();
		
		DatabaseHandler.disconnectFromDB();
		
		ConsoleHandler.printMessageInConsole("Shuttdown finished! [Runtime: "+(System.currentTimeMillis()-startMillis)/1000/60+" Minutes]", true);
		
		//CLOSE DLEAY
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				ConsoleHandler.printMessageInConsole("Terminating...", true);
				System.exit(0);
			}
		}, 1000*3);
		
	}
	
}
