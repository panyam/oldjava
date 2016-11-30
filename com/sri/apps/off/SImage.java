package com.sri.apps.off;

class SImage
{
    final static int RED = 0;
    final static int GREEN = 1;
    final static int BLUE = 2;

    boolean changed = false;
    short data[][][] = null;
    int pixData[];
    double zBuffer[][] = null;

        /**
         * Thenumber of rows in the image.
         */
    int width;

        /**
         * The number of columns in the image.
         */
    int height;

        /**
         * Creates a new image object given the number
         * of rows and columns of the image.
         *
         * One thing that we are doing here differently here is as follows:
         *
         *  In Conventional images, a pixel is called by its row and column
         *  value.  This is like referring to a pixel by its y,x
         *  coordintaes rather than the x,y coordinates.  To avoid this
         *  each image frame is created as a list of columsn rathern than a
         *  list of rows, where each column corresponds to a particular a
         *  certain row.
         *
         *  Of course when we write the image, to an output stream, we swap
         *  this.  This is done so that each time we want to write a pixel
         *  value at (x,y) we would not have to do data[height - y - 1][x]
         *  and we can simply do data[x][y].  This is important as when we
         *  are drawing lines, this amounts to a lot of subtractions.
         */
    SImage(int width, int height)
    {
        setSize(width, height);
    }

    void setSize(int width, int height)
    {
        if (width != this.width || height != this.height)
        {
            changed = true;
            pixData = null;
            data = null;
            zBuffer = null;
            System.gc();
            this.height = height;
            this.width = width;

            pixData = new int[width * height];
            data = new short[3][width][height];
            zBuffer = new double[width][height];
        }
    }

        /**
         * Initialises the z buffer to minZ.
         */
    void initZBuffer(double minZ)
    {
        for (int i = 0;i < width;i++)
        {
            for (int j = 0;j < height;j++)
            {
                zBuffer[i][j] = minZ;
                data[0][1][j] = data[1][i][j] = data[2][i][j] = 0;
            }
        }
    }

    short [][]getRedFrame()
    {
        return data[RED];
    }

    short [][]getGreenFrame()
    {
        return data[GREEN];
    }

    short [][]getBlueFrame()
    {
        return data[BLUE];
    }

        /**
         * Returns the z buffer for the image.
         */
    double [][]getZBuffer()
    {
        return zBuffer;
    }

        /**
         * Returns the number of rows in this image.
         */
    int getRows()
    {
        return height;
    }


        /**
         * Returns the number of columns in this image.
         */
    int getCols()
    {
        return width;
    }

        /**
         * Draws a line from x1,y1 to x2,y2 with the given 
         * pixel value.
         *
         * The line is drawn using the Bresenham's algorithm.
         */
    void drawLine(int x1, int y1, int x2, int y2, int r, int g,int b)
    {
            // do any necessary clipping
        //if (outcodeLineClipper(x1, y1, x2, y2, width, height)) return ;

        int dy = y2 - y1;
        int dx = x2 - x1;
        int stepx, stepy;

                // handle special cases!!!
        if (dx == 0)
        {       // vertical line..
            int sy, ey;
            if (y1 < y2) 
            {
                sy = y1;
                ey = y2;
            } else
            {
                sy = y2;
                ey = y1;
            }
            for (int i = sy;i <= ey;i++)
            {
                data[RED][x1][i] = (short)(r & 0xff);
                data[GREEN][x1][i] = (short)(g & 0xff);
                data[BLUE][x1][i] = (short)(b & 0xff);
            }
            return ;
        } else if (dy == 0)
        {           // horizontal line..
            int sx, ex;
            if (x1 < x2) 
            {
                sx = x1;
                ex = x2;
            } else
            {
                sx = x2;
                ex = x1;
            }
            for (int i = sx;i <= ex;i++)
            {
                data[RED][i][y1] = (short)(r & 0xff);
                data[GREEN][i][y1] = (short)(g & 0xff);
                data[BLUE][i][y1] = (short)(b & 0xff);
            }
            return ;
        }

        if (dy < 0) { dy = -dy;  stepy = -1; } else { stepy = 1; }
        if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }

        dy <<= 1;       // dy = dy * 2
        dx <<= 1;       // dx = dx * 2

        data[RED][x1][y1] = (short)(r & 0xff);
        data[GREEN][x1][y1] = (short)(g & 0xff);
        data[BLUE][x1][y1] = (short)(b & 0xff);


        if (dx > dy) {
            int fraction = dy - (dx >> 1);
            while (x1 != x2) {
                if (fraction >= 0) {
                    y1 += stepy;
                    fraction -= dx;
                }
                x1 += stepx;
                fraction += dy;
                data[RED][x1][y1] = (short)(r & 0xff);
                data[GREEN][x1][y1] = (short)(g & 0xff);
                data[BLUE][x1][y1] = (short)(b & 0xff);
            }
        } else {
            int fraction = dx - (dy >> 1);
            while (y1 != y2) {
                if (fraction >= 0) {
                    x1 += stepx;
                    fraction -= dy;
                }
                y1 += stepy;
                fraction += dx;
                data[RED][x1][y1] = (short)(r & 0xff);
                data[GREEN][x1][y1] = (short)(g & 0xff);
                data[BLUE][x1][y1] = (short)(b & 0xff);
            }
        }
    }


        /**
         * Draws the outline of a polygon.
         */
    void drawPolygon(int xpoints[], int ypoints[], int nPoints,
                     int r,int g,int b)
    {
        int px = xpoints[0];
        int py = ypoints[0];

        int cx = px,cy = py;

        for (int i = 1;i < nPoints;i++)
        {
            cx = xpoints[i];
            cy = ypoints[i];

            drawLine(px,py,cx,cy, r, g, b);

            px = cx;
            py = cy;
        }
        drawLine(cx,cy,xpoints[0],ypoints[0],r,g,b);
    }

    void change()
    {
        changed = true;
    }

    public int []getPixelArray()
    {
        if (changed)
        {
            int cnt = 0;
            for (int i = height - 1;i >= 0;i--)
            {
                for (int j = 0;j < width;j++)
                {
                    pixData[cnt++] = 0xff000000 |
                                    ((data[RED][j][i] & 0xff) << 16) |
                                    ((data[GREEN][j][i] & 0xff) << 8) |
                                    ((data[BLUE][j][i] & 0xff));
                }
            }
            changed = false;
        }
        return pixData;
    }
}
