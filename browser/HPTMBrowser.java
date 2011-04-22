package browser;

// IMPORTs
import browser.Navigator;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import javax.swing.JApplet;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam;
import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;

import jpview.graphics.EnvironmentMap;
import jpview.gui.DefaultFrameElements;
import jpview.gui.DiffuseGainFrameElements;
import jpview.gui.FrameElements;
import jpview.gui.LuminanceFrameElements;
import jpview.gui.PTMCanvas;
import jpview.gui.PTMControls;
import jpview.gui.PTMRightMouseMenu;
import jpview.gui.PTMWindow;
import jpview.gui.PointLightFrameElements;
import jpview.gui.SpecularFrameElements;
import jpview.ptms.LRGBPTM;
import jpview.ptms.PTM;
import jpview.transforms.DirectionalLightOp;
import jpview.transforms.LocalLightOp;
import jpview.transforms.NormalMapOp;
import jpview.transforms.PixelTransformOp;
import jpview.transforms.ReflectionMapOp;

/**
 * HPTM Browser
 * 
 * High-resolution PTM image browser.
 * 
 * @author Massimiliano Corsini
 *         Visual Computing Laboratory
 *         ISTI - Italian National Research Council
 */

public class HPTMBrowser extends JApplet implements ActionListener,
			MouseListener, MouseMotionListener, Runnable, PTMWindow 
{
	// Full-resolution ptm (NOTE: For now the code works only for PTM of LRGB type)
	private LRGBPTM ptm = null;
	
	// To improve performance the ptm to draw will be 
	private LRGBPTM ptm_to_draw = null;

	// PTM viewer
	private PTMCanvas ptmCanvas = null;
	
	// Navigator
	private Navigator navigator = null;
	
	// This thread loads the sub-ptms during the interaction
	private Thread updateThread = null;
	
	// Internal counter
	private int internalCounter = 0;
	
	// Resolution table (contains information about resolution levels to load)
	private int[][] resolutionTable = null;
	
	private int row1 = 0, col1 = 0;
	private int row2 = 0, col2 = 0;
	private int currentRow = 0, currentCol = 0;
	private int currentLeft = 0, currentTop = 0;
	private int currentRight = 0, currentBottom = 0;

	private int mouseX = 0;
	private int mouseY = 0;
	
	boolean onlyHorizontal = false;

	private PixelTransformOp pixelTransformOp = null;
	
	private String httpbase;

	// Popup menu
	private PTMRightMouseMenu popup = null;

	private FrameElements frameElements = null;

	// DOM document containing settings.
	private Document dom;
	
	// SETTINGS
	private String ptmPath = "";
	private String ptmName = "";
	private int ptmWidth = 0;
	private int ptmHeight = 0;
	private int ptmType = PTM.LRGB;
	private int levels = 0;
	private int viewWidth = 0;
	private int viewHeight = 0;
	private int lightRangeLeft = 0;     // do not used for now
	private int lightRangeRight = 0;    // do not used for now
	private int lightRangeTop = 0;      // do not used for now
	private int lightRangeBottom = 0;   // do not used for now
	private int navWidth = 0;
	private int navHeight = 0;
	private float diffuseK = 1.0f;
	private float specularKd = 1.0f;
	private float specularKs = 1.0f;
	private float specularExp = 1.0f;
	private boolean flagDiffuseGain = true;
	private boolean flagSpecular = true;
	private boolean flagNormalMap = false;
	private boolean flagReflectionMap = false;
	private boolean flagEnvMap = false;
	private boolean flagAnimation = false;
	
	/**
	 * Load Browser Configuration.
	 *
	 * The configuration is stored in XML format. The file is indicated 
	 * by the <em>config</em> parameter of the Applet.
	 */
	private void loadConfiguration()
	{
		// Get Configuration filename	
		String config_filename = this.getParameter("config");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		URL url = null;
		try
		{
			url = new URL(httpbase + config_filename);
		}
		catch (MalformedURLException mue)
		{
			mue.printStackTrace();
		}
		
		URLConnection urlConn = null; 
		try
		{
			urlConn = url.openConnection();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		urlConn.setDoInput(true);
		urlConn.setUseCaches(false);
		
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			dom = builder.parse(urlConn.getInputStream());		
		}
		catch (ParserConfigurationException pe)
		{
			pe.printStackTrace();
		}
		catch (SAXException saxe)
		{
			saxe.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		// Retrieve "PTM" settings
		
		ptmPath = retrieveAttributeValue("PTM", "path");
		ptmName = retrieveAttributeValue("PTM", "name");
		ptmWidth = retrieveAttributeValueInt("PTM", "width");
		ptmHeight = retrieveAttributeValueInt("PTM", "height");
		ptmType = PTM.LRGB; // Only LRGB PTM are supported (!)
		levels = retrieveAttributeValueInt("PTM", "levels");
		
		// Retrieve "View Panel" settings
		
		viewWidth = retrieveAttributeValueInt("ViewPanel", "width");
		viewHeight = retrieveAttributeValueInt("ViewPanel", "height");
		
		lightRangeLeft = retrieveAttributeValueInt("LightDirectionRange", "left");
		lightRangeRight = retrieveAttributeValueInt("LightDirectionRange", "right");
		lightRangeTop = retrieveAttributeValueInt("LightDirectionRange", "top");
		lightRangeBottom = retrieveAttributeValueInt("LightDirectionRange", "bottom");
		
		// Retrieve "Navigation Panel" settings
		
		navWidth = retrieveAttributeValueInt("NavigationPanel", "width");
		navHeight = retrieveAttributeValueInt("NavigationPanel", "height");
		
		// Retrieve "Effects" settings
		
		flagDiffuseGain = retrieveAttributeValueBoolean("DiffuseGain", "enable");
		diffuseK = retrieveAttributeValueFloat("DiffuseGain", "kd");
		
		flagSpecular = retrieveAttributeValueBoolean("SpecularEnhancement", "enable");
		specularKd = retrieveAttributeValueFloat("SpecularEnhancement", "kd");
		specularKs = retrieveAttributeValueFloat("SpecularEnhancement", "ks");
		specularExp = retrieveAttributeValueFloat("SpecularEnhancement", "exp");
		
		flagNormalMap = retrieveAttributeValueBoolean("NormalsVisualization", "enable");
		
		flagReflectionMap = retrieveAttributeValueBoolean("ReflectionMapVisualization", "enable");
		
		flagEnvMap = retrieveAttributeValueBoolean("EnvMap", "enable");
		
		flagAnimation = retrieveAttributeValueBoolean("Animation", "enable");
	}
	
	private void createPopupMenu()
	{
		popup = new PTMRightMouseMenu(this);
		
		// This option is always disable for the HPTM Browser
		popup.disableReflectionVisualizationOption();
		
		// Active/disactive Environment Map (not implemented)
		if (flagEnvMap)
			popup.addEnvironmentMapOption();
		
		// Active/Disactive Animation
		if (flagAnimation)
			popup.addAnimationOption();
		
		JMenuItem lum = new JMenuItem("Luminance");
		popup.addSeparator();
		popup.add(lum);
		lum.addActionListener(this);
	}

	public void init() 
	{	
		// get horizontal parameter
		String flagHorz = this.getParameter("onlyhorizontal");
		if ((flagHorz.compareToIgnoreCase("yes") == 0) || (flagHorz.compareTo("1") == 0))
			onlyHorizontal = true;
		else
			onlyHorizontal = false;
		
		// Setup base http address
		httpbase = getCodeBase().toString();
		httpbase = "http://vcg.isti.cnr.it/~corsini/";
		
		// Assign background color
		getContentPane().setBackground(new Color(0, 0, 255));
		
		// Load Configuration (XML)
		///////////////////////////////////////////////////
		
		showStatus("Load configuration...");
	
		loadConfiguration();	
		
		// Setup interface
		///////////////////////////////////////////////////

		showStatus("Applet initialization...");
		
		// Initialize PTM 
		
		ptm = new LRGBPTM();
		ptm.setWidth(ptmWidth);
		ptm.setHeight(ptmHeight);
		ptm.setCoefficients(null);
		
		// Obtain a reduced version of the original PTM
		showStatus("Loading... (please, wait)");
		LRGBPTM ptm_thumb = loadCustomizedLRGBPTM(ptmPath, "thumbnail");
		
		showStatus("Setup the interfaces...");
		
		// Set layout
		getContentPane().setLayout(new BorderLayout());
		
		// Add title
		JPanel upperPanel = new JPanel();
		upperPanel.setBorder(BorderFactory.createEtchedBorder());
		JLabel lblTitle = new JLabel("HPTM Browser - Visual Computing Lab, ISTI-CNR.");
		upperPanel.add(lblTitle);
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		
		// Add PTM CANVAS component
		ptmCanvas = PTMCanvas.createPTMCanvas(viewWidth, viewHeight,
				PTMCanvas.BUFFERED_IMAGE);
		ptmCanvas.setDisplayWidth(viewWidth);
		ptmCanvas.setDisplayHeight(viewHeight);
		ptmCanvas.fixedSize(true);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setBorder(BorderFactory.createEtchedBorder());
		mainPanel.add(ptmCanvas);
		
		getContentPane().add(mainPanel, BorderLayout.LINE_START);
		
		pixelTransformOp = new DirectionalLightOp();
		
		getContentPane().add(new JPanel(), BorderLayout.CENTER);
		
		// Add NAVIGATOR component	
		BufferedImage bufferedImageRGB = new BufferedImage(ptm_thumb.getWidth(), 
				ptm_thumb.getHeight(), BufferedImage.TYPE_INT_RGB);
		int[] data = new int[ptm_thumb.getWidth() * ptm_thumb.getHeight()];	
		pixelTransformOp.transformPixels(data, ptm_thumb, ptm_thumb.getWidth()/2, ptm_thumb.getHeight()/2);
		bufferedImageRGB.setRGB(0, 0, ptm_thumb.getWidth(), ptm_thumb.getHeight(),
				data, 0, ptm_thumb.getWidth());
		
		navigator = new Navigator(navWidth, navHeight, bufferedImageRGB);
		
		ptm_thumb = null;
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(BorderFactory.createEtchedBorder());
		leftPanel.add(navigator);
		
		JLabel lblDesc1 = new JLabel("Select the particular of the PTM that", JLabel.LEFT);
		lblDesc1.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel lblDesc2 = new JLabel("you want to see and wait some seconds for the loading.", JLabel.LEFT);
		lblDesc2.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel lblDesc3 = new JLabel("Click left button and drag to select.", JLabel.LEFT);
		lblDesc3.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		leftPanel.add(lblDesc1);
		leftPanel.add(lblDesc2);
		leftPanel.add(lblDesc3);
		
		JLabel lblDesc4 = new JLabel("   ", JLabel.LEFT);
		lblDesc4.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel lblDesc5 = new JLabel("   ", JLabel.LEFT);
		lblDesc5.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel lblDesc6 = new JLabel("This browser is based on the Java PTM Library by Clifford Lyon.", JLabel.LEFT);
		lblDesc6.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel lblDesc7 = new JLabel("   ", JLabel.LEFT);
		lblDesc7.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel lblDesc8 = new JLabel("PTM technology has been developed by Tom Malzbender,", JLabel.LEFT);
		lblDesc8.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel lblDesc9 = new JLabel("Hans Wolters and Dan Gelb (HP-Labs).", JLabel.LEFT);
		lblDesc9.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		leftPanel.add(lblDesc4);
		leftPanel.add(lblDesc5);
		leftPanel.add(lblDesc6);
		leftPanel.add(lblDesc7);
		leftPanel.add(lblDesc8);
		leftPanel.add(lblDesc9);
		
		getContentPane().add(leftPanel, BorderLayout.LINE_END);
			
		// Setup popup menu
		////////////////////////////////////////////////////////
		
		//createPopupMenu();
		
		getContentPane().addMouseMotionListener(this);
		getContentPane().addMouseListener(this);
		
		// Further initialization
		///////////////////////////////////////////////////
		
		// invoke garbage collector to clean ptm_thumb
		System.gc();
		
		// Resolution Table
		int size = (int)Math.pow(2.0, levels);
		resolutionTable = new int[size][size];
		
		// PTM to draw
		ptm_to_draw = new LRGBPTM();
		ptm_to_draw.setWidth(viewWidth);
		ptm_to_draw.setHeight(viewHeight);
		ptm_to_draw.setCoefficients(null);
		
		// Initial light position
		mouseX = viewWidth / 2;
		mouseY = viewHeight / 2;
		
		showStatus("Initialization complete!");
	}

	public void start()
	{
		// Initialize update thread
		updateThread = new Thread(this);
		updateThread.setPriority(Thread.MIN_PRIORITY);
		updateThread.start();	
	}

	public void stop()
	{
		// Kill the update thread
		updateThread = null;
		
		// invoke garbage collector
		ptm = null;
		System.gc();
	}

	/**
	 * This method is called by the thread that was created in the start method.
	 * It updates the PTM continuosly by requisting sub-PTMs.
	 */
	public void run()
	{		
		while (true)
		{				
			synchronized(this)
			{
				if (internalCounter % 10 == 0)
				{
					int index = ZOrderMatrix.ZIndex(currentRow, currentCol, levels);
					
					// update patch if necessary
					if (resolutionTable[currentRow][currentCol] == 0)
					{
						showStatus("Loading tile...");
						
						updatePatch(currentRow, currentCol, index);
						resolutionTable[currentRow][currentCol] = 1;
						
						showStatus("Ready");
						
						updatePtmToDraw();
					}
					
					currentCol++;
					if (currentCol > col2)
					{
						currentCol = col1;
						currentRow++;
						if (currentRow > row2)
							currentRow = row1;
					}		
				}
				else if (internalCounter % 2 == 0)
				{
					pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm_to_draw, mouseX, mouseY);
					ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(), ptmCanvas.getHeight());
				}
				else
				{
					// update navigator (just in case)
					if (navigator.needsRepainting())
						navigator.repaint();
					
					// if there is a viewer update pending it will be processed now(!)
					if (navigator.updateRequest())
					{
						// notify that the update request is in progress(!)
						navigator.notifyRequest();
						
						// map the navigation rectangle on the full-res PTM
						Rectangle rc = new Rectangle(navigator.getNavigationRect());
						
						Point drawOffset = navigator.getDrawingOffset();
						rc.translate(-drawOffset.x, -drawOffset.y);
					
						float scalex = (float)ptmWidth / (float)navigator.getWidth();
						float scaley = (float)ptmHeight / (float)navigator.getHeight();
					
						int left = (int)(scalex * rc.getLocation().getX());
						int right = left + (int)(scalex * rc.getSize().getWidth());
						int top = (int)(scaley * rc.getLocation().getY());
						int bottom = top + (int)(scaley * rc.getSize().getHeight());
						
						// adjust the aspect ratio of the rectangle to match with the view panel
						Point center = new Point((left+right)/2, (top+bottom)/2);
						
						float ratio = (float)viewWidth / (float)viewHeight;
						int newW, newH;
						if (right-left > top-bottom)
						{
							newW = (right-left);
							newH = (int)((float)newW / ratio);
						}
						else
						{
							newH = (bottom-top);
							newW = (int)(ratio * (float)newH);
						}
				
						left = center.x - newW/2;
						right = center.x + newW/2;
						top = center.y - newH/2;
						bottom = center.y + newH/2;

						if (left < 0)
							left = 0;
						
						if (top < 0)
							top = 0;
						
						if (right > ptmWidth-1)
							right = ptmWidth-1;
						
						if (bottom > ptmHeight-1)
							bottom = ptmHeight-1;
					
						// convert rectangle to tiles' indices
						int size = (int)Math.pow(2.0, levels);
						float deltaW = (float)ptm.getWidth() / (float)size;
						float deltaH = (float)ptm.getHeight() / (float)size;
					
						int j1 = (int)((float)left / deltaW);
						int i1 = (int)((float)top / deltaH);
						int j2 = (int)((float)right / deltaW);
						int i2 = (int)((float)bottom / deltaH);
						
						// store the data
						if ((i1 != row1) || (j1 != col1) || (i2 != row2) || (j2 != col2))
						{
							row1 = i1;
							col1 = j1;
							row2 = i2;
							col2 = j2;
							
							currentCol = col1;
							currentRow = row1;
							currentLeft = left;
							currentTop = top;
							currentRight = right;
							currentBottom = bottom;
						}
						
						updatePtmToDraw();
					}
				}
			}
			
			try
			{
				updateThread.sleep(100);
			}
			catch (InterruptedException ie)
			{}
			
			internalCounter++;
			
			// every fifty seconds...
			if (internalCounter % 500 == 0)
			{
				// invoke garbage collector
				System.gc();
			}
		}
	}
	
	// WORKAROUND: due to the mechanism used by the PTMCanvas we use this PTM instead of 
	// the full-resolution one to improve performance
	private void updatePtmToDraw()
	{
		
		float sx = (float)(currentRight - currentLeft) / (float)viewWidth;
		float sy = (float)(currentBottom - currentTop) / (float)viewHeight;
		int[][] coeffSrc = ptm_to_draw.getCoefficients();
		int[][] coeffDest = ptm.getCoefficients();
		int offset,offset2;
		for (int y = 0; y < viewHeight; y++)
			for (int x = 0; x < viewWidth; x++)
			{
				offset = x + y * viewWidth;
				offset2 = currentLeft + (int)(sx * x) + 
					(currentTop + (int)(sy * y)) * ptm.getWidth();
				
				for (int ii = 0; ii < 7; ii++)
					coeffSrc[offset][ii] = coeffDest[offset2][ii];
			}
	}
	
	/**
	 * Load a patch and update the current PTM.
	 * 
	 * @param id  Patch Identification Number
	 */
	private void updatePatch(int r, int c, int id)
	{
		String tileName = ptmName + "_" + id;
		LRGBPTM ptmTile = loadCustomizedLRGBPTM(ptmPath, tileName);
		
		Point p = indicesToPosition(r, c);
		ptm.drawSubPtm(p.x, p.y, ptmTile);
	}
	
	private Point indicesToPosition(int r, int c)
	{
		float size = (float)Math.pow(2.0, levels);
		int posx = (int)(c * ((float)ptmWidth / (float)size));
		int posy = (int)(r * ((float)ptmHeight / (float)size));
		return new Point(posx, posy);
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		
		/*
		 * RE-ADD THIS CODE TO HANDLE POPUP MENU
		 * 
		// PERFORMANCE 
		if (cmd.equals("Sampling"))
		{
			ptmCanvas.speed();
			pixelTransformOp.clearCache();
		} 
		else if (cmd.equals("Detail"))
		{
			ptmCanvas.detail();
			pixelTransformOp.clearCache();
		} 
		else if (cmd.equals("Interpolated Sampling"))
		{
			ptmCanvas.speed();
			ptmCanvas.useHint(true);
			pixelTransformOp.clearCache();
		}

		// LIGHT SOURCE
		else if (cmd.equals("Directional"))
		{
			clearFrameElements();
			frameElements = new DefaultFrameElements();
			frameElements.init(this);
		} 
		else if (cmd.equals("Local"))
		{
			clearFrameElements();
			frameElements = new PointLightFrameElements();
			frameElements.init(this);
		} 
		else if (cmd.equals("Spotlight"))
		{
			clearFrameElements();
			frameElements = new PointLightFrameElements();
			frameElements.init(this);
			((LocalLightOp) pixelTransformOp).setFlashlight(true);
		} 
			
		// EFFECTS
		else if (cmd.equals("Effects Off"))
		{
			clearFrameElements();
			popup.effectsOff();
			frameElements = new DefaultFrameElements();
			frameElements.init(this);
		} 
		else if (cmd.equals("Diffuse Gain"))
		{
			clearFrameElements();
			popup.setForEffects();
			frameElements = new DiffuseGainFrameElements();
			frameElements.init(this);
		}  
		else if (cmd.equals("Specular"))
		{
			clearFrameElements();
			popup.setForEffects();
			frameElements = new SpecularFrameElements();
			frameElements.init(this);
		}
		else if (cmd.equals("Visualize Normals"))
		{
			clearFrameElements();
			pixelTransformOp = new NormalMapOp();
		} 
		else if (cmd.equals("Defaults"))
		{
			clearFrameElements();
			popup.effectsOff();
			popup.enableAll();
			ptmCanvas.detail();
			frameElements = new DefaultFrameElements();
			frameElements.init(this);
		} 
		else if (cmd.equals("Luminance"))
		{
			frameElements = new LuminanceFrameElements();
			frameElements.init(this);
		} 
		else 
		{
			System.out.println("Unknown: " + cmd);
		}
		
		// Invoke garbage collector
		System.gc();
*/
	}
	
	public void mouseDragged(MouseEvent me)
	{
		int x = Math.min(Math.max(me.getX(), 0), ptm_to_draw.getWidth());
		int y = Math.min(Math.max(me.getY(), 0), ptm_to_draw.getHeight());
		mouseX = x;
		
		if (onlyHorizontal)
			mouseY = ptm_to_draw.getHeight() / 2;
		else
			mouseY = y;
		
		pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm_to_draw, x, y);
		ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(), ptmCanvas.getHeight());
	}
	
	public void mouseMoved(MouseEvent me)
	{}

	public void mouseClicked(MouseEvent e)
	{
		handlePopup(e);
		this.repaint();
	}

	public void mouseEntered(MouseEvent e)
	{}

	public void mouseExited(MouseEvent e)
	{}

	public void mousePressed(MouseEvent e)
	{
		handlePopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		handlePopup(e);
		start();
	}

	public void handlePopup(MouseEvent e)
	{
		/* RE-ADD THIS CODE TO HANDLE POPUP MENU
		if (e.isPopupTrigger())
		{
			popup.show(e.getComponent(), e.getX(), e.getY());
			repaint();
			e.consume();
		}
		*/
	}

	private void clearFrameElements()
	{
		if (frameElements != null)
		{
			frameElements.release();
			frameElements = null;
			System.gc();
		}
	}

	public void forceUpdate()
	{
		pixelTransformOp.forceUpdate();
	}

	public void fireTransform()
	{
		pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm,
				mouseX, mouseY);
		ptmCanvas.paintImmediately(0, 0, ptmCanvas.getWidth(), ptmCanvas
				.getHeight());
	}
	
	private LRGBPTM loadCustomizedLRGBPTM(String path, String basename)
	{
		// setup image reader
		Iterator readers = ImageIO.getImageReadersByFormatName("jpeg2000");
		ImageReader reader=null;
		if (readers.hasNext())
		{
			reader = (ImageReader)readers.next();
		}
		
		// Retrieve customized PTM from the server using HTTP protocol
		///////////////////////////////////////////////////////////////////////
		
		
		// Load A0 layer
		URLConnection urlConn = establishConnection(httpbase + path + "/" + basename + "_a0.dat");
		
		try 
		{
			reader.setInput(new MemoryCacheImageInputStream(urlConn.getInputStream()));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedImage bufferedImageA0 = null;
		try
		{
			bufferedImageA0 = reader.read(0);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		// Load A1 layer
		urlConn = establishConnection(httpbase + path + "/" + basename + "_a1.dat");
	
		try 
		{
			reader.setInput(new MemoryCacheImageInputStream(urlConn.getInputStream()));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedImage bufferedImageA1 = null;
		try
		{
			bufferedImageA1 = reader.read(0);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		// Load A2 layer
		
		urlConn = establishConnection(httpbase + path + "/" + basename + "_a2.dat");
		
		try 
		{
			reader.setInput(new MemoryCacheImageInputStream(urlConn.getInputStream()));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedImage bufferedImageA2 = null;
		try
		{
			bufferedImageA2 = reader.read(0);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		// Load A3 layer
		
		urlConn = establishConnection(httpbase + path + "/" + basename + "_a3.dat");
		
		try 
		{
			reader.setInput(new MemoryCacheImageInputStream(urlConn.getInputStream()));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedImage bufferedImageA3 = null;
		try
		{
			bufferedImageA3 = reader.read(0);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		// Load A4 layer
		
		urlConn = establishConnection(httpbase + path + "/" + basename + "_a4.dat");
		
		try 
		{
			reader.setInput(new MemoryCacheImageInputStream(urlConn.getInputStream()));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedImage bufferedImageA4 = null;
		try
		{
			bufferedImageA4 = reader.read(0);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
			
		
		// Load A5 layer
		
		urlConn = establishConnection(httpbase + path + "/" + basename + "_a5.dat");
		
		try 
		{
			reader.setInput(new MemoryCacheImageInputStream(urlConn.getInputStream()));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedImage bufferedImageA5 = null;
		try
		{
			bufferedImageA5 = reader.read(0);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		// Load RGB layer
		
		urlConn = establishConnection(httpbase + path + "/" + basename + "_rgb.dat");

		try 
		{
			reader.setInput(new MemoryCacheImageInputStream(urlConn.getInputStream()));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedImage bufferedImageRGB = null;
		try
		{
			bufferedImageRGB = reader.read(0);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		// Reconstruct PTM
		int w = bufferedImageA0.getWidth();
		int h = bufferedImageA0.getHeight();
		
		LRGBPTM lrgbptm = new LRGBPTM();
		lrgbptm.setWidth(w);
		lrgbptm.setHeight(h);
		lrgbptm.setCoefficients(null);
		
		int coeff[] = new int[w * h];

		bufferedImageA0.getRaster().getPixels(0, 0, w, h, coeff);
		lrgbptm.setA0(coeff);
	
		bufferedImageA1.getRaster().getPixels(0, 0, w, h, coeff);
		lrgbptm.setA1(coeff);
		
		bufferedImageA2.getRaster().getPixels(0, 0, w, h, coeff);
		lrgbptm.setA2(coeff);
		
		bufferedImageA3.getRaster().getPixels(0, 0, w, h, coeff);
		lrgbptm.setA3(coeff);

		bufferedImageA4.getRaster().getPixels(0, 0, w, h, coeff);
		lrgbptm.setA4(coeff);

		bufferedImageA5.getRaster().getPixels(0, 0, w, h, coeff);
		lrgbptm.setA5(coeff);
		
		int[] rgbcoeff = new int[w * h * 3];
		bufferedImageRGB.getRaster().getPixels(0, 0, w, h, rgbcoeff);
		lrgbptm.setRGB(coeff);
		
		int offset, offset2;
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++)
			{
				offset = x + y * w;
				offset2 = offset * 3;
				coeff[offset] = (rgbcoeff[offset2] << 16) | 
					(rgbcoeff[offset2 + 1] << 8) | rgbcoeff[offset2 + 2];
			}
		
		lrgbptm.setRGB(coeff);
		
		return lrgbptm;
	}
	
	public void refreshMap()
	{
		ptm.getEnvironmentMap().refresh();
		ptm.recache();
	}
	
	public String retrieveAttributeValue(String tagname, String attribute)
	{
		NodeList nodes = dom.getElementsByTagName(tagname);
		assert(nodes.getLength() == 1);
		Node node = nodes.item(0);
		NamedNodeMap nodemap = node.getAttributes();
		return nodemap.getNamedItem(attribute).getNodeValue();
	}
	
	public float retrieveAttributeValueFloat(String tagname, String attribute)
	{
		NodeList nodes = dom.getElementsByTagName(tagname);
		assert(nodes.getLength() == 1);
		Node node = nodes.item(0);
		NamedNodeMap nodemap = node.getAttributes();
		String value = nodemap.getNamedItem(attribute).getNodeValue();
		return Float.parseFloat(value);
	}
	
	public int retrieveAttributeValueInt(String tagname, String attribute)
	{
		NodeList nodes = dom.getElementsByTagName(tagname);
		assert(nodes.getLength() == 1);
		Node node = nodes.item(0);
		NamedNodeMap nodemap = node.getAttributes();
		String value = nodemap.getNamedItem(attribute).getNodeValue();
		return Integer.parseInt(value);
	}
	
	public boolean retrieveAttributeValueBoolean(String tagname, String attribute)
	{
		NodeList nodes = dom.getElementsByTagName(tagname);
		assert(nodes.getLength() == 1);
		Node node = nodes.item(0);
		NamedNodeMap nodemap = node.getAttributes();
		String value = nodemap.getNamedItem(attribute).getNodeValue();
		if (Integer.parseInt(value) == 1)
			return true;
		else
			return false;
	}
	
	private URLConnection establishConnection(String httpaddr)
	{
		URL url = null;
		
		try
		{
			url = new URL(httpaddr);
		}
		catch (MalformedURLException mue) 
		{
			mue.printStackTrace();
		}
		
		URLConnection connection = null;
		try
		{
			connection = url.openConnection();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		return connection;
	}

	// ACCESSORS
	///////////////////////////////////////////////////////////////////////////
	
	public int getExp()
	{
		return ptm.getExp();
	}

	public float getKDiff()
	{
		return ptm.getKDiff();
	}

	public float getKSpec()
	{
		return ptm.getKSpec();
	}

	public float getLuminance()
	{
		return ptm.getLuminance();
	}
	
	public PTM getPTM()
	{
		return ptm;
	}
	
	public int getPTMHeight()
	{
		return ptm.getHeight();
	}

	public int getPTMWidth()
	{
		return ptm.getWidth();
	}

	public void setKSpec(float f)
	{
		ptm.setKSpec(f);
	}

	public void setKDiff(float f)
	{
		ptm.setKDiff(f);
	}

	public void setExp(int i)
	{
		ptm.setExp(i);
	}

	public void setLuminance(float f)
	{
		ptm.setLuminance(f);
	}

	public void setMapSampleSize(int i)
	{
		if (ptm.getEnvironmentMap() != null) 
		{
			ptm.getEnvironmentMap().setSampleSize(i);
		}
	}

	public void setMapBlurType(int i)
	{
		switch (i)
		{
		case EnvironmentMap.BLUR_TYPE_SIMPLE:
			ptm.getEnvironmentMap().setSimpleBlur();
			break;
		case EnvironmentMap.BLUR_TYPE_GAUSSIAN:
			ptm.getEnvironmentMap().setGaussianBlur();
			break;
		case EnvironmentMap.BLUR_TYPE_NONE:
		default:
			ptm.getEnvironmentMap().setNoBlur();
		}
	}

	public void setMapGuassianBlurSigma(float f)
	{
		ptm.getEnvironmentMap().setGaussianSigma(f);
	}

	public void setMapKernelSize(int i)
	{
		ptm.getEnvironmentMap().setBlurKernelSize(i);
	}
	
	public void setBrowser(Container c)
	{}
	
	public void setControls(PTMControls c)
	{}

	public void setEnvironmentMap(EnvironmentMap e)
	{}
	
	public void setPixelTransformOp(PixelTransformOp pto)
	{
		pixelTransformOp = pto;
	}
}
