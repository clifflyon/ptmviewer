package browser;

// Java AWT
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;

import jpview.io.PTMIO;
import jpview.ptms.LRGBPTM;
import jpview.ptms.PTM;
import jpview.ptms.RGBPTM;

/**
 * HPTM Builder
 * 
 * High-resolution PTM image decomposer for quadtree spatial indexing.
 * 
 * @author Massimiliano Corsini
 *         Visual Computing Laboratory
 *         ISTI - Italian National Research Council
 */
public class HPTMBuilder extends JPanel implements ActionListener
{
	PTM ptm = null;
	
	JFileChooser fc;
	JList files;
	JButton btnClear;
	
	JButton btnGo;
	JTextArea logArea;
	
	// ctor
	public HPTMBuilder()
	{
		super(new BorderLayout());
			
		// File Selection Area
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.setDragEnabled(true);
		fc.setControlButtonsAreShown(false);
		fc.setPreferredSize(new Dimension(400,300));
		
		// Set the launch directory as the current directory
	    try
	    {
	    	//File f = new File(new File(".").getCanonicalPath());
	    	File f = new File("C:/temp/examples");
	    	fc.setCurrentDirectory(f);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
		
		// List of files to be process...
		files = new JList(new DefaultListModel());
		files.setVisible(true);
		JScrollPane scroller = new JScrollPane(files);
		scroller.setPreferredSize(new Dimension(500,300));
		files.setVisibleRowCount(16);
		files.setDragEnabled(true);
		files.setTransferHandler(new ListTransferHandler());
				
		btnClear = new JButton("Clear All");
		btnClear.addActionListener(this);
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JLabel lbl = new JLabel("Selected files");
		listPanel.add(lbl);
		listPanel.add(scroller);
		listPanel.add(btnClear);

		JPanel upperPanel = new JPanel();
		BoxLayout upperLayout = new BoxLayout(upperPanel, BoxLayout.X_AXIS);
		upperPanel.setLayout(upperLayout);
		upperPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		upperPanel.add(fc);
		upperPanel.add(listPanel);
		
		// Processing Area
		JPanel bottomPanel = new JPanel();
		BoxLayout bottomLayout = new BoxLayout(bottomPanel, BoxLayout.X_AXIS);
		bottomPanel.setLayout(bottomLayout);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		logArea = new JTextArea(50, 50);
		logArea.setEditable(false);
		JScrollPane scrollingText = new JScrollPane(logArea);
		scrollingText.setPreferredSize(new Dimension(480, 240));
		
		btnGo = new JButton("Process");
		btnGo.addActionListener(this);
		bottomPanel.add(btnGo);
		bottomPanel.add(scrollingText);
		
		add(upperPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == btnClear)
		{
			// Clear all selected files
			DefaultListModel listModel = (DefaultListModel)files.getModel();
			listModel.clear();
			
			message("Selected files list has been empty!");
		}
		else if (e.getSource() == btnGo)
		{
			// Begin to process selected files
			
			message("Start to process selected files...");
			
			DefaultListModel listModel = (DefaultListModel)files.getModel();
			
			for (int i = 0; i < listModel.size(); i++)
			{
				String filename = (String)listModel.elementAt(i);
				decomposePTM(filename, 3);
			}
			
			message("Process done.");
		}
	}
	
	/**
	 * Decompose the given PTM.
	 */
	public void decomposePTM(String pathname, int nlevels)
	{
		// Step 1: Load the whole PTM.
		/////////////////////////////////////////
		
		try
		{
			ptm = PTMIO.getPTMParser(new FileInputStream(pathname)).readPTM();
			
			// Further file information
			File f = new File(pathname);
			String filename = f.getName();
			
			Object[] args1 = {filename};
			String txt = MessageFormat.format(
					"PTM \"{0}\" has been succesfully loaded.", args1);
			message(txt);
		
			Object[] args2 = {
					new Integer(ptm.getWidth()), 
					new Integer(ptm.getHeight())};
			String txt2 = MessageFormat.format(
					"PTM info: pixels {0,number,integer} x {1,number,integer} pixels.", 
					args2);
			message(txt2);
			
			newline();
			message(new String("Starting decomposition..."));
			newline();
	
			// Step 2: Decompose it and save each patch.
			///////////////////////////////////////////////////
		
			// Filename without extension
			int index = filename.lastIndexOf("."); 
			String imgname = filename.substring(0, index);
			
			// Directory name
			String dirname = f.getParent();
			dirname += File.separator;
			dirname += imgname;
			
			// If the directory does not exists we create it(!)
			File ptmdir = new File(dirname);
			
			if (ptmdir.exists())
			{
				// Clear all the content of the directory
				String[] cmds = new String[6];
				                         
				cmds[0] = "cmd.exe";
				cmds[1] = "/C";
				cmds[2] = "rd";
				cmds[3] = "/q";
				cmds[4] = "/s"; 
				cmds[5] = dirname;
					
				Process proc = Runtime.getRuntime().exec(cmds);
	
				try
				{
					proc.waitFor();
				}
				catch(InterruptedException e)
				{
					System.out.print(e.getMessage());
				}
			}
			
			ptmdir.mkdir();
			
			// Create and save all patchs
			int[][] z = ZOrderMatrix.createZMatrix(nlevels);
			int size = (int)Math.pow(2.0, nlevels);
			
			int x1,y1,x2,y2;
			float deltaW = (float)ptm.getWidth() / (float)size;
			float deltaH = (float)ptm.getHeight() / (float)size;
			
			for (int i = 0; i <  size; i++)
				for (int j = 0; j < size; j++)
				{	
					String patchname = imgname;
					patchname += "_";
					patchname += String.valueOf(z[i][j]);
					
					x1 = (int)(deltaW * j);
					y1 = (int)(deltaH * i);
					x2 = (int)(deltaW * (j+1))-1;
					y2 = (int)(deltaH * (i+1))-1;
					
					if (ptm.getType() == PTM.LRGB)
						saveCustomizedLRGBPTM((LRGBPTM)ptm, x1, y1, x2, y2, dirname, patchname);
					else if (ptm.getType() == PTM.RGB)
						saveCustomizedRGBPTM((RGBPTM)ptm, x1, y1, x2, y2, dirname, patchname);
					
					Object[] args3 = {
							new Integer(i), 
							new Integer(j)};
					String txt3 = MessageFormat.format(
							"Tile ( {0,number,integer} , {1,number,integer} ) created.", 
							args3);
					message(txt3);
				}
			
			// Create and save PTM of reduced size
			
			ptm.resize(ptm.getWidth() / 8, ptm.getHeight() / 8);
			if (ptm.getType() == PTM.LRGB)
				saveCustomizedLRGBPTM((LRGBPTM)ptm, 0, 0, ptm.getWidth()-1, ptm.getHeight()-1, 
						dirname, "thumbnail");
			else if (ptm.getType() == PTM.RGB)
				saveCustomizedRGBPTM((RGBPTM)ptm, 0, 0, ptm.getWidth()-1, ptm.getHeight()-1, 
						dirname, "thumbnail");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-2);
		}
	}
	
