package com.sri.gui.ext.drawing;

public class Transform2D extends Transform
{
	public Transform2D()
	{
		ctm = new float[3][3];
		reset();
	}
	
		/**
		 * Sets the translation of this matrix.
		 */ 
	public void translate(float deltaX, float deltaY, float deltaZ)
	{
		translate(deltaX, deltaY);
	}
	
	public void translate(float deltaX, float deltaY)
	{
		ctm[0][0] += (deltaX * ctm[2][0]);
		ctm[0][1] += (deltaX * ctm[2][1]);
		ctm[0][2] += (deltaX * ctm[2][2]);
		ctm[1][0] += (deltaY * ctm[2][0]);
		ctm[1][1] += (deltaY * ctm[2][1]);
		ctm[1][2] += (deltaY * ctm[2][2]);
	}

		/**
		 * Rotate with respect to a given point.
		 * 
		 * Basically involves translating to the origin
		 * rotating followed by translating back to original 
		 * point.
		 */
	public void rotate(float c, float s, float wrtX, float wrtY)
	{
		// the matrix to be multiplicated with is:
		// A * B * C * this
		// A = T(-wrtX, -wrtY);
		// B = R(theta)
		// C = T(wrtX, wrtY)
		// So ABC = 
		// 
		// C  -S  wrtX(1 - C) + wrtY*S
		// S  C   wrtY(1 - C) - wrtX*S
		// 0  0			1
			
		float temp[][] = 
		{
			{ ctm[0][0], ctm[0][1], ctm[0][2]}, 
			{ ctm[1][0], ctm[1][1], ctm[1][2]}, 
			{ ctm[2][0], ctm[2][1], ctm[2][2]}, 
		};
			
		float abc [][] =
		{
			{ c, -s, (wrtX * (1 - c)) + (wrtY * s) },
			{ s,  c, (wrtY * (1 - c)) - (wrtX * s) },
			{ 0, 0, 1 },
		};
		
		ctm[0][0] = (abc[0][0] * temp[0][0]) + (abc[0][1] * temp[1][0]) + (abc[0][2] * temp[2][0]);
		ctm[0][1] = (abc[0][0] * temp[0][1]) + (abc[0][1] * temp[1][1]) + (abc[0][2] * temp[2][1]);
		ctm[0][2] = (abc[0][0] * temp[0][2]) + (abc[0][1] * temp[1][2]) + (abc[0][2] * temp[2][2]);
		
		ctm[1][0] = (abc[1][0] * temp[0][0]) + (abc[1][1] * temp[1][0]) + (abc[1][2] * temp[2][0]);
		ctm[1][1] = (abc[1][0] * temp[0][1]) + (abc[1][1] * temp[1][1]) + (abc[1][2] * temp[2][1]);
		ctm[1][2] = (abc[1][0] * temp[0][2]) + (abc[1][1] * temp[1][2]) + (abc[1][2] * temp[2][2]);
		// row 3 is unchanged...
	}

		/**
		 * Rotate with respect to the origin.
		 * 
		 * Is a mult with the matrix:
		 * 
		 * c -s 0
		 * s  c 0
		 * 0  0 1
		 */
	public void rotate(float c, float s)
	{
		float temp[][] = 
		{
			{ ctm[0][0], ctm[0][1], ctm[0][2]}, 
			{ ctm[1][0], ctm[1][1], ctm[1][2]}, 
			{ ctm[2][0], ctm[2][1], ctm[2][2]}, 
		};
		ctm[0][0] = (c * temp[0][0]) - (s * temp[1][0]);
		ctm[0][1] = (c * temp[0][1]) - (s * temp[1][1]);
		ctm[0][2] = (c * temp[0][2]) - (s * temp[1][2]);
		ctm[1][0] = (s * temp[0][0]) + (c * temp[1][0]);
		ctm[1][1] = (s * temp[0][0]) + (c * temp[1][0]);
		ctm[1][2] = (s * temp[0][0]) + (c * temp[1][0]);
		
			// row 3 is unchanged...
	}
	
		/**
		 * Resets the transformation to the identity matrix
		 */
	public void reset()
	{
		ctm[0][1] = ctm[0][2] = 0;
		ctm[1][0] = ctm[1][2] = 0;
		ctm[2][0] = ctm[2][1] = 0;
		ctm[0][0] = ctm[1][1] = ctm[2][2] = 1;
	}
	
		/**
		 * Transforms a point x,y and returns the requested
		 * x or y coorindate
		 */
	public float transform(int which,float x,float y)
	{
		return (ctm[which][0] * x + ctm[which][1] * y + ctm[which][2]);
	}
	
		/**
		 * Applies scaling to this matrix.
		 * 
		 * So
		 * 
		 * | sx 0 0 |
		 * | 0 sy 0 | * thismatrix
		 * | 0 0  1 |
		 */
	public void scale(float sx, float sy)
	{
		if (sx != 0)
		{
			ctm[0][0] *= sx;
			ctm[1][0] *= sx;
			ctm[2][0] *= sx;
		}
		if (sy != 0)
		{
			ctm[0][1] *= sy;
			ctm[1][1] *= sy;
			ctm[2][1] *= sy;
		}
	}
	
		/**
		 * Copies the transformation matrix of another transform
		 * to ths one.
		 */
	public void copyFrom(Transform tr)
	{
		ctm[0][0] = tr.ctm[0][0]; ctm[0][1] = tr.ctm[0][1]; ctm[0][2] = tr.ctm[0][2]; 
		ctm[1][0] = tr.ctm[1][0]; ctm[1][1] = tr.ctm[1][1]; ctm[1][2] = tr.ctm[1][2]; 
		ctm[2][0] = tr.ctm[2][0]; ctm[2][1] = tr.ctm[2][1]; ctm[2][2] = tr.ctm[2][2]; 
	}
	
