package com.sri.gui.ext.drawing;

/**
 * This object is used to transform points.
 * 
 * Initially this objects the identity matrix representing 
 * the current coordinate system.  SO basically we
 * are transforming cooridnate systems rather than points.
 * These only apply to two dimensional transforms.
 * 
 * This is only a matrix for twodimensional cartesian coordinates.
 * For threeD matrices use Transform3D
 */
public abstract class Transform
{
	public final static int X = 0;
	public final static int Y = 1;
	public final static int Z = 2;
	
		/**
		 * Precomputed cos table.
		 */
	public final static float cos[] = new float[360];
	
		/**
		 * Precomputed sin table.
		 */
	public final static float sin[] = new float[360];
		
	static		// initialise sin and cos tables...
	{
		for (int i = 0;i < 360;i++)
		{
			sin[i] = (float)Math.sin(i * Math.PI / 180.0);
			cos[i] = (float)Math.cos(i * Math.PI / 180.0);
		}
	}
	
		/**
		 * Every transform needs a 4x4 transformation
		 * matrix.  So here it is.
		 * The Current Transformation Matrix
		 * 
		 * So basically any point that needs to be transformed
		 * will simply be post multiplied with this matrix...
		 */	
	public float ctm[][];// = new float[4][4];

	public void reset() { }
	
		/**
		 * Sets the translation of this matrix.
		 */ 
	public abstract void translate(float deltaX, float deltaY, float deltaZ);

		/**
		 * Sets the translation of this matrix.
		 */ 
	public abstract void translate(float deltaX, float deltaY);

		/**
		 * Applies a transform t to this transform.
		 * So it is :
		 *	[ c.ctm ] * [ ctm ]
		 */
	public abstract void applyFrom(Transform t);
	
		/**
		 * Applies this to a transform t and stores the 
		 * result in this.
		 * So it is :
		 *	[ ctm ] * [ c.ctm ]
		 */
	public abstract void applyTo(Transform t);
	public abstract void copyFrom(Transform tr);
	
		/**
		 * Finds the inverse of this matrix and copies
		 * it into the given matrix.
		 */
	public abstract void calcInverse(Transform t);

		/** Now a bunch of static functions...............*/
	
		/**
		 * Returns the distance between two points.
		 */
	public static int distance(int x1,int y1,int x2,int y2)
	{
		return (int)Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}
	
		/**
		 * Given a line with end points (x0,y0) and (x1,y1) returns
		 * the shortest distance between this line and a point (xp,yp);
		 */
	public static float distFromLine(int xp,int yp,int x0,int y0,int x1,int y1)
	{
		int p = x0 - x1;
		int q = y1 - y0;
		
		if (q == 0) return yp - y0;				// horizontal line...
		else if (p == 0) return xp - x0;		// vertical line...

		int p2 = p * p;
		int q2 = q * q;
		
		float xInt = (float)((q2 * x0) + (p2 * xp) + (p * q * (y0 - yp)) * 1.0 / (p2 + q2));
		float yInt = (float)((p * (xInt - xp + (q * yp)))/ q);
		return (float)Math.sqrt((xInt - xp) * (xInt - xp) + (yInt - yp) * (yInt - yp));
	}
	
	public final static int arcTanDegrees(int a,int b)
	{
		if (a == 0) return 0;
		else if (b == 0)
		{
			return (a > 0 ? 90 : 270);
		} else
		{
			return (int)(((Math.PI + Math.atan2(a,b)) * 180.0) / Math.PI);
		}
	}
	
	public final static float arcTan(int a,int b)
	{
		if (a == 0) return 0;
		else if (b == 0)
		{
			return (float)(Math.PI * (a > 0 ?  0.5 : 1.5));
		} else
		{
			return (float)(Math.PI + Math.atan2(a,b));
		}
	}
}
