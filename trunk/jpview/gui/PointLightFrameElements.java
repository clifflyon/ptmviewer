/*
 * SpecularFrameElements.java
 *
 * Created on September 18, 2004, 4:16 PM
 */

package jpview.gui;

import java.awt.Rectangle;

import jpview.transforms.LocalLightOp;
import jpview.transforms.PixelTransformOp;

/**
 * A collection of UI elements to control diffuse gain
 * 
 * @author clyon
 */
public class PointLightFrameElements implements FrameElements {

	private PixelTransformOp op = null;

	private PTMControls controls = null;

	/**
	 * Initializes the controls and operators for diffuse gain
	 * 
	 * @param parent
	 *            the PTMFrame containing the diffuse gain elements
	 */
	public void init(PTMWindow parent) {
		op = new LocalLightOp();
		parent.setPixelTransformOp(op);

		controls = new PointLightControls(parent);
		parent.setControls(controls);
		Rectangle r = new Rectangle((int) (parent.getWidth() * 0.03),
				(int) (parent.getHeight() * 0.03),
				(int) (parent.getWidth() * 0.94), 50);
		controls.setBounds(r);
		parent.getLayeredPane().add(controls, new Integer(1));
		controls.setVisible(true);
	}

	/**
	 * releases all resources associated with this class
	 */
	public void release() {
		op.release();
		op = null;
		if (controls != null) {
			controls.setVisible(false);
			controls.release();
			controls = null;
		}
	}
}
