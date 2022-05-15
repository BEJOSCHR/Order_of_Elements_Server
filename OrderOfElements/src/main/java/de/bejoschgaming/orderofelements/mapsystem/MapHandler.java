package de.bejoschgaming.orderofelements.mapsystem;

import java.util.HashMap;
import java.util.List;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

public class MapHandler {

	private static HashMap<String, Map> loadedMaps = new HashMap<>();
	
	public static void loadMapsFromDB() {
		
		if(DatabaseHandler.connectedToDB == false) {
			ConsoleHandler.printMessageInConsole("Can't load maps without db connection!", true);	
			return;
		}
		
		List<String> allMapNames = DatabaseHandler.getAll_Str(DatabaseHandler.tabellName_maps, "Name");
		
		loadedMaps.clear();
		for(String name : allMapNames) {
			loadedMaps.put(name, new Map(name));
		}
		
	}
	
	public static HashMap<String, Map> getLoadedMaps() {
		return loadedMaps;
	}
	public static Map getMap(String name) {
		if(loadedMaps.containsKey(name)) {
			return loadedMaps.get(name);
		}else {
			return null;
		}
	}
	
}
