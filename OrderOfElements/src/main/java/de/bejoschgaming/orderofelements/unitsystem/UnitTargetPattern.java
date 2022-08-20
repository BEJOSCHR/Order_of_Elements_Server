package de.bejoschgaming.orderofelements.unitsystem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

public class UnitTargetPattern {

	private String pattern;
	private String rawTargetSyntax;
	private List<Point> targetRelatives = new ArrayList<Point>();
	
	public UnitTargetPattern(String pattern, String targetSyntax) {
		
		this.pattern = pattern;
		this.rawTargetSyntax = targetSyntax;
		
		for(String field : targetSyntax.split("_")) {
			
			int x = Integer.parseInt(field.split(":")[0]);
			int y = Integer.parseInt(field.split(":")[1]);
			this.targetRelatives.add(new Point(x, y));
			
		}
		
		if(this.targetRelatives.isEmpty()) {
			ConsoleHandler.printMessageInConsole("TargetPattern '"+this.pattern+"' init with no relativePoints!", true);
		}
		
	}

	public String getUnitTargetPatternDataString() {
		
		String div = ";";
		return pattern+div+rawTargetSyntax;
		
	}
	
	public String getPattern() {
		return pattern;
	}
	public String getRawTargetSyntax() {
		return rawTargetSyntax;
	}
	public List<Point> getTargetRelatives() {
		return targetRelatives;
	}
	
}
