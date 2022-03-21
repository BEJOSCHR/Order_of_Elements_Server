package de.bejoschgaming.orderofelements.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;

public class GraphicsHandler {

	public static JFrame frame = null;
	public static Label label = null;
	public static int width, height;
	
	public static void initVisuals() {

		frame = new JFrame();

		frame.setLocationRelativeTo(null);
		frame.setLocation(0, 0);
		frame.setUndecorated(true);

		frame.setTitle("OrderOfElements - " + "0.0.1");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

//		frame.addKeyListener(new KeyHandler());
//		frame.addMouseListener(new MouseHandler());
//		frame.addMouseMotionListener(new MouseHandler());
//		frame.addMouseWheelListener(new MouseHandler());
//		frame.addWindowListener(new WindowHandler());

//		try {
//		    frame.setIconImage(ImageIO.read(BomberfrauMain.class.getClassLoader()
//			    .getResourceAsStream(ImageHandler.PATH + "Bomberman_Icon.png")));
//		} catch (Exception error) {
//		    ConsoleHandler.print("Couldn't load window icon!", MessageType.BACKEND);
//		}

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		frame.setSize(1920, 1080);
		
		width = frame.getWidth();
		height = frame.getHeight();

		// TODO CALCULATE DIMENSIONS RELATIVE TO WIDTH AND HEIGHT
//		GameData.FIELD_DIMENSION = (int) (height - GameData.MAP_SIDE_BORDER) / GameData.MAP_DIMENSION;
		label = new Label();
		label.requestFocus();

		frame.requestFocus();

//		ConsoleHandler.print("Initialised Visuals!", MessageType.BACKEND);

	}
	
	/**
	 * Allgemeine Methode um einen beliebigen Text mit Parametern relativ zu einem
	 * Punkt (x,y) mittig darzustellen
	 * 
	 * @param g,        das Graphics object
	 * @param color,    die Textfarbe
	 * @param textSize, die Textgr��e
	 * @param text,     der eigentliche Text
	 * @param x,        die X-Koordinate (Links-Rechts-Verschiebung) zu der der Text
	 *                  mittig dargestellt wird
	 * @param y,        die Y-Koordinate (Oben-Unten-Verschiebung) zu der der Text
	 *                  mittig dargestellt wird
	 */
	public static void drawCentralisedText(Graphics g, Color color, int textSize, String text, int x, int y) {
	
		g.setColor(color);
		g.setFont(new Font("Arial", Font.BOLD, textSize));
		int width = g.getFontMetrics().stringWidth(" "+text);
		int height = g.getFontMetrics().getHeight() * 2 / 3;
		g.drawString(" "+text, x - width / 2, y + height / 2);
	
	}
	
}
