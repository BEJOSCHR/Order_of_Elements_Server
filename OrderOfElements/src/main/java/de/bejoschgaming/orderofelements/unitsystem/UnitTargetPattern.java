package de.bejoschgaming.orderofelements.unitsystem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

public class UnitTargetPattern {

	private String pattern;
	private List<Point> targetRelatives = new ArrayList<Point>();
	
	public UnitTargetPattern(String pattern, String targetSyntax) {
		
		this.pattern = pattern;
		
		for(String field : targetSyntax.split("_")) {
			
			int x = Integer.parseInt(field.split(":")[0]);
			int y = Integer.parseInt(field.split(":")[1]);
			this.targetRelatives.add(new Point(x, y));
			
		}
		
		if(this.targetRelatives.isEmpty()) {
			ConsoleHandler.printMessageInConsole("TargetPattern '"+this.pattern+"' init with no relativePoints!", true);
		}
		
	}

	public String getPattern() {
		return pattern;
	}
	public List<Point> getTargetRelatives() {
		return targetRelatives;
	}
	
}
