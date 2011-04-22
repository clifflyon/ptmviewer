/*
 * PTMFrame.java
 *
 * Created on September 15, 2004, 10:27 PM
 */

package jpview.gui;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import jpview.graphics.EnvironmentMap;
import jpview.io.PTMIO;
import jpview.ptms.PTM;
import jpview.transforms.DiffuseGainOp;
import jpview.transforms.DirectionalLightOp;
import jpview.transforms.LocalLightOp;
import jpview.transforms.NormalMapOp;
import jpview.transforms.PixelTransformOp;
import jpview.transforms.ReflectionMapOp;
import jpview.transforms.SpecularOp;

/**
 * 
 * @author clyon
 */
public class PTMFrame extends JFrame implements MouseMotionListener,
		ActionListener, MouseListener, PTMWindow, Runnable {
	/** add mutators for w x h */
	public int w;

	public int h;

	private PixelTransformOp pixelTransformOp = null;

	private PTM ptm = null;

	public PTMCanvas ptmCanvas = null;

	private PTMRightMouseMenu popup = null;

	private PTMControls controls = null;

	private FrameElements frameElements = null;

	private int mouseX = 0, mouseY = 0, frames = 0, angle = 0;

	private boolean animated = false;

	private Thread animator;

	private long start;

	public static Object mutex = new short[0];

	private EnvironmentMap em = null;

	private boolean justDragged = false;

	/* TODO: separate shared + static components */
	public void release() {
		if (fc != null) {
			fc.setVisible(false);
			fc.removeAll(); /* add release code */
			fc.dispose();
			fc = null;
		}
		if (pixelTransformOp != null) {
			pixelTransformOp.release();
			pixelTransformOp = null;
		}
		if (ptm != null) {
			ptm.release();
			ptm = null;
		}
		if (controls != null) {
			controls.release();
			controls = null;
		}
	}

	public PTMControls getControls() {
		return controls;
	}

	public PTM getPTM() {
		return ptm;
	}

	public PTMCanvas getPTMCanvas() {
		return ptmCanvas;
	}

	public PixelTransformOp getPixelTransformOp() {
		return pixelTransformOp;
	}

	public void setControls(PTMControls c) {
		controls = c;
	}

	public void setPTM(PTM m) {
		ptm = m;
	}

	public void setCanvas(PTMCanvas c) {
		ptmCanvas = c;
	}

	public int mouseX() {
		return mouseX;
	}

	public int mouseY() {
		return mouseY;
	}

	private JScrollPane pane = null;

	private int progress = 0;

	public int getProgress() {
		return progress;
	}

	private Container parent = null;

	private FloatingControls fc = new FloatingControls();

	public FloatingControls getFloatingControls() {
		return fc;
	}

	public PTMFrame(String name, Container c) throws java.io.IOException,
			java.lang.Exception {

		// change back for deployment
		// ptm =
		// PTMIO.getPTMParser(getClass().getClassLoader().getResourceAsStream(name)).readPTM();
		parent = c;
		ptm = PTMIO.getPTMParser(getClass().getResourceAsStream(name))
				.readPTM();
		w = ptm.getWidth();
		h = ptm.getHeight();

		ptmCanvas = PTMCanvas.createPTMCanvas(ptm.getWidth(), ptm.getHeight(),
				PTMCanvas.BUFFERED_IMAGE);
		// getContentPane().add(ptmCanvas);
		pane = new JScrollPane(ptmCanvas,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		getContentPane().add(pane);

		pixelTransformOp = new DirectionalLightOp();
		mouseX = ptm.getWidth() / 2;
		mouseY = ptm.getHeight() / 2;
		pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm, mouseX,
				mouseY);
		popup = new PTMRightMouseMenu(this);
		addMouseMotionListener(PTMFrame.this);
		addMouseListener(PTMFrame.this);
		pane.getViewport().addMouseMotionListener(PTMFrame.this);
		pane.getViewport().addMouseListener(PTMFrame.this);
		pack();

		fc.setPTMWindow(this);
		fc.pack();
		fc.setLocation(new Point(ptm.getWidth() + 28, this.getLocation().y));
		fc.setVisible(true);

	}

	public void mouseDragged(java.awt.event.MouseEvent e) {
		synchronized (mutex) {
			stop();
			int x = Math.min(Math.max(e.getX(), 0), ptm.getWidth());
			int y = Math.min(Math.max(e.getY(), 0), ptm.getHeight());
			mouseX = x;
			mouseY = y;
			pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm, x, y);
			ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(), ptmCanvas
					.getHeight());
			justDragged = true;
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void setPixelTransformOp(PixelTransformOp pto) {
		pixelTransformOp = pto;
	}

	public void start() {
		if (animated) {
			animator = new Thread(this);
			animator.setPriority(Thread.MIN_PRIORITY);
			start = System.currentTimeMillis();
			frames = 0;
			animator.start();
		}
	}

	public void stop() {
		animator = null;
	}

	/**
	 * This method is called by the thread that was created in the start method.
	 * It does the main animation.
	 */
	public void run() {
		float anglex = 0;
		float angley = 0;

		int w, h;
		w = (ptm.getWidth() - 1) / 2;
		h = (ptm.getHeight() - 1) / 2;

		for (int i = 0; i < 360; i++) {
			int tryX = (int) ((Math.cos(Math.toRadians(i)) * w) + w);
			if (tryX == mouseX) {
				anglex = (float) Math.toRadians(i);
			}
			int tryY = (int) ((Math.sin(Math.toRadians(i)) * h) + h);
			if (tryY == mouseY) {
				angley = (float) Math.toRadians(i);
			}
		}

		while (Thread.currentThread() == animator) {
			frames++;
			if (this.getPTM() != null && this.getPTM().useEnv()) {
				angle = ((angle + 3) % 360);
				this.getPTM().getEnvironmentMap().setAngle(angle);
			}
			anglex = anglex + 0.02f;
			angley = angley + 0.03f;
			synchronized (mutex) {
				try {
					mouseX = (int) ((Math.cos(anglex) * w) + w);
					mouseY = (int) ((Math.sin(angley) * h) + h);
					this.fireTransform();
				} catch (java.lang.NullPointerException npe) {
					break; /* user closed the window */
				}
			}

			// if ( frames % 50 == 0 ) {
			// float elapsed = (float) ((System.currentTimeMillis() - start ));
			// elapsed /= 1000;
			// System.out.print( frames + "\t" + elapsed + "\t" );
			//
			// System.out.println(
			// ((float) frames) / elapsed
			// );
			// }

		}
	}

	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		synchronized (mutex) {

			if (cmd.equals("Animate")) {

				animated = popup.isAnimated();
				if (animated)
					start();
				else
					stop();

				/**
				 * P E R F O R M A N C E
				 */

			} else if (cmd.equals("Sampling")) {

				clearFrameElements();
				ptmCanvas.speed();
				pixelTransformOp.clearCache();

			} else if (cmd.equals("Detail")) {

				clearFrameElements();
				ptmCanvas.detail();
				pixelTransformOp.clearCache();

			} else if (cmd.equals("Interpolated Sampling")) {

				clearFrameElements();
				ptmCanvas.speed();
				ptmCanvas.useHint(true);
				pixelTransformOp.clearCache();

			}

			/**
			 * L I G H T S O U R C E
			 */

			else if (cmd.equals("Directional")) {

				fc.setSpecularEnabled(false);
				fc.setDiffuseGainEnabled(false);
				fc.setDistanceEnabled(false);
				pixelTransformOp = new DirectionalLightOp();

			} else if (cmd.equals("Local")) {

				fc.setSpecularEnabled(false);
				fc.setDiffuseGainEnabled(false);
				fc.setDistanceEnabled(true);
				pixelTransformOp = new LocalLightOp();

			} else if (cmd.equals("Spotlight")) {

				fc.setSpecularEnabled(false);
				fc.setDiffuseGainEnabled(false);
				fc.setDistanceEnabled(true);
				pixelTransformOp = new LocalLightOp();
				((LocalLightOp) pixelTransformOp).setFlashlight(true);

			} else if (cmd.equals("Specular")) {

				clearFrameElements();
				fc.setSpecularEnabled(true);
				fc.setDiffuseGainEnabled(false);
				fc.setDistanceEnabled(false);
				pixelTransformOp = new SpecularOp();
				popup.setForEffects();

			} else if (cmd.equals("Effects Off")) {

				clearFrameElements();
				fc.setSpecularEnabled(false);
				fc.setDiffuseGainEnabled(false);
				fc.setDistanceEnabled(false);
				pixelTransformOp = new DirectionalLightOp();
				popup.enableAll();

			} else if (cmd.equals("Diffuse Gain")) {

				clearFrameElements();
				fc.setSpecularEnabled(false);
				fc.setDiffuseGainEnabled(true);
				fc.setDistanceEnabled(false);
				pixelTransformOp = new DiffuseGainOp();
				popup.setForEffects();

			} else if (cmd.equals("Environment Mapping")) {

				notImplemented();

			} else if (cmd.equals("Visualize Normals")) {

				clearFrameElements();
				fc.setSpecularEnabled(false);
				fc.setDiffuseGainEnabled(false);
				fc.setDistanceEnabled(false);

				pixelTransformOp = new NormalMapOp();

			} else if (cmd.equals("Visualize Reflection")) {

				clearFrameElements();
				pixelTransformOp = new ReflectionMapOp();

			} else if (cmd.equals("Defaults")) {

				clearFrameElements();
				ptmCanvas.detail();
				fc.setSpecularEnabled(false);
				fc.setDiffuseGainEnabled(false);
				fc.setDistanceEnabled(false);
				pixelTransformOp = new DirectionalLightOp();
				popup.enableAll();
				popup.effectsOff();

			} else {
				System.out.println("Unknown: " + cmd);
			}

			// System.out.println( this.pixelTransformOp.getClass().getName());
			// System.out.println(ptmCanvas.getPixels().length);

			if (!animated) {
				pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm,
						mouseX, mouseY);
				ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(),
						ptmCanvas.getHeight());
			}
		}

		System.gc();
	}

	public void mouseClicked(MouseEvent e) {
		handlePopup(e);
		this.repaint();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		handlePopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		handlePopup(e);
		if (animated && justDragged) {
			start();
			justDragged = false;
		}

	}

	public void handlePopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
			repaint();
			e.consume();
		}
	}

	public void fireTransform() {
		synchronized (mutex) {
			pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm,
					mouseX, mouseY);
			ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(), ptmCanvas
					.getHeight());
		}
	}

	public void notImplemented() {
		JOptionPane
				.showMessageDialog(this, "This feature is not available yet");
	}

	private void clearFrameElements() {
		if (frameElements != null) {
			frameElements.release();
			frameElements = null;
			System.gc();
		}
	}

	public static void main(String args[]) {
		try {
			PTMFrame me = new PTMFrame(args[0], null);
			// me.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			me.setSize(me.getPTM().getWidth(), me.getPTM().getHeight() + 26);
			me.setVisible(true);
			// me.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getExp() {
		return ptm.getExp();
	}

	public float getKDiff() {
		return ptm.getKDiff();
	}

	public float getKSpec() {
		return ptm.getKSpec();
	}

	public int getPTMHeight() {
		return ptm.getHeight();
	}

	public int getPTMWidth() {
		return ptm.getWidth();
	}

	public void setKSpec(float f) {
		ptm.setKSpec(f);
	}

	public void setKDiff(float f) {
		ptm.setKDiff(f);
	}

	public void setExp(int i) {
		ptm.setExp(i);
	}

	public float getLuminance() {
		return ptm.getLuminance();
	}

	public void setLuminance(float f) {
		ptm.setLuminance(f);
	}

	public void refreshMap() {
		ptm.getEnvironmentMap().refresh();
		ptm.recache();
	}

	public void setMapSampleSize(int i) {
		if (ptm.getEnvironmentMap() != null) {
			ptm.getEnvironmentMap().setSampleSize(i);
		}
	}

	public void setMapBlurType(int i) {
		switch (i) {
		case EnvironmentMap.BLUR_TYPE_SIMPLE:
			ptm.getEnvironmentMap().setSimpleBlur();
			break;
		case EnvironmentMap.BLUR_TYPE_GAUSSIAN:
			ptm.getEnvironmentMap().setGaussianBlur();
			break;
		case EnvironmentMap.BLUR_TYPE_NONE:
		default:
			ptm.getEnvironmentMap().setNoBlur();
		}
	}

	public void setMapGuassianBlurSigma(float f) {
		ptm.getEnvironmentMap().setGaussianSigma(f);
	}

	public void setMapKernelSize(int i) {
		ptm.getEnvironmentMap().setBlurKernelSize(i);
	}

	public void forceUpdate() {
		pixelTransformOp.forceUpdate();
	}

	public void setBrowser(Container c) {
		System.out.println("setting: " + c);
		parent = c;
	}

	public void setEnvironmentMap(EnvironmentMap e) {
		em = e;
		ptm.setEnvironmentMap(em);

	}

}
