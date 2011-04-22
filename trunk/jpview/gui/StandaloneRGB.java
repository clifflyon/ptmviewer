package jpview.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JApplet;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import jpview.graphics.EnvironmentMap;
import jpview.io.PTMIO;
import jpview.ptms.PTM;
import jpview.transforms.DirectionalLightOp;
import jpview.transforms.LocalLightOp;
import jpview.transforms.NormalMapOp;
import jpview.transforms.PixelTransformOp;
import jpview.transforms.ReflectionMapOp;

public class StandaloneRGB extends JApplet implements ActionListener,
		MouseListener, MouseMotionListener, Runnable, PTMWindow {

	private PTM ptm = null;

	private PTMCanvas ptmCanvas = null;

	private int mouseX = 0;

	private int mouseY = 0;

	private PixelTransformOp pixelTransformOp = null;

	private PTMRightMouseMenu popup = null;

	private FrameElements frameElements = null;

	private short[] mutex = new short[0];

	private boolean animated = false;

	private Thread animator = null;

	private long start = 0;

	private int frames = 0;

	public void init() {
		String name = this.getParameter("ptmfile");
		int red = Integer.parseInt(this.getParameter("bg_red"));
		int green = Integer.parseInt(this.getParameter("bg_green"));
		int blue = Integer.parseInt(this.getParameter("bg_blue"));
		this.getContentPane().setBackground(new Color(red, green, blue));
		try {
			// name =
			// "../ptmfiles/mummysmall.ptm";

			ptm = PTMIO.getPTMParser(getClass().getResourceAsStream(name))
					.readPTM();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		ptmCanvas = PTMCanvas.createPTMCanvas(ptm.getWidth(), ptm.getHeight(),
				PTMCanvas.BUFFERED_IMAGE);
		ptmCanvas.fixedSize(true);
		((PTMCanvasBufferedImage) ptmCanvas).displayWidth = Integer
				.parseInt(this.getParameter("pw"));
		((PTMCanvasBufferedImage) ptmCanvas).displayHeight = Integer
				.parseInt(this.getParameter("ph"));
		getContentPane().add(ptmCanvas);

		pixelTransformOp = new DirectionalLightOp();
		mouseX = ptm.getWidth() / 2;
		mouseY = ptm.getHeight() / 2;
		pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm, mouseX,
				mouseY);
		popup = new PTMRightMouseMenu(this);
		JMenuItem lum = new JMenuItem("Luminance");
		popup.addSeparator();
		popup.add(lum);
		lum.addActionListener(this);
		getContentPane().addMouseMotionListener(this);
		getContentPane().addMouseListener(this);
		// PTMFrame frame = new PTMFrame ( name,this );
		// Standalone.this.setContentPane ( frame.getContentPane () );
	}

	public void mouseDragged(MouseEvent e) {
		synchronized (mutex) {
			stop();
			int x = Math.min(Math.max(e.getX(), 0), ptm.getWidth());
			int y = Math.min(Math.max(e.getY(), 0), ptm.getHeight());
			mouseX = x;
			mouseY = y;
			pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm, x, y);
			ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(), ptmCanvas
					.getHeight());
		}
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
			anglex = anglex + 0.02f;
			angley = angley + 0.03f;
			synchronized (mutex) {
				try {
					mouseX = (int) ((Math.cos(anglex) * w) + w);
					mouseY = (int) ((Math.sin(angley) * h) + h);
					pixelTransformOp.transformPixels(ptmCanvas.getPixels(),
							ptm, mouseX, mouseY);
					;
					ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(),
							ptmCanvas.getHeight());
				} catch (java.lang.NullPointerException npe) {
					break; /* user closed the window */
				}
			}
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

				ptmCanvas.speed();
				pixelTransformOp.clearCache();

			} else if (cmd.equals("Detail")) {

				ptmCanvas.detail();
				pixelTransformOp.clearCache();

			} else if (cmd.equals("Interpolated Sampling")) {

				ptmCanvas.speed();
				ptmCanvas.useHint(true);
				pixelTransformOp.clearCache();

			}

			/**
			 * L I G H T S O U R C E
			 */

			else if (cmd.equals("Directional")) {

				clearFrameElements();
				frameElements = new DefaultFrameElements();
				frameElements.init(this);

			} else if (cmd.equals("Local")) {

				clearFrameElements();
				frameElements = new PointLightFrameElements();
				frameElements.init(this);

			} else if (cmd.equals("Spotlight")) {

				clearFrameElements();
				frameElements = new PointLightFrameElements();
				frameElements.init(this);
				((LocalLightOp) pixelTransformOp).setFlashlight(true);

			} else if (cmd.equals("Specular")) {

				clearFrameElements();
				popup.setForEffects();
				frameElements = new SpecularFrameElements();
				frameElements.init(this);

			} else if (cmd.equals("Effects Off")) {

				clearFrameElements();
				popup.effectsOff();
				frameElements = new DefaultFrameElements();
				frameElements.init(this);

			} else if (cmd.equals("Diffuse Gain")) {

				clearFrameElements();
				popup.setForEffects();
				frameElements = new DiffuseGainFrameElements();
				frameElements.init(this);

			} else if (cmd.equals("Environment Mapping")) {

				notImplemented();

			} else if (cmd.equals("Visualize Normals")) {

				clearFrameElements();
				pixelTransformOp = new NormalMapOp();

			} else if (cmd.equals("Visualize Reflection")) {

				clearFrameElements();
				pixelTransformOp = new ReflectionMapOp();

			} else if (cmd.equals("Defaults")) {

				clearFrameElements();
				popup.effectsOff();
				popup.enableAll();
				ptmCanvas.detail();
				frameElements = new DefaultFrameElements();
				frameElements.init(this);

			} else if (cmd.equals("Luminance")) {

				frameElements = new LuminanceFrameElements();
				frameElements.init(this);

			} else {
				System.out.println("Unknown: " + cmd);
			}

			// System.out.println( this.pixelTransformOp.getClass().getName());
			// System.out.println(ptmCanvas.getPixels().length);

			if (!animated) {
				pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm,
						mouseX, mouseY);
				/** use the canvas width x height ?? */
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
		start();
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

	public PTM getPTM() {
		return ptm;
	}

	public void setBrowser(Container c) {
	}

	public void setControls(PTMControls c) {
	}

	public void setEnvironmentMap(EnvironmentMap e) {
	}

	public void mouseMoved(java.awt.event.MouseEvent me) {
		;
	}

}