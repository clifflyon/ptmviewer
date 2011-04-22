/*
 * PTMWindow.java
 *
 * Created on September 19, 2004, 10:36 PM
 */

package jpview.gui;

import java.awt.Container;

import javax.swing.JLayeredPane;

import jpview.graphics.EnvironmentMap;
import jpview.ptms.PTM;
import jpview.transforms.PixelTransformOp;

/**
 * 
 * @author clyon
 */
public interface PTMWindow {

	public void fireTransform();

	public float getKSpec();

	public float getKDiff();

	public int getExp();

	public int getPTMWidth();

	public int getWidth();

	public int getHeight();

	public int getPTMHeight();

	public void setKSpec(float f);

	public void setKDiff(float f);

	public void setExp(int i);

	public void setControls(PTMControls c);

	public float getLuminance();

	public void setLuminance(float f);

	public void setMapSampleSize(int i);

	public void setMapBlurType(int i);

	public void setMapKernelSize(int i);

	public void setMapGuassianBlurSigma(float f);

	public void refreshMap();

	public PTM getPTM();

	public void forceUpdate();

	public void setBrowser(Container c);

	public void setEnvironmentMap(EnvironmentMap e);

	public void setPixelTransformOp(PixelTransformOp pto);

	public JLayeredPane getLayeredPane();

}
