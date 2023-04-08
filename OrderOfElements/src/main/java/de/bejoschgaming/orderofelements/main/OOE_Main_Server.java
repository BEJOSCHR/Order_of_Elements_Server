package de.bejoschgaming.orderofelements.main;

import java.util.Timer;
import java.util.TimerTask;

import de.bejoschgaming.orderofelements.connection.ConnectionHandler;
import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.filesystem.FileHandler;
import de.bejoschgaming.orderofelements.mapsystem.MapHandler;
import de.bejoschgaming.orderofelements.patchnotessystem.PatchnotesHandler;
import de.bejoschgaming.orderofelements.sessionsystem.SessionHandler;
import de.bejoschgaming.orderofelements.unitsystem.UnitHandler;

public class OOE_Main_Server {

	public static long startMillis = 0;
	
	public static void main(String[] args) {
		
		ConsoleHandler.printMessageInConsole("Starting OrderOfElements_Server [OOE_S]...", true);
		
		FileHandler.firstWrite();
		
		ConsoleHandler.startUserInputScanner();
		
		DatabaseHandler.connectToDB();
		if(DatabaseHandler.connectedToDB == false) {
			ConsoleHandler.printMessageInConsole("Startup aborted! Can't continue withut db connection!", true);
			System.exit(1);
		}
		
		ConnectionHandler.startServerConnection();
		
		MapHandler.loadMapsFromDB();
		ConsoleHandler.printMessageInConsole("Loaded "+MapHandler.getLoadedMaps().size()+" maps from DB!", true);
		
		PatchnotesHandler.loadPatchnotesData();
		ConsoleHandler.printMessageInConsole("Loaded patchnotes from DB! ("+(PatchnotesHandler.getPatchnotesData()!=null)+")", true);
		
		UnitHandler.loadUnitData();
		ConsoleHandler.printMessageInConsole("Loaded "+UnitHandler.getUnitCategories().size()+" unitCategories, "+UnitHandler.getUnitTargetPattern().size()+" unitTargetPatterns and "+UnitHandler.getUnits().size()+" units!", true);
		
		startMillis = System.currentTimeMillis();
		ConsoleHandler.printMessageInConsole("Startup finished!", true);
		
	}

	public static void terminateProgramm() {
		
		ConsoleHandler.printMessageInConsole("Stopping OrderOfElements_Server [OOE_S]...", true);
		
		ConsoleHandler.stopUserInputScanner();
		
		SessionHandler.disconnectAllSessions();
		
		//CLOSE DLEAY - wait for sessions to be closed
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				
				ConnectionHandler.closeServerConnection();
				
				DatabaseHandler.disconnectFromDB();
				
				ConsoleHandler.printMessageInConsole("Shuttdown finished! [Runtime: "+(System.currentTimeMillis()-startMillis)/1000/60+" Minutes]", true);
				
				//CLOSE DLEAY - wait so console output can be checked
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						ConsoleHandler.printMessageInConsole("Terminating...", true);
						System.exit(0);
					}
				}, 1000*3);
				
			}
		}, 1000*2);
		
	}
	
}
