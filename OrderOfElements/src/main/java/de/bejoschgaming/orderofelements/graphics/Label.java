
package de.bejoschgaming.orderofelements.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import de.bejoschgaming.orderofelements.objects.map.MapData;


@SuppressWarnings("serial")
public class Label extends JLabel {

    private int displayedFPS = 0;
    private long nextSecond = System.currentTimeMillis() + 1000;
    private int framesInCurrentSecond = 0;
    private int framesInLastSecond = 0;
    private long nextRepaintDelay = 0;
    private int maxFPS = 120;
    private boolean showFPS = true;

    public Label() {

		this.setBounds(0, 0, GraphicsHandler.width, GraphicsHandler.height);
		this.setVisible(true);
		GraphicsHandler.frame.add(this, BorderLayout.CENTER);

    }
    
    @Override
    protected void paintComponent(Graphics g) {

		// MAX FPS GRENZE SCHAFFEN
		long now = System.currentTimeMillis();
		try {
			if(nextRepaintDelay > now) {
				Thread.sleep(nextRepaintDelay - now);
		    }
		    nextRepaintDelay = now + 1000 / (maxFPS - 41);
		}catch (InterruptedException e) {}
		
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		MapData.map.draw(g);
		
		// DRAW FPS
		if(showFPS == true) {
		    g.setColor(Color.WHITE);
		    g.setFont(new Font("Arial", Font.BOLD, (int) (12)));
		    g.drawString("" + getCurrentFPSValue(), 0 + 2, 0 + 13);
		}
		
		// CALCULATE FPS
		calculateFPS();
		
		repaint();

    }

    /**
     * Berechnet und updatet die FPS
     */
    private void calculateFPS() {
		long currentTime = System.currentTimeMillis();
		if (currentTime > nextSecond) {
		    nextSecond += 1000;
		    framesInLastSecond = framesInCurrentSecond;
		    framesInCurrentSecond = 0;
		}
		framesInCurrentSecond++;
		displayedFPS = framesInLastSecond;
    }

    /**
     * Gibt die derzeitigen FPS an
     * 
     * @return {@link Integer}, die derzeitigen FPS
     */
    public int getCurrentFPSValue() {
    	return displayedFPS;
    }

    public void setShowFPS(boolean showFPS) {
    	this.showFPS = showFPS;
    }

    public boolean isShowingFPS() {
    	return showFPS;
    }

}
