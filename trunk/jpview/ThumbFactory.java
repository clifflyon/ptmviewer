/*
 * ThumbFactory.java
 *
 * Created on September 12, 2004, 5:27 PM
 */

package jpview;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import jpview.gui.PTMCanvas;
import jpview.gui.PTMCanvasBufferedImage;
import jpview.io.PTMIO;
import jpview.ptms.PTM;
import jpview.transforms.DirectionalLightOp;
import jpview.transforms.PixelTransformOp;

/**
 * 
 * @author clyon
 */
public class ThumbFactory {

	/** Creates a new instance of ThumbFactory */
	public ThumbFactory() {

	}

	public static void main(String[] args) {
		try {
			PTM ptm = PTMIO
					.getPTMParser(new FileInputStream(new File(args[0])))
					.readPTM();
			// PTM ptm = new Ellipsoid(512,512);

			/** Environment map - read the whole thing manually */
			// FileInputStream in = new FileInputStream(new File(args[1]));
			// ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			// int b;
			// while ( (b=in.read()) != -1 ) {
			// bOut.write(b);
			// }
			// ByteArrayInputStream bIn = new
			// ByteArrayInputStream(bOut.toByteArray());
			// EnvironmentMap em = new EnvironmentMap(ImageIO.read(bIn));
			// ptm.setEnvironmentMap(em);

			PTMCanvas ptmCanvas = PTMCanvas.createPTMCanvas(ptm.getWidth(), ptm
					.getHeight(), PTMCanvas.BUFFERED_IMAGE);
			PixelTransformOp pixelTransformOp = new DirectionalLightOp();
			pixelTransformOp.transformPixels(ptmCanvas.getPixels(), ptm);
			BufferedImage image = ((PTMCanvasBufferedImage) ptmCanvas)
					.getImage();
			// AffineTransformOp ato = new AffineTransformOp
			// ( new AffineTransform
			// ( AffineTransform.getScaleInstance(0.25,0.25)
			// ), null
			// );
			// ato.filter(image,null);
			ImageIO.write(image, "jpeg", new File(args[1]));

			// LRGBPTM lptm = (LRGBPTM) ptm;
			// PTMArrays2 buf = new PTMArrays2();
			// buf.copy( lptm.a0, buf.a0 );
			// buf.copy( lptm.a1, buf.a1 );
			// buf.copy( lptm.a2, buf.a2 );
			// buf.copy( lptm.a3, buf.a3 );
			// buf.copy( lptm.a4, buf.a4 );
			// buf.copy( lptm.a5, buf.a5 );
			// ObjectOutputStream os = new ObjectOutputStream(new
			// FileOutputStream(new File(args[1])));
			// os.writeObject(buf);
			// os.flush();
			// os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
