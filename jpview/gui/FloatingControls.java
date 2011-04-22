/*
 * FloatingControls.java
 *
 * Created on October 7, 2004, 12:56 AM
 */
package jpview.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

import jpview.Utils;
import jpview.graphics.EnvironmentMap;

/**
 * 
 * @author clyon
 */
public class FloatingControls extends JFrame {

	private GeneralPanel gp = new GeneralPanel();

	private SpecularPanel sp = new SpecularPanel();

	private DiffuseGainPanel dp = new DiffuseGainPanel();

	private EnvironmentMapPanelInner em = new EnvironmentMapPanelInner();

	private PTMWindow ptmw = null;

	public void setSpecularEnabled(boolean b) {
		sp.setEnabled(b);
	}

	public void setDiffuseGainEnabled(boolean b) {
		dp.setEnabled(b);
	}

	public void setLuminanceSliderValue(int val) {
		gp.setLuminanceSlider(val);
	}

	public void setDistanceEnabled(boolean b) {
		gp.setDistanceEnabled(b);
	}

	public void setPTMWindow(PTMWindow p) {
		ptmw = p;

		gp.setPTMWindow(p);
		gp.init();

		sp.setPTMWindow(p);
		sp.init();
		sp.setEnabled(false);

		dp.setPTMWindow(p);
		dp.init();
		dp.setEnabled(false);

		em.setPTMWindow(p);
		em.init();
		em.setFC(this);

	}

	/** Creates a new instance of FloatingControls */
	public FloatingControls() {
		this.getContentPane().setLayout(new SpringLayout());

		FloatingControls.this.getContentPane().add(gp);
		FloatingControls.this.getContentPane().add(sp);
		FloatingControls.this.getContentPane().add(dp);
		FloatingControls.this.getContentPane().add(em);
		SpringUtilities.makeCompactGrid(this.getContentPane(), 4, 1, // rows,
																		// cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad
	}

	public void release() {
		gp = null;
		sp = null;
		dp = null;
	}

	public static void main(String args[]) {
		FloatingControls ptmf = new FloatingControls();
		ptmf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ptmf.pack();
		ptmf.setLocationRelativeTo(null);
		ptmf.setVisible(true);
		// ptmf.show();
	}

}

abstract class FloatingControlPanel extends JPanel {
	protected PTMWindow ptmw;

	public void setPTMWindow(jpview.gui.PTMWindow p) {
		ptmw = p;
	}

	public abstract void init();

	public void setEnabled(boolean b) {
		Component[] comp = this.getComponents();
		for (int i = 0; i < comp.length; i++) {
			comp[i].setEnabled(b);
		}
	}

	public void release() {
		Component[] comp = this.getComponents();
		for (int i = 0; i < comp.length; i++) {
			comp[i] = null;
		}
		this.removeAll();
	}

}

class GeneralPanel extends FloatingControlPanel {

	private JSlider luminanceSlider = new JSlider();

	private JLabel label = new JLabel("light");

	private JSlider distanceSlider = new JSlider();

	private JLabel distanceLabel = new JLabel("distance");

	public void setLuminanceSlider(int val) {
		luminanceSlider.setValue(val);
		luminanceSlider.repaint();
	}

	public void setDistanceSlider(int val) {
		distanceSlider.setValue(val);
		distanceSlider.repaint();
	}

	public void setDistanceEnabled(boolean b) {
		distanceSlider.setEnabled(b);
		distanceLabel.setEnabled(b);
	}

	public GeneralPanel() {

		super();
		this.setLayout(new SpringLayout());

		distanceSlider.setMinimum(100);
		distanceSlider.setMaximum(100000);

		GeneralPanel.this.setDistanceEnabled(false);
		GeneralPanel.this.add(label);
		GeneralPanel.this.add(luminanceSlider);
		GeneralPanel.this.add(distanceLabel);
		GeneralPanel.this.add(distanceSlider);

		SpringUtilities.makeCompactGrid(this, 2, 2, // rows, cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad
		TitledBorder b = BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED), "general");
		b.setTitleJustification(TitledBorder.RIGHT);
		this.setBorder(b);
	}

	public void init() {
		luminanceSlider.setValue(Math.round(ptmw.getLuminance()
				* (luminanceSlider.getMaximum() / 4)));
		luminanceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					ptmw.setLuminance(((float) val)
							/ (luminanceSlider.getMaximum() / 4));
					ptmw.fireTransform();
				} else {
				}
				luminanceSlider.repaint();

