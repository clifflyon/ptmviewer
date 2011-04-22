/*
 * PTMControls.java
 *
 * Created on September 18, 2004, 1:01 AM
 */

package jpview.gui;

import java.awt.Graphics;

import javax.swing.JInternalFrame;

/**
 * 
 * @author clyon
 */
public abstract class PTMControls extends JInternalFrame {
	public abstract void release();

	protected void paintComponent(Graphics g) {
		g.setColor(Defaults.SEE_THROUGH_GREY);
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

}
