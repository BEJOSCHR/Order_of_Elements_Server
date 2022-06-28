package de.bejoschgaming.orderofelements.playersystem;

public enum RankingType {

	UNRANKED(0),
	PAWN(1),
	SQUIRE(2),
	KNIGHT(3),
	KING(4),
	EMPEROR(5);
	
	private final int number;
	
	private RankingType(final int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
}
