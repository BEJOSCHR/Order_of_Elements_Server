package de.bejoschgaming.orderofelements.unitsystem;

public class UnitCategory {

	private String category, description;
	
	public UnitCategory(String category, String description) {
		
		this.category = category;
		this.description = description;
		
	}

	public String getUnitCategoryDataString() {
		
		String div = ";";
		return category+div+description;
		
	}
	
	public String getCategory() {
		return category;
	}
	public String getDescription() {
		return description;
	}
	
}
