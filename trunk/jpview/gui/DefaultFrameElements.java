/*
 * SpecularFrameElements.java
 *
 * Created on September 18, 2004, 4:16 PM
 */

package jpview.gui;

import jpview.transforms.DirectionalLightOp;
import jpview.transforms.PixelTransformOp;

/**
 * Default frame elements is a container for the simple directional light
 * transformation
 * 
 * @author clyon
 */
public class DefaultFrameElements implements FrameElements {

	private PixelTransformOp op = null;

	/**
	 * Initializes default frame elements
	 * 
	 * @param parent
	 *            The PTMFrame for which this class represents the default
	 *            elements
	 */
	public void init(PTMWindow parent) {
		op = new DirectionalLightOp();
		parent.setPixelTransformOp(op);
	}

	/**
	 * Release all resources associated with this class
	 */
	public void release() {
		op.release();
		op = null;
	}

}
