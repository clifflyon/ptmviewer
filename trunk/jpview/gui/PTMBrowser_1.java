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
public class PTMBrowser_1 extends JApplet {

	private static PTMFrame ptmFrame = null;

	public void init() {

		// JFrame.setDefaultLookAndFeelDecorated(true);

		try {

			this.getContentPane().setBackground(new Color(50, 50, 80));
			String dir = this.getParameter("dir");
			String thumbs = this.getParameter("thumbs");
			LinkedList ll = new LinkedList();
			String[] pics = thumbs.split(",");
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(3, 3, 5, 5));
			panel.setBackground(new Color(50, 50, 80));

			/**
			 * For deployment, change to ImageIO...
			 */
			for (int i = 0; i < pics.length; i++) {
				Thumbnail t = new Thumbnail(pics[i], ptmFrame);
				loadImageForThumb(t, dir, pics[i]);
				panel.add(t);
			}
			thumbs = this.getParameter("thumbs2");
			ll = new LinkedList();
			pics = thumbs.split(",");
			for (int i = 0; i < pics.length; i++) {
				Thumbnail t = new Thumbnail(pics[i], ptmFrame);
				loadImageForThumb(t, dir, pics[i]);
				panel.add(t);
			}
			thumbs = this.getParameter("thumbs3");
			ll = new LinkedList();
			pics = thumbs.split(",");
			for (int i = 0; i < pics.length; i++) {
				Thumbnail t = new Thumbnail(pics[i], ptmFrame);
				loadImageForThumb(t, dir, pics[i]);
				panel.add(t);
			}
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(panel, BorderLayout.CENTER);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage(String path, String name) {

		BufferedImage bi = null;

		try {
			// bi =
			// Utils.readUnbuffered(getClass().getResourceAsStream(path+name));
			bi = Utils.readUnbuffered(getClass().getClassLoader().getResource(
					path + name).openStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bi;
	}

	public void loadImageForThumb(final Thumbnail t, final String path,
			final String name) {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				t.setImage(getImage(path, name));
				return t;
			}
		};
		worker.start();
	}
}
