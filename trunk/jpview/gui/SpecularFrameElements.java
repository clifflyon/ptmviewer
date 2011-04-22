/*
 * SpecularFrameElements.java
 *
 * Created on September 18, 2004, 4:16 PM
 */

package jpview.gui;

import java.awt.Rectangle;

import jpview.transforms.PixelTransformOp;
import jpview.transforms.SpecularOp;

/**
 * 
 * @author clyon
 */
public class SpecularFrameElements implements FrameElements {

	private PixelTransformOp op = null;

	private PTMControls controls = null;

	public void init(PTMWindow parent) {
		op = new SpecularOp();
		parent.setPixelTransformOp(op);

		controls = new SpecularControls(parent);
		parent.setControls(controls);
		Rectangle r = new Rectangle((int) (parent.getWidth() * 0.03),
				(int) (parent.getHeight() * 0.03),
				(int) (parent.getWidth() * 0.94), 100);
		controls.setBounds(r);
		parent.getLayeredPane().add(controls, new Integer(1));
		controls.setVisible(true);
	}

	public void release() {
		op.release();
		op = null;
		if (controls != null) {
			controls.setVisible(false);
			controls.release();
			controls.dispose();
			controls = null;
		}
	}
}
