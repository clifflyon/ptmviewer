/*
 * Rotation.java
 *
 * Created on September 28, 2004, 10:40 PM
 */

package jpview.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import jpview.Utils;

/**
 * 
 * @author clyon
 */
public class Rotation {

	static int[] pixels;

	static int[] lookup;

	/** Creates a new instance of Rotation */
	public Rotation() {
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		try {
			int[] buf;
			BufferedImage bi = ImageIO.read(new File(args[0]));
			BufferedImage original = new BufferedImage(bi.getWidth(), bi
					.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) original.getGraphics();
			g.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
			pixels = Utils.grabPixels(original);
			lookup = new int[pixels.length];
			int radius = Math.min(original.getWidth() / 2,
					original.getHeight() / 2);
			for (int i = 0; i < original.getWidth(); i++) {
				for (int j = 0; j < original.getHeight(); j++) {
					int offset = j * original.getWidth() + i;
					int x = i - original.getWidth() / 2;
					int y = j - original.getHeight() / 2;
					double dist = Math.sqrt(x * x + y * y);
					if (dist > radius) {
						lookup[offset] = offset;
					} else {
						double a = Math.toRadians(10);
						int ppx = (int) Math.round(x * Math.cos(a) + y
								* Math.sin(a));
						int ppy = (int) Math.round(y * Math.cos(a) - x
								* Math.sin(a));
						ppx += original.getWidth() / 2;
						ppy += original.getHeight() / 2;
						if (ppx < 0)
							ppx = 0;
						if (ppy < 0)
							ppy = 0;
						if (ppx > original.getWidth() - 1)
							ppx = original.getWidth() - 1;
						if (ppy > original.getHeight() - 1)
							ppy = original.getHeight() - 1;

						lookup[offset] = ppy * original.getWidth() + ppx;
					}
				}
			}

			buf = new int[pixels.length];

			JFrame f = new JFrame("map");
			ImageScreen is = new ImageScreen(original);
			is.setPreferredSize(new Dimension(original.getWidth(), original
					.getHeight()));
			f.getContentPane().add(is);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.pack();
			f.setVisible(true);
			// f.show();

			Point p = new Point(bi.getWidth() / 4, bi.getHeight() / 4);

			Thread.sleep(3000);

			for (int i = 0; i < 2; i++) {
				int px = p.x;
				int py = p.y;

				px -= bi.getWidth() / 2;
				py -= bi.getHeight() / 2;

				double a = Math.toRadians(i);
				int ppx = (int) Math.round(px * Math.cos(a) + py * Math.sin(a));
				int ppy = (int) Math.round(py * Math.cos(a) - px * Math.sin(a));

				ppx += bi.getWidth() / 2;
				ppy += bi.getHeight() / 2;

				is.setPixel(ppx, ppy);

				for (int ii = 0; ii < buf.length; ii++) {
					buf[ii] = pixels[lookup[ii]];
				}

				for (int ii = 0; ii < buf.length; ii++) {
					pixels[ii] = buf[ii];
				}

				is.repaint();
				// Thread.currentThread().sleep(1000);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
