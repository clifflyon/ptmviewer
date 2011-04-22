package jpview.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeListener;

/*
 * GUIControls.java
 *
 * Created on July 9, 2004,     10:28 PM
 */

/**
 * The control panel for adjusting diffuse gain. Used for internal frames.
 * 
 * @author Default
 */
public class PointLightControls extends PTMControls {
	private JLabel pointLightLabel = new JLabel("z-axis", JLabel.RIGHT);

	private JSlider pointLightSlider = new JSlider();

	private SpringLayout sl = new SpringLayout();

	private PTMWindow ptmf = null; /* callbacks */

	/**
	 * Releases all resources associated with this Object.
	 */
	public void release() {
		pointLightLabel = null;
		pointLightSlider = null;
	}

	/**
	 * Creates a new instance of GUIControls
	 * 
	 * @param ptmf
	 *            The PTMFrame parent.
	 */
	public PointLightControls(PTMWindow ptmf) {
		this.setClosable(true);
		this.setIconifiable(true);
		this.setTitle("Local light");
		this.setResizable(true);
		this.setOpaque(false);
		((JComponent) this.getContentPane()).setOpaque(false);
		this.pointLightSlider.setOpaque(false);
		pointLightSlider.setMinimum(100);
		pointLightSlider.setMaximum(100000);

		pointLightSlider.setValue(ptmf.getPTM().getZ());

		this.pointLightLabel.setFont(Defaults.CONTROL_FONT);
		this.pointLightLabel.setForeground(Defaults.FONT_COLOR);
		this.setBorder(Defaults.CONTROL_BORDER);
		this.getContentPane().setLayout(sl);
		this.getContentPane().add(pointLightLabel);
		this.getContentPane().add(pointLightSlider);

		SpringUtilities.makeCompactGrid(this.getContentPane(), 1, 2, // rows,
																		// cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad

		final PTMWindow pf = ptmf;

		pointLightSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.getPTM().setZ(val);
					pf.fireTransform();
				}
			}
		});
	}
}
