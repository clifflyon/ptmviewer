package jpview.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/*
 * JPViewDefaults.java
 *
 * Created on August 14, 2004, 10:09 PM
 */

/**
 * This class contains default values for various look and feel settings
 * 
 * @author Default
 */
public class Defaults {
	/**
	 * The default font for the control panels
	 */
	public static final Font CONTROL_FONT = new Font("Dialog", Font.BOLD, 14);

	/**
	 * The default font color for the control panels
	 */
	public static final Color FONT_COLOR = Color.WHITE;

	/**
	 * The default border for JPanels
	 */
	public static final Border CONTROL_BORDER = new LineBorder(FONT_COLOR, 1,
			true);

	/**
	 * The default "see-through" color for internal frames
	 */
	public static final Color SEE_THROUGH_GREY = new Color(40, 40, 80, 64);

	/**
	 * The default highlight window size
	 */
	public static final Rectangle HL_WINDOW_SZ = new Rectangle(10, 10, 488, 100);

	/**
	 * The default small window size
	 */
	public static final Rectangle SM_WINDOW_SZ = new Rectangle(10, 10, 488, 50);

	/**
	 * The default thumbnail size
	 */
	public static final Dimension THUMB_SZ = new Dimension(150, 150);
}
