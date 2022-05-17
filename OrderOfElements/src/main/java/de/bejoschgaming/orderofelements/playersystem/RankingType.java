package de.bejoschgaming.orderofelements.playersystem;

public enum RankingType {

	IRON(0),
	BRONZE(1),
	SILVER(2),
	GOLD(3),
	PLATINUM(4),
	DIAMOND(5);
	
	private final int number;
	
	private RankingType(final int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
}
