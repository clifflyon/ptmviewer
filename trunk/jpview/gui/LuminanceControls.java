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
public class LuminanceControls extends PTMControls {
	private JLabel luminanceLabel = new JLabel("luminance", JLabel.RIGHT);

	private JSlider luminanceSlider = new JSlider();

	private SpringLayout sl = new SpringLayout();

	private PTMWindow ptmf = null; /* callbacks */

	/**
	 * Releases all resources associated with this Object.
	 */
	public void release() {
		luminanceLabel = null;
		luminanceSlider = null;
	}

	/**
	 * Creates a new instance of GUIControls
	 * 
	 * @param ptmf
	 *            The PTMFrame parent.
	 */
	public LuminanceControls(PTMWindow ptmf) {
		this.setClosable(true);
		this.setIconifiable(true);
		this.setTitle("Luminance");
		this.setResizable(true);
		this.setOpaque(false);
		((JComponent) this.getContentPane()).setOpaque(false);
		this.luminanceSlider.setOpaque(false);

		luminanceSlider.setValue(Math.round(ptmf.getLuminance()
				* (luminanceSlider.getMaximum() / 2)));

		this.luminanceLabel.setFont(Defaults.CONTROL_FONT);
		this.luminanceLabel.setForeground(Defaults.FONT_COLOR);
		this.setBorder(Defaults.CONTROL_BORDER);
		this.getContentPane().setLayout(sl);
		this.getContentPane().add(luminanceLabel);
		this.getContentPane().add(luminanceSlider);

		SpringUtilities.makeCompactGrid(this.getContentPane(), 1, 2, // rows,
																		// cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad

		final PTMWindow pf = ptmf;

		luminanceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setLuminance(((float) val)
							/ (luminanceSlider.getMaximum() / 2));
					pf.fireTransform();
				}
			}
		});
	}
}
