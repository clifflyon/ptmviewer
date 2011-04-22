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
 * 
 * @author Default
 */
public class SpecularControls extends PTMControls {
	private JLabel kSpecLabel = new JLabel("kSpec", JLabel.RIGHT);

	private JLabel kDiffLabel = new JLabel("kDiff", JLabel.RIGHT);

	private JLabel expLabel = new JLabel("exp", JLabel.RIGHT);

	protected JSlider kSpecSlider = new JSlider();

	protected JSlider kDiffSlider = new JSlider();

	protected JSlider expSlider = new JSlider();

	// private GraphPaperLayout gpl = new GraphPaperLayout(new Dimension(3,3));
	private SpringLayout sl = new SpringLayout();

	private PTMFrame ptmf = null; /* callbacks */

	public void release() {
		kSpecLabel = null;
		kDiffLabel = null;
		expLabel = null;
		kSpecSlider = null;
		kDiffSlider = null;
		expSlider = null;
	}

	/** Creates a new instance of GUIControls */
	public SpecularControls(PTMWindow ptmf) {
		this.setClosable(true);
		this.setIconifiable(true);
		this.setTitle("Highlights");
		this.setResizable(true);
		this.setOpaque(false);
		((JComponent) this.getContentPane()).setOpaque(false);
		this.kSpecSlider.setOpaque(false);
		this.kDiffSlider.setOpaque(false);
		this.expSlider.setOpaque(false);

		kSpecSlider.setValue(Math.round(ptmf.getKSpec()
				* kSpecSlider.getMaximum()));
		kDiffSlider.setValue(Math.round(ptmf.getKDiff()
				* kDiffSlider.getMaximum()));
		expSlider.setValue(ptmf.getExp());

		this.kSpecLabel.setFont(Defaults.CONTROL_FONT);
		this.kSpecLabel.setForeground(Defaults.FONT_COLOR);
		this.kDiffLabel.setFont(Defaults.CONTROL_FONT);
		this.kDiffLabel.setForeground(Defaults.FONT_COLOR);
		this.expLabel.setFont(Defaults.CONTROL_FONT);
		this.expLabel.setForeground(Defaults.FONT_COLOR);
		this.setBorder(Defaults.CONTROL_BORDER);
		this.getContentPane().setLayout(sl);
		this.getContentPane().add(kSpecLabel);
		this.getContentPane().add(kSpecSlider);
		this.getContentPane().add(kDiffLabel);
		this.getContentPane().add(kDiffSlider);
		this.getContentPane().add(expLabel);
		this.getContentPane().add(expSlider);

		SpringUtilities.makeCompactGrid(this.getContentPane(), 3, 2, // rows,
																		// cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad

		final PTMWindow pf = ptmf;

		kSpecSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setKSpec(((float) val) / kSpecSlider.getMaximum());
					pf.fireTransform();
					// pf.repaint();
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});

		kDiffSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setKDiff(((float) val) / kDiffSlider.getMaximum());
					pf.fireTransform();
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});

		expSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setExp(val);
					pf.forceUpdate();
					pf.fireTransform();
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});
	}
}
