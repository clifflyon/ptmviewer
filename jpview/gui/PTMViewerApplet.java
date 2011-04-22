/*
 * PTMViewer.java
 *
 * Created on September 6, 2004, 10:38 PM
 */

package jpview.gui;

import java.awt.Container;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;

import jpview.graphics.EnvironmentMap;
import jpview.graphics.Vec3f;
import jpview.io.PTMIO;
import jpview.ptms.PTM;
import jpview.transforms.PixelTransformOp;
import jpview.transforms.SpecularOp;

/**
 * 
 * @author clyon
 */
public class PTMViewerApplet extends JApplet implements MouseMotionListener,
		PTMWindow, Runnable {
	private PTM ptm = null;

	private PTMCanvas ptmCanvas = null;

	private PixelTransformOp pixelTransformOp = null;

	private EnvironmentMap em = null;

	private PTMControls controls = null;

	private int mouseX, mouseY;

	ImageScreen is = null;

	private Object lock = new short[0];

	public void init() {
		try {
			ptm = PTMIO.getPTMParser(
					getClass().getClassLoader().getResourceAsStream(
							getParameter("ptmfile"))).readPTM();
			// ptm = new Ellipsoid(512,512);

			/** Environment map - read the whole thing manually */
			InputStream in = getClass().getClassLoader().getResourceAsStream(
					getParameter("map"));
			// = url.openStream();
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			int b;
			while ((b = in.read()) != -1) {
				bOut.write(b);
			}
			ByteArrayInputStream bIn = new ByteArrayInputStream(bOut
					.toByteArray());
			BufferedImage map = ImageIO.read(bIn);
			em = new EnvironmentMap(map, ptm);
			ptm.setEnvironmentMap(em);
			// EnvironmentMapFrame emf = new EnvironmentMapFrame(em,this);
			// emf.setVisible(true);
			// emf.show();

			ptmCanvas = PTMCanvas.createPTMCanvas(ptm.getWidth(), ptm
					.getHeight(), PTMCanvas.BUFFERED_IMAGE);
			getContentPane().add(ptmCanvas);

			pixelTransformOp = new SpecularOp();
			// pixelTransformOp = new EnvironmentMapOp();
			// pixelTransformOp = new DirectionalLightOp();

			mouseX = ptm.getWidth() / 2;
			mouseY = ptm.getHeight() / 2;
			this.fireTransform();

			addMouseMotionListener(this);

			ColorConvertOp op = new ColorConvertOp(ColorSpace
					.getInstance(ColorSpace.CS_GRAY), null);
			BufferedImage grayImage = op.filter(map, null);
			//
			// JFrame f = new JFrame();
			//
			// is = new ImageScreen(grayImage);
			// f.getContentPane().add(is);
			// f.setVisible(true);
			// f.setSize(512,512+26);
			// f.show();

			JFrame controls = new EnvControls(this);
			controls.pack();
			controls.setLocation(new Point(this.getWidth() + 10, this
					.getLocation().y + 26));
			controls.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mouseDragged(java.awt.event.MouseEvent e) {
		int x = Math.min(Math.max(e.getX(), 0), ptm.getWidth());
		int y = Math.min(Math.max(e.getY(), 0), ptm.getHeight());
		mouseX = x;
		mouseY = y;
		pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm, x, y);
		ptmCanvas.paintImmediately(0, 0, ptm.getWidth(), ptm.getHeight());
	}

	public void mouseMoved(java.awt.event.MouseEvent e) {
		int x = Math.min(Math.max(e.getX(), 0), ptm.getWidth());
		int y = Math.min(Math.max(e.getY(), 0), ptm.getHeight());
		Vec3f N = ptm.normal(x, y);
		Vec3f R = Vec3f.reflect(ptm.normal(x, y), new Vec3f(0, 0, 1));
		float m = (float) Math.sqrt(2 * (R.z() + 1));
		float u = R.x() / m;
		float v = R.y() / m;
		int u1 = Math.round((u + 1) * ptm.getWidth() / 2);
		int v1 = Math.round((v + 1) * ptm.getHeight() / 2);
		showStatus(x + "," + y + " " + N + " " + R + " " + "(" + u1 + "," + v1
				+ ")");

		if (is != null) {
			is.setPixel(u1, v1);
			is.repaint();
		}

		// Vec3f [] normals = new Vec3f[] {
		// ptm.normal(x-1,y-1), ptm.normal(x,y-1), ptm.normal(x+1,y-1),
		// ptm.normal(x-1,y), ptm.normal(x,y), ptm.normal(x+1,y),
		// ptm.normal(x-1,y+1), ptm.normal(x,y+1), ptm.normal(x+1,y+1)
		// };
		// em.updateMonitor(normals);
	}

	public PTM getPTM() {
		return ptm;
	}

	public void fireTransform() {
		if (pixelTransformOp != null)
			pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm,
					mouseX, mouseY);
		if (ptmCanvas != null)
			ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(), ptmCanvas
					.getHeight());
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
		ptm.getEnvironmentMap().setSampleSize(i);
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

	public void start() {
		if (animated) {
			animator = new Thread(this);
			start = System.currentTimeMillis();
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
		while (Thread.currentThread() == animator) {
			em.setAngle(angle % 360);
			// ptm.recache();
			fireTransform();
			angle += 5;
		}
	}

	public void forceUpdate() {
		this.pixelTransformOp.forceUpdate();
	}

	public void setBrowser(Container c) {

	}

	public void setEnvironmentMap(EnvironmentMap e) {
	}

	public void setPixelTransformOp(PixelTransformOp pto) {
	}

	public void setControls(PTMControls c) {
	}

	private boolean animated = false;

	private Thread animator = null;

	private long start = 0;

	int angle = 0;
}
