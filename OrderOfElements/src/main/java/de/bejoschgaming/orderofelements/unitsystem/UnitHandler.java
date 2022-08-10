package de.bejoschgaming.orderofelements.unitsystem;

import java.util.ArrayList;
import java.util.List;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;

public class UnitHandler {

	private static List<UnitCategory> unitCategories = new ArrayList<UnitCategory>();
	private static List<UnitTargetPattern> unitTargetPattern = new ArrayList<UnitTargetPattern>();
	private static List<Unit> units = new ArrayList<Unit>();
	
	public static void loadUnitData() {
		
		unitCategories.clear();
		unitTargetPattern.clear();
		units.clear();
		
		unitCategories = DatabaseHandler.getUnitCategories();
		unitTargetPattern = DatabaseHandler.getUnitTargetPattern();
		units = DatabaseHandler.getUnits();
		
	}

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
