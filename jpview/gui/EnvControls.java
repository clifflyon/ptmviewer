package jpview.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeListener;

import jpview.graphics.EnvironmentMap;

/*
 * GUIControls.java
 *
 * Created on July 9, 2004,     10:28 PM
 */

/**
 * A collection of UI elements to control the environment mapping
 * 
 * @author Default
 */
public class EnvControls extends JFrame {
	private JLabel kSpecLabel = new JLabel("kSpec", JLabel.RIGHT);

	private JLabel kDiffLabel = new JLabel("kDiff", JLabel.RIGHT);

	private JLabel expLabel = new JLabel("exp", JLabel.RIGHT);

	private JLabel lumLabel = new JLabel("luminance", JLabel.RIGHT);

	// private JLabel sampleSizeLabel = new JLabel("Sample", JLabel.RIGHT);
	private JLabel kernelSizeLabel = new JLabel("Kernel", JLabel.RIGHT);

	private JLabel sigmaLabel = new JLabel("Sigma", JLabel.RIGHT);

	private JLabel sampleSliderLabel = new JLabel("Sample", JLabel.RIGHT);

	// private ButtonGroup blurGroup = new ButtonGroup();
	// private JRadioButton noBlur = new JRadioButton("No Blur");
	// private JRadioButton simpleBlur = new JRadioButton("Simple Blur");
	// private JRadioButton gaussianBlur = new JRadioButton("Gaussian Blur");
	//    
	// {
	// blurGroup.add(noBlur);
	// blurGroup.add(simpleBlur);
	// blurGroup.add(gaussianBlur);
	// }

	/**
	 * Slider controlling the specular highlights
	 */
	protected JSlider kSpecSlider = new JSlider();

	/**
	 * Slider controlling the diffuse lighting
	 */
	protected JSlider kDiffSlider = new JSlider();

	/**
	 * Slider controlling the specular exponent
	 */
	protected JSlider expSlider = new JSlider();

	/**
	 * The slider controlling the level of light in the scene
	 */
	protected JSlider lumSlider = new JSlider();

	/**
	 * Slider controlling the level of downsampling for the environment map
	 */
	protected JSlider sampleSlider = new JSlider();

	/**
	 * Slider controlling the kernel size for the gaussian blur
	 */
	protected JSlider kernelSlider = new JSlider();

	/**
	 * Slider controlling the sigma value for gaussian blur
	 */
	protected JSlider sigmaSlider = new JSlider();

	// protected JTextField sampleSizeBox = new JTextField(3);
	// protected JTextField kernelSizeBox = new JTextField(3);
	// protected JTextField sigmaBox = new JTextField(3);
	// {
	// sampleSizeBox.setText(EnvironmentMap.DEFAULT_DOWNSAMPLE+"");
	// kernelSizeBox.setText(EnvironmentMap.DEFAULT_KERNEL_SIZE+"");
	// sigmaBox.setText(EnvironmentMap.DEFAULT_GAUSSIAN_SIGMA+"");
	// }

	// protected JButton update = new JButton("Update");

	// private GraphPaperLayout gpl = new GraphPaperLayout(new Dimension(3,3));
	private SpringLayout sl = new SpringLayout();

	// private PTMFrame ptmf = null; /* callbacks */
	private EnvironmentMapPanel emp = null;

	/**
	 * Releases all resources associated with this object
	 */
	public void release() {
		kSpecLabel = null;
		kDiffLabel = null;
		expLabel = null;
		lumLabel = null;
		kSpecSlider = null;
		kDiffSlider = null;
		expSlider = null;
		lumSlider = null;
		// sampleSizeBox = null;
		emp = null;
	}

