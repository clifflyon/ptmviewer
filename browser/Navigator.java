package browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * 
 * @author Massimiliano Corsini
 */
public class Navigator extends JComponent implements MouseListener, MouseMotionListener
{	
	// definitions
	static public final int SIZE_LIMIT = 5;
	
	// private data members
	private int width, height;
	private BufferedImage image = null;
	private Rectangle navRect = new Rectangle();
	private Point drawingOffset = new Point(-1, -1);
	private Point sPoint = new Point(-1, -1);
	private Point ePoint = new Point(-1 ,-1);
	
	// update request (if true the view driven by the navigator needs to be updated) 
	private boolean request = false;
	
	// the navigator needs to be repaint
	private boolean repainting = false;
	
	// ctor
	public Navigator(int w, int h, BufferedImage thumbnail)
	{
		width = w;
		height = h;
		image = thumbnail;
		
		setPreferredSize(new Dimension(w,h));
		setMaximumSize(new Dimension(w, h));
		setMinimumSize(new Dimension(w, h));
		
		// Add mouse listener(s)
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void paint(java.awt.Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		double sx = (double)width / (double)image.getWidth();
		double sy = (double)height / (double)image.getHeight();
		double tx, ty;
		
		if (sx < sy)
		{
			sy = sx;
			tx = 0.0;
			ty = (double)(height - image.getHeight() * sy) / 2.0;
		}
		else
		{
			sx = sy;
			tx = (double)(width - image.getWidth() * sx) / 2.0;
			ty = 0.0;
		}
		
		AffineTransform affine = AffineTransform.getTranslateInstance(tx, ty);
		affine.scale(sx, sy);

		g2d.drawImage(image, affine, this);
		
		// store drawing offset
		drawingOffset.x = (int)tx;
		drawingOffset.y = (int)ty;
		
		// draw navigation rectangle
		if (navRect.getWidth() > 2)
		{
			g2d.setColor(Color.WHITE);
			g2d.drawRect((int)navRect.getLocation().getX(), (int)navRect.getLocation().getY(),
					(int)navRect.getSize().getWidth(), (int)navRect.getSize().getHeight());
		}
		
		repainting = false;
	}
	
	public void mouseEntered(MouseEvent me)
	{
		// Nothing to do
	}
	
	public void mouseExited(MouseEvent me)
	{
		// Nothng to do
	}
	
	public void mousePressed(MouseEvent me)
	{
		if (me.getButton() == MouseEvent.BUTTON1)
		{
			me.consume();
			sPoint.x = me.getX();
			sPoint.y = me.getY();
			repainting = true;
		}
	}
	
	public void mouseReleased(MouseEvent me)
	{
		me.consume();  
		ePoint.x = me.getX();  
		ePoint.y = me.getY();
		
		Rectangle rc = new Rectangle();
		
		if ((ePoint.x < sPoint.x) & (ePoint.y < sPoint.y))
		{
			rc.setBounds(ePoint.x, ePoint.y, 
				sPoint.x - ePoint.x, sPoint.y - ePoint.y);
		}
		else if (ePoint.x < sPoint.x)
		{
			rc.setBounds(ePoint.x, sPoint.y, 
				sPoint.x - ePoint.x, ePoint.y - sPoint.y); 
		}
		else if (ePoint.y < sPoint.y)
		{
			rc.setBounds(sPoint.x, ePoint.y, 
				ePoint.x - sPoint.x, sPoint.y - ePoint.y);
		}
		else
		{
			rc.setBounds(sPoint.x, sPoint.y, 
				ePoint.x - sPoint.x, ePoint.y - sPoint.y);
		}
		
		if ((rc.getWidth() > SIZE_LIMIT) || (rc.getHeight() > SIZE_LIMIT))
		{
			navRect = rc;
			request = true;
		}
		
		repainting = true;
	}
	
	public void mouseClicked(MouseEvent me)
	{
		// check for double-click
		if (me.getClickCount() == 2)
		{
			// Nothing to do
		}
	}
	
	public void mouseDragged(MouseEvent me)
	{
		me.consume();
		
		ePoint.x = me.getX();
		ePoint.y = me.getY();
		
		Rectangle rc = new Rectangle();
		
		if ((ePoint.x < sPoint.x) & (ePoint.y < sPoint.y))
		{
			rc.setBounds(ePoint.x, ePoint.y, 
				sPoint.x - ePoint.x, sPoint.y - ePoint.y);
		}		
		else if (ePoint.x < sPoint.x)
		{
			rc.setBounds(ePoint.x, sPoint.y,
				sPoint.x - ePoint.x, ePoint.y - sPoint.y); 
		}
		else if (ePoint.y < sPoint.y)
		{
			rc.setBounds(sPoint.x, ePoint.y, 
				ePoint.x - sPoint.x, sPoint.y - ePoint.y);
		}
		else
		{
			rc.setBounds(sPoint.x, sPoint.y, 
				ePoint.x - sPoint.x, ePoint.y - sPoint.y);
		}
		
		if ((rc.getWidth() > SIZE_LIMIT) || (rc.getHeight() > SIZE_LIMIT))
			navRect = rc;
		
		repainting = true;
	}
	
	public void mouseMoved(MouseEvent me)
	{
		// Nothing to do
	}
	
	public boolean updateRequest()
	{
		return request;
	}
	
	public void notifyRequest()
	{
		request = false;
	}
	
	public boolean needsRepainting()
	{
		return repainting;
	}

	// ACCESSORS
	///////////////////////////////////////////////////////////////////////////
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}

	public Rectangle getNavigationRect()
	{
		return navRect;
	}
	
	public Point getDrawingOffset()
	{
		return drawingOffset;
	}
}