				((PTMCanvasBufferedImage) ((PTMFrame) ptmw).ptmCanvas)
						.dumpAvgValues();
			}
		});
		distanceSlider.setValue(ptmw.getPTM().getZ());

		distanceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int val = (int) source.getValue();
					ptmw.getPTM().setZ(val);
					ptmw.fireTransform();
				} else {
				}
				distanceSlider.repaint();
			}
		});
	}
}

class SpecularPanel extends FloatingControlPanel {

	JSlider kSpecSlider = new JSlider();

	JSlider kDiffSlider = new JSlider();

	JSlider expSlider = new JSlider();

	JLabel kSpecLabel = new JLabel("kSpec");

	JLabel kDiffLabel = new JLabel("kDiff");

	JLabel expLabel = new JLabel("exp");

	public SpecularPanel() {
		super();

		this.setLayout(new SpringLayout());
		SpecularPanel.this.add(kSpecLabel);
		SpecularPanel.this.add(kSpecSlider);
		SpecularPanel.this.add(kDiffLabel);
		SpecularPanel.this.add(kDiffSlider);
		SpecularPanel.this.add(expLabel);
		SpecularPanel.this.add(expSlider);

		SpringUtilities.makeCompactGrid(this, 3, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
		TitledBorder b = BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED), "specular");
		b.setTitleJustification(TitledBorder.RIGHT);
		this.setBorder(b);

	}

	public void init() {

		final PTMWindow pf = ptmw;

		kSpecSlider.setValue(Math.round(pf.getKSpec()
				* kSpecSlider.getMaximum()));
		kDiffSlider.setValue(Math.round(pf.getKDiff()
				* kDiffSlider.getMaximum()));
		expSlider.setValue(pf.getExp());

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

class DiffuseGainPanel extends FloatingControlPanel {
	JSlider dGainSlider = new JSlider();

	JLabel label = new JLabel("dGain");

	public DiffuseGainPanel() {
		super();
		this.setLayout(new SpringLayout());
		this.add(label);
		this.add(dGainSlider);
		SpringUtilities.makeCompactGrid(this, 1, 2, // rows, cols
				6, 6, // initX, initY
				6, 4); // xPad, yPad
		TitledBorder b = BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED), "diffuse gain");
		b.setTitleJustification(TitledBorder.RIGHT);
		this.setBorder(b);

	}

	public void init() {
		final PTMWindow pf = ptmw;
		dGainSlider.setValue(Math.round(pf.getPTM().getDGain() * 8));
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

class EnvironmentMapPanelInner extends FloatingControlPanel {

	JPanel top = new JPanel();

	JPanel bottom = new JPanel();

	JSlider group = new JSlider();

	JSlider sampleSlider = new JSlider();

	JSlider kernelSlider = new JSlider();

	JSlider sigmaSlider = new JSlider();

	JLabel sampleLabel = new JLabel("sample");

	JLabel kernelLabel = new JLabel("kernel");

	JLabel sigmaLabel = new JLabel("sigma");

	String[] mapStrings = { "none", "galileo", "grace", "cafe", "stpeters",
			"uffizi", "rnl" };

	JComboBox mapList = new JComboBox(mapStrings);

	EnvironmentMapPanel emp = new EnvironmentMapPanel();

	{

		group.setMinimum(2);
		group.setMaximum(32);
		group.setValue(EnvironmentMap.DEFAULT_KERNEL_SIZE);
		sampleSlider.setMinimum(1);
		sampleSlider.setValue(2);
		// sampleSlider.setInverted(true);

		kernelSlider.setMinimum(1);
		kernelSlider.setMaximum(32);
		// kernelSlider.setInverted(true);
		kernelSlider.setValue(5);

		sigmaSlider.setMinimum(1);
		sigmaSlider.setMaximum(10);
		sigmaSlider.setMajorTickSpacing(1);
		// sigmaSlider.setInverted(true);
		sigmaSlider.setValue(1);
	};

	private FloatingControls floatingControls = null;

	public void setPTMWindow(jpview.gui.PTMWindow p) {
		ptmw = p;
		emp.setPTMWindow(ptmw);
	}

	public void setFC(FloatingControls f) {
		floatingControls = f;
	}

	public EnvironmentMapPanelInner() {
		super();
		this.top.setLayout(new SpringLayout());
		this.bottom.setLayout(new SpringLayout());

		this.top.add(new JLabel("reflectance"));
		this.top.add(group);

		// this.top.add(sampleLabel);
		// this.top.add(sampleSlider);
		//        
		// this.top.add(kernelLabel);
		// this.top.add(kernelSlider);
		//        
		// this.top.add(sigmaLabel);
		// this.top.add(sigmaSlider);

		this.bottom.add(mapList);
		this.bottom.add(emp);

		mapList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JComboBox source = (JComboBox) ae.getSource();
				String selected = (String) source.getSelectedItem();
				// System.out.println(selected);
				String name = selected + "_probe.jpg";
				String path = "jpview/imagefiles/";
				BufferedImage bi = null;
				try {
					Class me = EnvironmentMapPanel.class;
					ClassLoader cl = me.getClassLoader();
					URL url = cl.getResource(path + name);
					if (url != null)
						bi = Utils.readUnbuffered(url.openStream());
				} catch (Exception e) {
					e.printStackTrace();
				}

				synchronized (PTMFrame.mutex) {

					if (bi != null) {

						if (emp.getEnvironmentMap() == null) {
							if (ptmw != null) {
								emp.setEnvironmentMap(new EnvironmentMap(bi,
										ptmw.getPTM()));
								ptmw.getPTM().useEnv(true);
							} else {
								emp.setEnvironmentMap(new EnvironmentMap(bi,
										null));
							}
						} else {

							emp.getEnvironmentMap().setImage(bi);
							if (ptmw != null) {
								ptmw.getPTM().useEnv(true);
							}

						}
					} else {

						if (emp.getEnvironmentMap() != null)
							// emp.unsetEnvironmentMap();
							emp.getEnvironmentMap().setImage(null);
						ptmw.getPTM().useEnv(false);
						ptmw.setLuminance(1f);
						floatingControls.setLuminanceSliderValue(25);
					}
					emp.redraw();
					emp.repaint();
					emp.getCanvas().repaint();
					if (ptmw != null) {
						ptmw.refreshMap();
						ptmw.fireTransform();
					}
				}

			}
		});

		SpringUtilities.makeCompactGrid(this.top, 1, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		SpringUtilities.makeCompactGrid(this.bottom, 2, 1, // rows, cols
				6, 6, // initX, initY
				10, 10); // xPad, yPad

		TitledBorder b = BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED), "environment");
		b.setTitleJustification(TitledBorder.RIGHT);

		this.setLayout(new BorderLayout());
		this.add(top, BorderLayout.NORTH);
		this.add(bottom, BorderLayout.SOUTH);
		this.setBorder(b);
	}

	public void init() {

		final PTMWindow pf = ptmw;

		group.addChangeListener(new ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()
						&& (pf.getPTM().getEnvironmentMap() != null)) {
					int val = (int) source.getValue();
					// System.out.println("Read val: " + val);
					synchronized (PTMFrame.mutex) {
						// sigmaSlider.setValue(Math.round(((float)val)/4));
						// kernelSlider.setValue(val);
						pf.setMapKernelSize(val);
						pf.setMapGuassianBlurSigma(((float) val) / 4);
						pf.refreshMap();
						pf.fireTransform();
					}
				} else {
					// pf.getPTMCanvas().repaint();
				}
			}
		});

		// sampleSlider.setMaximum(20);
		//        
		// sampleSlider.addChangeListener( new ChangeListener() {
		// public void stateChanged(javax.swing.event.ChangeEvent e) {
		// JSlider source = (JSlider)e.getSource();
		// if (!source.getValueIsAdjusting()) {
		// int val = (int) source.getValue();
		// //System.out.println("Read val: " + val);
		// synchronized ( ((PTMFrame) pf).mutex ) {
		// pf.setMapSampleSize(val);
		// pf.refreshMap();
		// pf.fireTransform();
		// }
		// } else {
		// //pf.getPTMCanvas().repaint();
		// }
		// }
		// });
		//        
		// kernelSlider.addChangeListener( new ChangeListener() {
		// public void stateChanged(javax.swing.event.ChangeEvent e) {
		// JSlider source = (JSlider)e.getSource();
		// if (!source.getValueIsAdjusting()) {
		// int val = (int) source.getValue();
		//                    
		// pf.setMapKernelSize(val);
		// pf.refreshMap();
		// pf.fireTransform();
		// } else {
		// //pf.getPTMCanvas().repaint();
		// }
		// }
		// });
		//        
		// sigmaSlider.addChangeListener( new ChangeListener() {
		// public void stateChanged(javax.swing.event.ChangeEvent e) {
		// JSlider source = (JSlider)e.getSource();
		// if (!source.getValueIsAdjusting()) {
		// int val = (int) source.getValue();
		// pf.setMapGuassianBlurSigma(val);
		// pf.refreshMap();
		// pf.fireTransform();
		// } else {
		// //pf.getPTMCanvas().repaint();
		// }
		// }
		// });
	}
}