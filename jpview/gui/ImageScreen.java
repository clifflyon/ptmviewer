/*
 * ImageScreen.java
 *
 * Created on September 5, 2004, 2:10 PM
 */

package jpview.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * 
 * @author clyon
 */
public class ImageScreen extends JComponent {
	private BufferedImage image;

	private int lightX = -1;

	private int lightY = -1;

	boolean drawn = false;

	public ImageScreen(BufferedImage bi) {
		super();
		image = bi;
	}

	public void paintComponent(Graphics g) {
		Rectangle r = this.getBounds();
		Graphics2D g2d = (Graphics2D) g;
		if (image != null) {
			g.drawImage(image, 0, 0, r.width, r.height, this);
		}
		if (lightX >= 0 || lightY >= 0) {
			g2d.setColor(Color.YELLOW);
			g2d.drawRect(lightX, lightY, 1, 1);
		}
	}

	public void setPixel(int x, int y) {
		lightX = x;
		lightY = y;
	}
}