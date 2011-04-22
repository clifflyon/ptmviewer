/*
 * PTMBrowser.java
 *
 * Created on September 15, 2004, 12:24 AM
 */

package jpview.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JApplet;
import javax.swing.JPanel;

import jpview.Utils;

/**
 * 
 * @author clyon
 */
public class PTMBrowser extends JApplet {

	private static PTMFrame ptmFrame = null;

	public void init() {

		try {
			String dir = this.getParameter("dir");
			String thumbs = this.getParameter("thumbs");
			LinkedList ll = new LinkedList();
			String[] pics = thumbs.split(",");
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(3, 3));
			panel.setBackground(new Color(50, 50, 80));

			/**
			 * For deployment, change to ImageIO...
			 */

			for (int i = 0; i < pics.length; i++) {
				String s = dir + pics[i];
				// System.out.println(s);
				// BufferedImage bi =
				// ImageIO.read(getClass().getResourceAsStream(s));
				BufferedImage bi = Utils.readUnbuffered(getClass()
						.getResourceAsStream(s));
				Thumbnail t = new Thumbnail(pics[i], bi, ptmFrame);
				panel.add(t);
			}
			thumbs = this.getParameter("thumbs2");
			ll = new LinkedList();
			pics = thumbs.split(",");
			for (int i = 0; i < pics.length; i++) {
				String s = dir + pics[i];
				BufferedImage bi = Utils.readUnbuffered(getClass()
						.getResourceAsStream(s));
				// BufferedImage bi =
				// ImageIO.read(getClass().getClassLoader().getResource(s));
				Thumbnail t = new Thumbnail(pics[i], bi, ptmFrame);
				panel.add(t);
			}
			thumbs = this.getParameter("thumbs3");
			ll = new LinkedList();
			pics = thumbs.split(",");
			for (int i = 0; i < pics.length; i++) {
				String s = dir + pics[i];
				BufferedImage bi = Utils.readUnbuffered(getClass()
						.getResourceAsStream(s));
				// BufferedImage bi =
				// ImageIO.read(getClass().getClassLoader().getResource(s));
				Thumbnail t = new Thumbnail(pics[i], bi, ptmFrame);
				panel.add(t);
			}
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(panel, BorderLayout.CENTER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
