package de.bejoschgaming.orderofelements.unitsystem;

import de.bejoschgaming.orderofelements.database.DatabaseHandler;

public class Unit {

	private int id;
	private String name;
	private int cost;
	private UnitCategory category;
	private int health, armor, shield, damage;
	private UnitTargetPattern type_attack, type_move, type_aura;
	
	private int startHealth;
	private int x = 0, y = 0;
	
	public Unit(int id) {
		
		this.id = id;
		this.name = DatabaseHandler.selectString(DatabaseHandler.tabellName_units, "Name", "ID", ""+id);
		this.cost = DatabaseHandler.selectInt(DatabaseHandler.tabellName_units, "Cost", "ID", ""+id);
		this.category = UnitHandler.getCategoryByName(DatabaseHandler.selectString(DatabaseHandler.tabellName_units, "Category", "ID", ""+id));
		this.health = DatabaseHandler.selectInt(DatabaseHandler.tabellName_units, "Health", "ID", ""+id);
		this.armor = DatabaseHandler.selectInt(DatabaseHandler.tabellName_units, "Armor", "ID", ""+id);
		this.shield = DatabaseHandler.selectInt(DatabaseHandler.tabellName_units, "Shield", "ID", ""+id);
		this.damage = DatabaseHandler.selectInt(DatabaseHandler.tabellName_units, "Damage", "ID", ""+id);
		this.type_attack = UnitHandler.getTargetPatternByName(DatabaseHandler.selectString(DatabaseHandler.tabellName_units, "Type_Attack", "ID", ""+id));
		this.type_move = UnitHandler.getTargetPatternByName(DatabaseHandler.selectString(DatabaseHandler.tabellName_units, "Type_Move", "ID", ""+id));
		this.type_aura = UnitHandler.getTargetPatternByName(DatabaseHandler.selectString(DatabaseHandler.tabellName_units, "Type_Aura", "ID", ""+id));
		
		this.x = 0;
		this.y = 0;
		this.startHealth = this.health;
		
	}

	//GETTER
	
	public String getUnitDataString() {
		
		String div = ";";
		return id+div+name+div+cost+div+category.getCategory()+div+health+div+armor+div+shield+div+damage+div+type_attack.getPattern()+div+type_move.getPattern()+div+type_aura.getPattern();
		
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getCost() {
		return cost;
	}
	public UnitCategory getCategory() {
		return category;
	}
	public int getStartHealth() {
		return startHealth;
	}
	public int getHealth() {
		return health;
	}
	public int getArmor() {
		return armor;
	}
	public int getShield() {
		return shield;
	}
	public int getDamage() {
		return damage;
	}
	public UnitTargetPattern getType_attack() {
		return type_attack;
	}
	public UnitTargetPattern getType_move() {
		return type_move;
	}
	public UnitTargetPattern getType_aura() {
		return type_aura;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}

	//SETTER

	public void setHealth(int health) {
		this.health = health;
	}
	public void setShield(int shield) {
		this.shield = shield;
	}
	public void addHealth(int health) {
		this.health += health;
	}
	public void addShield(int shield) {
		this.shield += shield;
	}

	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void addX(int x) {
		this.x += x;
	}
	public void addY(int y) {
		this.y += y;
	}
	
}