		/**
		 * Applies this to a transform t and stores the 
		 * result in this.
		 * So it is :
		 *	[ ctm ] * [ c.ctm ]
		 */
	public void applyTo(Transform t)
	{
		if (!(t instanceof Transform2D)) return ;
		float temp[][] = 
		{
			{ t.ctm[0][0], t.ctm[0][1], t.ctm[0][2]}, 
			{ t.ctm[1][0], t.ctm[1][1], t.ctm[1][2]}, 
			{ t.ctm[2][0], t.ctm[2][1], t.ctm[2][2]}, 
		};
		
		ctm[0][0] = (ctm[0][0] * temp[0][0]) + (ctm[0][1] * temp[1][0]) + (ctm[0][2] * temp[2][0]);
		ctm[0][1] = (ctm[0][0] * temp[0][1]) + (ctm[0][1] * temp[1][1]) + (ctm[0][2] * temp[2][1]);
		ctm[0][2] = (ctm[0][0] * temp[0][2]) + (ctm[0][1] * temp[1][2]) + (ctm[0][2] * temp[2][2]);
		ctm[1][0] = (ctm[1][0] * temp[0][0]) + (ctm[1][1] * temp[1][0]) + (ctm[1][2] * temp[2][0]);
		ctm[1][1] = (ctm[1][0] * temp[0][1]) + (ctm[1][1] * temp[1][1]) + (ctm[1][2] * temp[2][1]);
		ctm[1][2] = (ctm[1][0] * temp[0][2]) + (ctm[1][1] * temp[1][2]) + (ctm[1][2] * temp[2][2]);
		ctm[2][0] = (ctm[2][0] * temp[0][0]) + (ctm[2][1] * temp[1][0]) + (ctm[2][2] * temp[2][0]);
		ctm[2][1] = (ctm[2][0] * temp[0][1]) + (ctm[2][1] * temp[1][1]) + (ctm[2][2] * temp[2][1]);
		ctm[2][2] = (ctm[2][0] * temp[0][2]) + (ctm[2][1] * temp[1][2]) + (ctm[2][2] * temp[2][2]);
	}
	
		/**
		 * Applies a transform t to this transform.
		 * So it is :
		 *	[ c.ctm ] * [ ctm ]
		 */
	public void applyFrom(Transform t)
	{
		if (!(t instanceof Transform2D)) return ;
		
		float temp[][] = 
		{
			{ ctm[0][0], ctm[0][1], ctm[0][2]}, 
			{ ctm[1][0], ctm[1][1], ctm[1][2]}, 
			{ ctm[2][0], ctm[2][1], ctm[2][2]}, 
		};
		
		ctm[0][0] = (t.ctm[0][0] * temp[0][0]) + (t.ctm[0][1] * temp[1][0]) + (t.ctm[0][2] * temp[2][0]);
		ctm[0][1] = (t.ctm[0][0] * temp[0][1]) + (t.ctm[0][1] * temp[1][1]) + (t.ctm[0][2] * temp[2][1]);
		ctm[0][2] = (t.ctm[0][0] * temp[0][2]) + (t.ctm[0][1] * temp[1][2]) + (t.ctm[0][2] * temp[2][2]);
		ctm[1][0] = (t.ctm[1][0] * temp[0][0]) + (t.ctm[1][1] * temp[1][0]) + (t.ctm[1][2] * temp[2][0]);
		ctm[1][1] = (t.ctm[1][0] * temp[0][1]) + (t.ctm[1][1] * temp[1][1]) + (t.ctm[1][2] * temp[2][1]);
		ctm[1][2] = (t.ctm[1][0] * temp[0][2]) + (t.ctm[1][1] * temp[1][2]) + (t.ctm[1][2] * temp[2][2]);
		ctm[2][0] = (t.ctm[2][0] * temp[0][0]) + (t.ctm[2][1] * temp[1][0]) + (t.ctm[2][2] * temp[2][0]);
		ctm[2][1] = (t.ctm[2][0] * temp[0][1]) + (t.ctm[2][1] * temp[1][1]) + (t.ctm[2][2] * temp[2][1]);
		ctm[2][2] = (t.ctm[2][0] * temp[0][2]) + (t.ctm[2][1] * temp[1][2]) + (t.ctm[2][2] * temp[2][2]);
	}
	
		/**
		 * Get the inverse transformed value of x, y by
		 * LU decomposition
		 */
	public float[] inversePoint(double x, double y)
	{
		float num = (float)((x * ctm[1][0]) - (y * ctm[0][0]) - 
					 (ctm[0][2] * ctm[1][0]) + (ctm[0][0] * ctm[1][2]));
		float den = (float)((ctm[0][1] * ctm[1][0]) - (ctm[0][0] * ctm[1][1]));
		float ysol = num / den;
		float out[] = { (float)(x - ctm[0][2] - (ctm[0][1] * ysol)), ysol };
		return out;
	}
	
		/**
		 * Finds the inverse of this matrix and copies
		 * it into the given matrix.
		 * 
		 * So far we are doing a row reduction...
		 * But need to find a better way...
		 * 
		 * One trick we can use is that all entries
		 * in the last row except the last one will be 0...
		 */
	public void calcInverse(Transform t)
	{
		if (t == null || !(t instanceof Transform2D)) return ;
		
		float temp[][] = 
		{
			{ 1, ctm[0][1] / ctm[0][0], ctm[0][2]  / ctm[0][0]},
			{ ctm[1][0], ctm[1][1], ctm[1][2]},
			{ ctm[2][0], ctm[2][1], ctm[2][2]},
		};
		
		t.reset();	// reset t to a 1 by 1 matrix...
		
			// first reduce row one...
	}
}
