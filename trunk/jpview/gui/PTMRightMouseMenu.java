/*
 * PTMRightMouseMenu.java
 *
 * Created on September 15, 2004, 10:14 PM
 */

package jpview.gui;

import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * Customizable popup menu
 * 
 * @author clyon
 * @author Massimliano Corsini
 */
public class PTMRightMouseMenu extends JPopupMenu 
{
	private ActionListener listener = null; 
	
	/**
	 * Environment Map option.
	 *
	 * Customizable option. It is not present by default.
	 */
	public void addEnvironmentMapOption()
	{
		experimentalSubMenu.add(environmentMapOn);
		environmentMapOn.setSelected(false);
		environmentMapOn.addActionListener(listener);
		add(experimentalSubMenu);
		addSeparator();
	}
	
	/**
	 * Animation option.
	 *
	 * Customizable option. It is not present by default. 
	 */
	public void addAnimationOption()
	{
		animatedCheckBox.addActionListener(listener);
		add(animatedCheckBox);
		addSeparator();
	}
	
	/**
	 * Defaults option.
	 *
	 * Customizable option. It is not present by default. 
	 */
	public void addDefaultsOption()
	{
		defaults.addActionListener(listener);
		add(defaults);
		addSeparator();
	}
	
	/** 
	 * Disable Diffuse Gain option.
	 * 
	 * Remove this voice from the Effects menu.
	 */
	public void disableDiffuseGainOption()
	{
		effectsSubMenu.remove(rbDiffuseGain);
		bgEffects.remove(rbDiffuseGain);
		rbDiffuseGain.removeActionListener(listener);
	}
	
	/**
	 * Disable Specular option.
	 * 
	 * Remove this voice from the Effects menu.
	 */
	public void disableSpecularOption()
	{
		effectsSubMenu.remove(rbShiny);
		bgEffects.remove(rbShiny);
		rbShiny.removeActionListener(listener);
	}
	
	/**
	 * Disable Normal Visualization option.
	 * 
	 * Remove this voice from the Effects menu. 
	 */
	public void disableNormalsVisualizationOption()
	{
		effectsSubMenu.remove(rbNormalMap);
		bgEffects.remove(rbNormalMap);
		rbNormalMap.removeActionListener(listener);
	}
	
	/**
	 * Disable Reflection Visualization option.
	 * 
	 * Remove this voice from the Effects menu.
	 */
	public void disableReflectionVisualizationOption()
	{
		effectsSubMenu.remove(rbReflectionMap);
		bgEffects.remove(rbReflectionMap);
		rbReflectionMap.removeActionListener(listener);
	}
	
	private JCheckBoxMenuItem animatedCheckBox = new JCheckBoxMenuItem(
			"Animate");

	// Performance sub-menu
	private JMenu perfSubMenu = new JMenu("Performance");
	
	private JRadioButtonMenuItem detail = new JRadioButtonMenuItem("Detail");

	private JRadioButtonMenuItem speed = new JRadioButtonMenuItem("Sampling");

	private JRadioButtonMenuItem speedHints = new JRadioButtonMenuItem(
			"Interpolated Sampling");

	// Light Source sub-menu
	private JMenu lightSubMenu = new JMenu("Light Source");

	private JRadioButtonMenuItem directional = new JRadioButtonMenuItem(
			"Directional");

	private JRadioButtonMenuItem local = new JRadioButtonMenuItem("Local");

	private JRadioButtonMenuItem flash = new JRadioButtonMenuItem("Spotlight");

	private JMenuItem defaults = new JMenuItem("Defaults");

	// Effects sub-menu
	private JMenu effectsSubMenu = new JMenu("Effects");
	
	private JRadioButtonMenuItem rbDiffuseGain = new JRadioButtonMenuItem(
			"Diffuse Gain");
	
	private JRadioButtonMenuItem rbShiny = new JRadioButtonMenuItem(
			"Specular");

	private JRadioButtonMenuItem rbNoEffects = new JRadioButtonMenuItem(
			"Effects Off");

	private JRadioButtonMenuItem rbNormalMap = new JRadioButtonMenuItem(
			"Visualize Normals");

	private JRadioButtonMenuItem rbReflectionMap = new JRadioButtonMenuItem(
			"Visualize Reflection");

	private ButtonGroup bgPerformance = new ButtonGroup();

	private ButtonGroup bgEffects = new ButtonGroup();

	private ButtonGroup bgLight = new ButtonGroup();
	
	// Environment mapping sub-menu (experimental -> not active)
	private JMenu experimentalSubMenu = new JMenu("Experimental");

	private JCheckBoxMenuItem environmentMapOn = new JCheckBoxMenuItem(
			"Environment Mapping");

	public void setForEffects()
	{
		directional.setSelected(true);
		local.setEnabled(false);
		flash.setEnabled(false);
	}

	public void enableAll()
	{
		local.setEnabled(true);
		flash.setEnabled(true);
	}

	public void effectsOff()
	{
		rbNoEffects.setSelected(true);
	}

	/** Creates a new instance of PTMRightMouseMenu */
	public PTMRightMouseMenu(ActionListener frame)
	{
		super();
		
		listener = frame;
		
		// SETUP BASIC OPTIONs
		
		// Light Source options - always present
		lightSubMenu.add(directional);
		lightSubMenu.add(local);
		lightSubMenu.add(flash);
		directional.setSelected(true);

		bgLight.add(directional);
		bgLight.add(local);
		bgLight.add(flash);

		directional.addActionListener(frame);
		local.addActionListener(frame);
		flash.addActionListener(frame);
		add(lightSubMenu);
		addSeparator();
		
		
		// Performance options - always present
		bgPerformance.add(detail);
		bgPerformance.add(speed);
		bgPerformance.add(speedHints);
		detail.setSelected(true);

		perfSubMenu.add(detail);
		perfSubMenu.add(speed);
		perfSubMenu.add(speedHints);

		detail.addActionListener(frame);
		speed.addActionListener(frame);
		speedHints.addActionListener(frame);
		add(perfSubMenu);
		addSeparator();
		
		// Effect options - always present
		rbShiny.addActionListener(frame);
		rbDiffuseGain.addActionListener(frame);
		rbNoEffects.addActionListener(frame);
		rbNormalMap.addActionListener(frame);
		rbReflectionMap.addActionListener(frame);

		bgEffects.add(rbShiny);
		bgEffects.add(rbDiffuseGain);
		bgEffects.add(rbNoEffects);
		bgEffects.add(rbNormalMap);
		bgEffects.add(rbReflectionMap);

		effectsSubMenu.add(rbNoEffects);
		effectsSubMenu.add(rbDiffuseGain);
		effectsSubMenu.add(rbShiny);
		effectsSubMenu.add(rbNormalMap);
		effectsSubMenu.add(rbReflectionMap);

		rbNoEffects.setSelected(true);
		
		add(effectsSubMenu);
		addSeparator();
	}

	public boolean isAnimated()
	{
		return animatedCheckBox.isSelected();
	}

}
