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
public class DiffuseGainControls extends PTMControls {
	private JLabel dGainLabel = new JLabel("dGain", JLabel.RIGHT);

	private JSlider dGainSlider = new JSlider();

	private SpringLayout sl = new SpringLayout();

	private PTMWindow ptmf = null; /* callbacks */

	/**
	 * Releases all resources associated with this Object.
	 */
	public void release() {
		dGainLabel = null;
		dGainSlider = null;
	}

	/**
	 * Creates a new instance of GUIControls
	 * 
	 * @param ptmf
	 *            The PTMFrame parent.
	 */
	public DiffuseGainControls(PTMWindow ptmf) {
		this.setClosable(true);
		this.setIconifiable(true);
		this.setTitle("Diffuse Gain");
		this.setResizable(true);
		this.setOpaque(false);
		((JComponent) this.getContentPane()).setOpaque(false);
		this.dGainSlider.setOpaque(false);

		dGainSlider.setValue(Math.round(ptmf.getPTM().getDGain() * 8));

		this.dGainLabel.setFont(Defaults.CONTROL_FONT);
		this.dGainLabel.setForeground(Defaults.FONT_COLOR);
		this.setBorder(Defaults.CONTROL_BORDER);
		this.getContentPane().setLayout(sl);
		this.getContentPane().add(dGainLabel);
		this.getContentPane().add(dGainSlider);

		SpringUtilities.makeCompactGrid(this.getContentPane(), 1, 2, // rows,
																		// cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad

		final PTMWindow pf = ptmf;

		dGainSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.getPTM().setDGain(((float) val) / 8);
					pf.fireTransform();
				}
			}
		});
	}
}
