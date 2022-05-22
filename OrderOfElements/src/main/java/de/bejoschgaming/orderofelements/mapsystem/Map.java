package de.bejoschgaming.orderofelements.mapsystem;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;

public class Map {
	
	private String name;
	private String data;
	
	public Map(String name) {
		
		this.name = name;
		this.data = DatabaseHandler.selectString(DatabaseHandler.tabellName_maps, "Data", "Name", name);
		
	}
	
	public String getName() {
		return name;
	}
	public String getData() {
		return data;
	}
	
}
