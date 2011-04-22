/*
 * Thumbnail.java
 *
 * Created on September 15, 2004, 10:24 PM
 */

package jpview.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 * 
 * @author clyon
 */
public class Thumbnail extends JLabel implements MouseListener {
	private String imageName = null;

	private static PTMFrame ptmFrame = null;

	protected static Border MOUSE_ENTERED = BorderFactory
			.createLineBorder(java.awt.Color.WHITE);

	private static final Border MOUSE_CLICKED = BorderFactory
			.createLineBorder(java.awt.Color.RED);

	private static final Cursor DEFAULT_CURSOR = Cursor
			.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	private static final Cursor WAIT_CURSOR = Cursor
			.getPredefinedCursor(Cursor.WAIT_CURSOR);

	private static boolean acceptClicks = true;

	public Thumbnail(String name, BufferedImage bi, PTMFrame pf) {
		super(new ImageIcon(bi));
		imageName = name;
		this.setBackground(new Color(50, 50, 80));
		this.addMouseListener(this);
		ptmFrame = pf;
	}

	public Thumbnail(String name, PTMFrame pf) {
		super();
		imageName = name;
		this.addMouseListener(this);
		this.setBackground(new Color(50, 50, 80));
		ptmFrame = pf;
	}

	public void setImage(BufferedImage bi) {
		this.setIcon(new ImageIcon(bi));
		this.setHorizontalAlignment(JLabel.CENTER);
	}

	public void mouseClicked(java.awt.event.MouseEvent e) {
		if (!acceptClicks)
			return;
		acceptClicks = false;
		Thumbnail.this.getParent().setCursor(WAIT_CURSOR);
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		JFrame theFrame = new JFrame("Loading "
				+ imageName.substring(0, imageName.indexOf(".")) + ".ptm ...");
		Container contentPane = theFrame.getContentPane();
		contentPane.add(progressBar);
		theFrame.setSize(300, 100);
		theFrame.setLocationRelativeTo(null);
		theFrame.setVisible(true);

		if (ptmFrame != null) {
			ptmFrame.setVisible(false);
			ptmFrame.release();
			ptmFrame.dispose();
			System.gc();
		}
		loadPTM(imageName, theFrame);
	}

	public void mouseEntered(java.awt.event.MouseEvent e) {
		this.setBorder(MOUSE_ENTERED);
	}

	public void mouseExited(java.awt.event.MouseEvent e) {
		this.setBorder(null);
	}

	public void mousePressed(java.awt.event.MouseEvent e) {
		if (acceptClicks) {
			this.setBorder(MOUSE_CLICKED);
		}
	}

	public void mouseReleased(java.awt.event.MouseEvent e) {
	}

	public void loadPTM(String name, JFrame f) {
		final String imageName = name;
		final JFrame frame = f;
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				File file = new File(imageName);
				String s = file.getName();
				String s1 = s.substring(0, s.indexOf("."));
				try {
					ptmFrame = new PTMFrame("../ptmfiles/" + s1 + ".ptm",
							getParent());
				} catch (java.lang.Exception ioe) {
					// clean up
					frame.setVisible(false);
					frame.dispose();
					ptmFrame = null;
					Thumbnail.this.getParent().setCursor(
							new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null, "Failed to load " + s1
							+ ".ptm !", "Exception", JOptionPane.ERROR_MESSAGE);
					acceptClicks = true;
					return null;

				}
				// ptmFrame = new PTMFrame( "ptmfiles/" + s1 + ".ptm");
				ptmFrame.setTitle(s1);

				int w = ptmFrame.w;
				int h = ptmFrame.h;

				if (h > 600) {
					float ratio = 600 / ((float) h);
					w *= ratio;
					h *= ratio;
				}

				if (w > 800) {
					float ratio = 800 / ((float) w);
					w *= ratio;
					h *= ratio;
				}

				final PTMFrame ptmf = ptmFrame;
				final int _w = w;
				final int _h = h;
				ptmf.getPTMCanvas().setPreferredSize(new Dimension(_w, _h));
				// ptmf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				ptmf.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						ptmf.release();
						ptmf.dispose();
					}
				});
				ptmf.pack();
				// ptmf.setLocationRelativeTo(null);
				ptmf.setVisible(true);
				Thumbnail.this.getParent().setCursor(
						new Cursor(Cursor.DEFAULT_CURSOR));
				System.gc();
				acceptClicks = true;
				frame.setVisible(false);
				frame.dispose();
				return ptmFrame;
			}
		};
		worker.start();
	}
}