	/**
	 * Creates a new instance of GUIControls
	 * 
	 * @param ptmf
	 *            The PTM Frame, concrete subclass of the PTM Window
	 */
	public EnvControls(PTMWindow ptmf) {
		this.setTitle("Test");
		this.setResizable(true);

		kSpecSlider.setValue(Math.round(ptmf.getKSpec()
				* kSpecSlider.getMaximum()));
		kDiffSlider.setValue(Math.round(ptmf.getKDiff()
				* kDiffSlider.getMaximum()));
		expSlider.setValue(ptmf.getExp());
		lumSlider.setValue(Math.round(ptmf.getLuminance()
				* lumSlider.getMaximum() / 4));

		this.kSpecLabel.setFont(Defaults.CONTROL_FONT);
		this.kSpecLabel.setForeground(Defaults.FONT_COLOR);

		this.kDiffLabel.setFont(Defaults.CONTROL_FONT);
		this.kDiffLabel.setForeground(Defaults.FONT_COLOR);

		this.expLabel.setFont(Defaults.CONTROL_FONT);
		this.expLabel.setForeground(Defaults.FONT_COLOR);

		this.lumLabel.setFont(Defaults.CONTROL_FONT);
		this.lumLabel.setForeground(Defaults.FONT_COLOR);

		this.sampleSliderLabel.setFont(Defaults.CONTROL_FONT);
		this.sampleSliderLabel.setForeground(Defaults.FONT_COLOR);

		this.kernelSizeLabel.setFont(Defaults.CONTROL_FONT);
		this.kernelSizeLabel.setForeground(Defaults.FONT_COLOR);

		this.sigmaLabel.setFont(Defaults.CONTROL_FONT);
		this.sigmaLabel.setForeground(Defaults.FONT_COLOR);
		this.emp = new EnvironmentMapPanel(ptmf.getPTM().getEnvironmentMap(),
				ptmf);

		this.getContentPane().setLayout(sl);

		this.getContentPane().add(kSpecLabel);
		this.getContentPane().add(kSpecSlider);

		this.getContentPane().add(kDiffLabel);
		this.getContentPane().add(kDiffSlider);

		this.getContentPane().add(expLabel);
		this.getContentPane().add(expSlider);

		this.getContentPane().add(lumLabel);
		this.getContentPane().add(lumSlider);

		this.getContentPane().add(sampleSliderLabel);
		this.getContentPane().add(sampleSlider);

		// this.getContentPane().add(sampleSizeLabel);
		// this.getContentPane().add(sampleSizeBox);

		this.getContentPane().add(kernelSizeLabel);
		this.getContentPane().add(kernelSlider);

		this.getContentPane().add(sigmaLabel);
		this.getContentPane().add(sigmaSlider);

		// this.getContentPane().add(new JLabel());
		// this.getContentPane().add(noBlur);
		//        
		// this.getContentPane().add(new JLabel());
		// this.getContentPane().add(simpleBlur);
		//
		// this.getContentPane().add(new JLabel());
		// this.getContentPane().add(gaussianBlur);

		// this.getContentPane().add(new JLabel());
		// this.getContentPane().add(update);

		this.getContentPane().add(new JLabel());
		this.getContentPane().add(emp);

		SpringUtilities.makeCompactGrid(this.getContentPane(), 8, 2, // rows,
																		// cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad

		sampleSlider.setMinimum(1);
		sampleSlider.setMaximum(ptmf.getPTM().getEnvironmentMap()
				.getMaxDownsample());
		sampleSlider.setMajorTickSpacing(1);
		// sampleSlider.setPaintTicks(true);
		// sampleSlider.setSnapToTicks(true);
		sampleSlider.setInverted(true);
		sampleSlider.setValue(EnvironmentMap.DEFAULT_DOWNSAMPLE);

		kernelSlider.setMinimum(1);
		kernelSlider.setMaximum(32);
		kernelSlider.setMajorTickSpacing(1);
		// kernelSlider.setPaintTicks(true);
		// kernelSlider.setSnapToTicks(true);
		kernelSlider.setInverted(true);
		kernelSlider.setValue(5);

		sigmaSlider.setMinimum(1);
		sigmaSlider.setMaximum(10);
		sigmaSlider.setMajorTickSpacing(1);
		sigmaSlider.setInverted(true);
		sigmaSlider.setValue(2);

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

		lumSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setLuminance(((float) val)
							/ (lumSlider.getMaximum() / 4));
					pf.fireTransform();
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});

		sampleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setMapSampleSize(val);
					pf.refreshMap();
					pf.fireTransform();
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});

		kernelSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setMapKernelSize(val);
					pf.refreshMap();
					pf.fireTransform();
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});

		sigmaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					pf.setMapGuassianBlurSigma(val);
					pf.refreshMap();
					pf.fireTransform();
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});

		// update.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// int sample = 0, kernel = 0;
		// float sigma = 0f;
		//                
		// // try {
		// // sample = Integer.parseInt(sampleSizeBox.getText());
		// // } catch ( NumberFormatException nfe ) {
		// // sample = EnvironmentMap.DEFAULT_DOWNSAMPLE;
		// // sampleSizeBox.setText(sample + "");
		// // }
		//                
		// // try {
		// // kernel = Integer.parseInt(kernelSizeBox.getText());
		// // } catch ( NumberFormatException nfe ) {
		// // kernel = EnvironmentMap.DEFAULT_KERNEL_SIZE;
		// // kernelSizeBox.setText(kernel + "");
		// // }
		//                
		// // try {
		// // sigma = Float.parseFloat(sigmaBox.getText());
		// // } catch ( NumberFormatException nfe ) {
		// // sigma = EnvironmentMap.DEFAULT_GAUSSIAN_SIGMA;
		// // sigmaBox.setText(sigma + "");
		// // }
		//                
		// //pf.setMapSampleSize(sample);
		// //pf.setMapKernelSize(kernel);
		// pf.setMapGuassianBlurSigma(sigma);
		// pf.refreshMap();
		// pf.fireTransform();
		// }
		// });

		// noBlur.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// pf.setMapBlurType(EnvironmentMap.BLUR_TYPE_NONE);
		// pf.refreshMap();
		// pf.fireTransform();
		// }
		// });
		//
		// simpleBlur.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// pf.setMapBlurType(EnvironmentMap.BLUR_TYPE_SIMPLE);
		// pf.refreshMap();
		// pf.fireTransform();
		// }
		// });
		//        
		// gaussianBlur.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// pf.setMapBlurType(EnvironmentMap.BLUR_TYPE_GAUSSIAN);
		// pf.refreshMap();
		// pf.fireTransform();
		// }
		// });

	}
}
