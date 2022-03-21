package de.bejoschgaming.orderofelements.objects.map;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import de.bejoschgaming.orderofelements.objects.map.fields.Field;
import de.bejoschgaming.orderofelements.objects.map.fields.FieldType;

public class Map {

	private List<Field> fields = new ArrayList<>();
	
	public Map() {
		
		fillMap();
		
	}
	
	private void fillMap() {
		
		for(int x = 0 ; x < 33 ; x+=1) {
			for(int y = 0 ; y < 13 ; y+=1) {
				if(x % 2 == y % 2) {
					if(x == 0 || x == 32 || y == 0 || y == 12) {
						fields.add(new Field(x, y, FieldType.BORDER));
					}else {
						fields.add(new Field(x, y, FieldType.DEFAULT));
					}
				}
			}
		}
		
	}
	
	public void draw(Graphics g) {
		
		for(Field field : this.fields) {
			field.draw(g);
		}
		
	}
	
}
