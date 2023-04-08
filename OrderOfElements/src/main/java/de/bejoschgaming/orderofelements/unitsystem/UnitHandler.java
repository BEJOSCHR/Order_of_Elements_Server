package de.bejoschgaming.orderofelements.unitsystem;

import java.util.ArrayList;
import java.util.List;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;
import de.bejoschgaming.orderofelements.playersystem.PlayerProfile;

public class UnitHandler {

	private static List<UnitCategory> unitCategories = new ArrayList<UnitCategory>();
	private static List<UnitTargetPattern> unitTargetPattern = new ArrayList<UnitTargetPattern>();
	private static List<Unit> units = new ArrayList<Unit>();
	
	//INIT
	
	public static void loadUnitData() {
		
		unitCategories.clear();
		unitTargetPattern.clear();
		units.clear();
		
		unitCategories = DatabaseHandler.getUnitCategories();
		unitTargetPattern = DatabaseHandler.getUnitTargetPattern();
		units = DatabaseHandler.getUnits();
		
	}

	//SENDER
	
	public static void sendUnitData(PlayerProfile profile) {
		
		for(UnitCategory category : unitCategories) {
			profile.getClientSession().getConnection().sendPacket(170, category.getUnitCategoryDataString());
		}
		for(UnitTargetPattern pattern : unitTargetPattern) {
			profile.getClientSession().getConnection().sendPacket(171, pattern.getUnitTargetPatternDataString());
		}
		for(Unit unit: units) {
			profile.getClientSession().getConnection().sendPacket(172, unit.getUnitDataString());
		}
		
	}
	
	//SPZIFIC GETTER
	
	public static UnitCategory getCategoryByName(String name) {
		
		for(UnitCategory category : unitCategories) {
			if(category.getCategory().equals(name)) {
				return category;
			}
		}
		return null;
		
	}
	public static UnitTargetPattern getTargetPatternByName(String name) {
		
		for(UnitTargetPattern targetPattern : unitTargetPattern) {
			if(targetPattern.getPattern().equals(name)) {
				return targetPattern;
			}
		}
		return null;
		
	}
	public static Unit getUnit(int ID) {
		
		for(Unit unit : units) {
			if(unit.getId() == ID) {
				return unit;
			}
		}
		return null;
		
	}
	public static Unit getUnit(String name) {
		
		for(Unit unit : units) {
			if(unit.getName().equalsIgnoreCase(name)) {
				return unit;
			}
		}
		return null;
		
	}
	
	//GETTER
	
	public static List<UnitCategory> getUnitCategories() {
		return unitCategories;
	}
	public static List<UnitTargetPattern> getUnitTargetPattern() {
		return unitTargetPattern;
	}
	public static List<Unit> getUnits() {
		return units;
	}
	
}