	/**
	 * Save a PTM of LRGB type in a custom format.
	 * 
	 * The LRGB PTM is stored as a sequence of 7 gray-levels images
	 * representing the coefficients of the polynomial of the single  
	 * RGB channel. Such images are compressed using the JPEG2000 format.
	 * @throws IOException 
	 */
	private void saveCustomizedLRGBPTM(LRGBPTM ptm, int left, int top, int right, int bottom, 
			String dir, String basename) throws IOException
	{
		assert(left >= 0);
		assert(top >= 0);
		assert(right < ptm.getWidth());
		assert(bottom < ptm.getHeight());
		assert(left < right);
		assert(top < bottom);
		
		// Create image output stream
		String patchname = basename + "_a0.dat";
		File fpatch = new File(dir, patchname);
		fpatch.createNewFile();
		FileOutputStream fout = new FileOutputStream(fpatch);
		ImageOutputStream ios = ImageIO.createImageOutputStream(fout);
		Iterator writers = ImageIO.getImageWritersByFormatName("jpeg2000");
		ImageWriter writer=null;
		if (writers.hasNext())
		{
			writer = (ImageWriter)writers.next();
		}
		writer.setOutput(ios);
		
		// Prepare data to save as image	
		SampleModel sampleModel = new ComponentSampleModel(
				DataBuffer.TYPE_INT, (right-left+1), (bottom-top+1), 
				1, (right-left+1), new int[] {0});

		ColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_GRAY),
				null, false, false,	Transparency.OPAQUE, DataBuffer.TYPE_INT);
		
		// Set encoding parameters (=> losseless compression)
		J2KImageWriteParam encodeParam = (J2KImageWriteParam)writer.getDefaultWriteParam();
		encodeParam.setFilter(J2KImageWriteParam.FILTER_53);
		
		int datasize = (right-left+1)*(bottom-top+1);

		// Save A0 layer
		DataBufferInt buffA0 = new DataBufferInt(
				ptm.getA0(left, top, right, bottom),
				datasize);
		
		BufferedImage bufferedImageA0 = new BufferedImage(colorModel, 
				Raster.createWritableRaster(sampleModel, buffA0, new Point(0,0)),
				false, null);
		
		writer.write(null, new IIOImage(bufferedImageA0, null, null), encodeParam);
		ios.flush();
		ios.close();
		
		// Save A1 layer	
		DataBufferInt buffA1 = new DataBufferInt(
			ptm.getA1(left, top, right, bottom), 
			datasize);
		
		BufferedImage bufferedImageA1 = new BufferedImage(colorModel, 
				Raster.createWritableRaster(sampleModel, buffA1, new Point(0,0)),
				false, null);
		
		patchname = basename + "_a1.dat";
		fpatch = new File(dir, patchname);
		fpatch.createNewFile();
		fout = new FileOutputStream(fpatch);
		ios = ImageIO.createImageOutputStream(fout);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(bufferedImageA1, null, null), encodeParam);
		ios.flush();
		ios.close();
		
		// Save A2 layer
		DataBufferInt buffA2 = new DataBufferInt(
				ptm.getA2(left, top, right, bottom), 
				datasize);
			
		BufferedImage bufferedImageA2 = new BufferedImage(colorModel, 
				Raster.createWritableRaster(sampleModel, buffA2, new Point(0,0)),
				false, null);
		
		patchname = basename + "_a2.dat";
		fpatch = new File(dir, patchname);
		fpatch.createNewFile();
		fout = new FileOutputStream(fpatch);
		ios = ImageIO.createImageOutputStream(fout);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(bufferedImageA2, null, null), encodeParam);
		ios.flush();
		ios.close();
		
		// Save A3 layer
		DataBufferInt buffA3 = new DataBufferInt(
				ptm.getA3(left, top, right, bottom), 
				datasize);
			
		BufferedImage bufferedImageA3 = new BufferedImage(colorModel, 
				Raster.createWritableRaster(sampleModel, buffA3, new Point(0,0)),
				false, null);
			
		patchname = basename + "_a3.dat";
		fpatch = new File(dir, patchname);
		fpatch.createNewFile();
		fout = new FileOutputStream(fpatch);
		ios = ImageIO.createImageOutputStream(fout);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(bufferedImageA3, null, null), encodeParam);
		ios.flush();
		ios.close();
		
		// Save A4 layer 
		DataBufferInt buffA4 = new DataBufferInt(
				ptm.getA4(left, top, right, bottom), 
				datasize);
				
		BufferedImage bufferedImageA4 = new BufferedImage(colorModel, 
				Raster.createWritableRaster(sampleModel, buffA4, new Point(0,0)),
				false, null);
				
		patchname = basename + "_a4.dat";
		fpatch = new File(dir, patchname);
		fpatch.createNewFile();
		fout = new FileOutputStream(fpatch);
		ios = ImageIO.createImageOutputStream(fout);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(bufferedImageA4, null, null), encodeParam);
		ios.flush();
		ios.close();
			
		// Save A5 layer
		DataBufferInt buffA5 = new DataBufferInt(
				ptm.getA5(left, top, right, bottom), 
				datasize);
					
		BufferedImage bufferedImageA5 = new BufferedImage(colorModel, 
				Raster.createWritableRaster(sampleModel, buffA5, new Point(0,0)),
				false, null);
					
		patchname = basename + "_a5.dat";
		fpatch = new File(dir, patchname);
		fpatch.createNewFile();
		fout = new FileOutputStream(fpatch);
		ios = ImageIO.createImageOutputStream(fout);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(bufferedImageA5, null, null), encodeParam);
		ios.flush();
		ios.close();

		// Save RGB layer
		BufferedImage bufferedImageRGB = new BufferedImage((right-left+1), (bottom-top+1), 
				BufferedImage.TYPE_INT_RGB);
		bufferedImageRGB.setRGB(0, 0, right-left+1, bottom-top+1,
				ptm.getRGB(left,top,right,bottom), 0, right-left+1);
		
		patchname = basename + "_rgb.dat";
		fpatch = new File(dir, patchname);
		fpatch.createNewFile();
		fout = new FileOutputStream(fpatch);
		ios = ImageIO.createImageOutputStream(fout);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(bufferedImageRGB, null, null), encodeParam);
		ios.flush();
		ios.close();
		
		// clean up
		writer.dispose();
	}
	
	/**
	 * Save a PTM of RGB type in a custom format.
	 * 
	 * The RGB PTM is stored as a sequence of 18 gray-levels images
	 * representing the coefficients of each color channel.
	 * Such images are compressed using the JPEg2000 format. 
	 */
	private void saveCustomizedRGBPTM(RGBPTM ptm, int left, int top, int right, int bottom,
			String dir, String basename)
	{
		assert(left >= 0);
		assert(top >= 0);
		assert(right < ptm.getWidth());
		assert(bottom < ptm.getHeight());
		assert(left < right);
		assert(top < bottom);
		
		// TODO
	}
	
	/**
	 * Write the given message to the log area.
	 */
	public void message(String text)
	{
        logArea.append(text + "\n");

        // Make sure the new text is visible, even if there
        // was a selection in the text area.
        logArea.setCaretPosition(logArea.getDocument().getLength());
        //logArea.update(logArea.getGraphics());
	}
	
	public void newline()
	{
		message("");
	}
	
	private static void createAndShowGUI() 
	{
		JFrame frame = new JFrame("PTM Builder - Visual Computing Lab, ISTI - CNR");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		
		// build the interface
		HPTMBuilder ui = new HPTMBuilder();
		ui.setOpaque(true);
		frame.setContentPane(ui);

		// display the window
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) 
	{
		try
		{
	        // Schedule a job for the event-dispatching thread:
	        // creating and showing this application's GUI.
			SwingUtilities.invokeLater(new Runnable() 
			{
				public void run() 
				{
					createAndShowGUI();
				}
			});
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
