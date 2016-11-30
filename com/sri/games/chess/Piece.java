
import java.awt.*;
import java.awt.image.*;

/**
 * A class for representing info about pieces.
 */
public class Piece
{
	protected static ColorModel dcm = new DirectColorModel(32,0xff << 16,0xff << 8, 0xff << 0);

    public final static int NO_PIECE = -1;
    public final static int PAWN = 0;
    public final static int KNIGHT = 1;
    public final static int BISHOP = 2;
    public final static int ROOK = 3;
    public final static int QUEEN = 4;
    public final static int KING = 5;

    public static Image pieceImages[][] = new Image[2][6];

        /**
         * Load the pieces from an image.
         */
    public static void loadImage(Image image)
    {
        int sx = 3, sy = 3, w = 87, h = 87;
        for (int i = 1, y = sy;i >= 0;i--)
        {
            int x = sx;
            for (int j = 0;j < 6;j++)
            {
		        int array[] = new int [1 + w * h];	// the array for the image...
		        PixelGrabber pix = new PixelGrabber(image,x,y,w,h,array,0,w);
		        try
		        {
			        pix.grabPixels();
		        } catch (InterruptedException e) {
                    e.printStackTrace();
		        }
                //pieceImages[i][j] = createImage(new MemoryImageSource(w,h,dcm,array,0,w));
                x += w + sx;
            }
            y += h + sy;
        }
    }
}

