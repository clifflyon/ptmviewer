package jpview.graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jpview.gui.PTMCanvas;
import jpview.io.PTMIO;
import jpview.ptms.PTM;
import jpview.transforms.DirectionalLightOp;
import jpview.transforms.PixelTransformOp;

public class Example implements MouseMotionListener {
	PixelTransformOp pixelTransformOp = new DirectionalLightOp();

	PTMCanvas canvas = null;

	PTM ptm = null;

	int mouseX = 0;

	int mouseY = 0;

	public Example(String ptmFileName) {
		try {
			ptm = PTMIO
					.getPTMParser(new FileInputStream(new File(ptmFileName)))
					.readPTM();
			canvas = PTMCanvas.createPTMCanvas(ptm.getWidth(), ptm.getHeight(),
					PTMCanvas.BUFFERED_IMAGE);
			pixelTransformOp.transformPixels(canvas.getPixels(), ptm, ptm
					.getWidth() / 2, ptm.getHeight() / 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mouseMoved(MouseEvent mouseEvent) {
	}

	public void mouseDragged(MouseEvent e) {
		int x = Math.min(Math.max(e.getX(), 0), ptm.getWidth());
		int y = Math.min(Math.max(e.getY(), 0), ptm.getHeight());
		mouseX = x;
		mouseY = y;
		pixelTransformOp.transformPixels(canvas.getPixels(), ptm, x, y);
		canvas.paintImmediately(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private static void createAndShowGUI(String name) {
		Example example = new Example(name);
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(example.canvas);
		frame.addMouseMotionListener(example);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			final String name = args[0];
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI(name);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
