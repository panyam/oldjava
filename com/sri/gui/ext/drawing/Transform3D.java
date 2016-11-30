package com.sri.gui.ext.drawing;

public class Transform3D extends Transform
{
	public Transform3D()
	{
		ctm = new float[4][4];
		reset();
	}
	
		/**
		 * Resets the transformation to the identity matrix
		 */
	public void reset()
	{
		ctm[0][1] = ctm[0][2] = ctm[0][3] = 0;
		ctm[1][0] = ctm[1][2] = ctm[1][3] = 0;
		ctm[2][0] = ctm[2][1] = ctm[2][3] = 0;
		ctm[3][0] = ctm[3][1] = ctm[3][2] = 0;
		ctm[0][0] = ctm[1][1] = ctm[2][2] = ctm[3][3] = 1;
	}
	
		/**
		 * Returns the x/y/z value after applying this transformation to it.
		 */
	public float transformX3D(int which,float x,float y,float z)
	{
		return (ctm[which][0] * x + ctm[which][1] * y + ctm[which][2] * z + ctm[which][3]);
	}
	
		/**
		 * Copies the transformation matrix of another transform
		 * to ths one.
		 */
	public void copyFrom(Transform tr)
	{
		ctm[0][0] = tr.ctm[0][0]; ctm[0][1] = tr.ctm[0][1]; ctm[0][2] = tr.ctm[0][2]; ctm[0][3] = tr.ctm[0][3];
		ctm[1][0] = tr.ctm[1][0]; ctm[1][1] = tr.ctm[1][1]; ctm[1][2] = tr.ctm[1][2]; ctm[1][3] = tr.ctm[1][3];
		ctm[2][0] = tr.ctm[2][0]; ctm[2][1] = tr.ctm[2][1]; ctm[2][2] = tr.ctm[2][2]; ctm[2][3] = tr.ctm[2][3];
		ctm[3][0] = tr.ctm[3][0]; ctm[3][1] = tr.ctm[3][1]; ctm[3][2] = tr.ctm[3][2]; ctm[3][3] = tr.ctm[3][3];
	}
		/**
		 * Applies scaling to this matrix.
		 * 
		 * So
		 * 
		 * | sx 0 0 0 |
		 * | 0 sy 0 0 | * thismatrix
		 * | 0 0 sz 0 |
		 * | 0 0  1 0 |
		 */
	public void scale(float sx, float sy, float sz)
	{
		if (sx != 0)
		{
			ctm[0][0] *= sx;
			ctm[1][0] *= sx;
			ctm[2][0] *= sx;
			ctm[3][0] *= sx;
		}
		if (sy != 0)
		{
			ctm[0][1] *= sy;
			ctm[1][1] *= sy;
			ctm[2][1] *= sy;
			ctm[3][1] *= sx;
		}
		if (sz != 0)
		{
			ctm[0][2] *= sz;
			ctm[1][2] *= sz;
			ctm[2][2] *= sz;
			ctm[3][2] *= sz;
		}
	}
	
		/**
		 * Sets the translation of this matrix.
		 */ 
	public void translate(float deltaX, float deltaY, float deltaZ)
	{
		if (deltaX != 0)
		{
			ctm[0][0] += (deltaX * ctm[3][0]);
			ctm[0][1] += (deltaX * ctm[3][1]);
			ctm[0][2] += (deltaX * ctm[3][2]);
			ctm[0][3] += (deltaX * ctm[3][3]);
		}
		if (deltaY != 0)
		{
			ctm[1][0] += (deltaY * ctm[3][0]);
			ctm[1][1] += (deltaY * ctm[3][1]);
			ctm[1][2] += (deltaY * ctm[3][2]);
			ctm[1][3] += (deltaY * ctm[3][3]);
		}
		if (deltaZ != 0)
		{
			ctm[2][0] += (deltaZ * ctm[3][0]);
			ctm[2][1] += (deltaZ * ctm[3][1]);
			ctm[2][2] += (deltaZ * ctm[3][2]);
			ctm[2][3] += (deltaZ * ctm[3][3]);
		}
	}

		/**
		 * Sets the translation of this matrix.
		 */ 
	public void translate(float deltaX, float deltaY)
	{
		translate(deltaX, deltaY, 0);
	}

		/**
		 * Rotation along the x axis.
		 */
	public final void rotateX(int degrees)
	{
		rotateX((float)cos[degrees < 0 ? -degrees : degrees],(float)(degrees < 0 ? -sin[-degrees] : sin[degrees]));
	}
	
		/**
		 * Rotation along the y axis.
		 */
	public final void rotateY(int degrees)
	{
		rotateY((float)cos[degrees < 0 ? -degrees : degrees],(float)(degrees < 0 ? -sin[-degrees] : sin[degrees]));
	}
	
		/**
		 * Rotation along the z axis.
		 */
	public final void rotateZ(int degrees)
	{
		rotateZ((float)cos[degrees < 0 ? -degrees : degrees],(float)(degrees < 0 ? -sin[-degrees] : sin[degrees]));
	}

		/**
		 * Applies this to a transform t and stores the 
		 * result in this.
		 * So it is :
		 *	[ ctm ] * [ c.ctm ]
		 */
	public void applyTo(Transform t)
	{
		if (!(t instanceof Transform3D)) return ;
		float temp[][] = 
		{
			{ t.ctm[0][0], t.ctm[0][1], t.ctm[0][2], t.ctm[0][3]}, 
			{ t.ctm[1][0], t.ctm[1][1], t.ctm[1][2], t.ctm[1][3]}, 
			{ t.ctm[2][0], t.ctm[2][1], t.ctm[2][2], t.ctm[2][3]}, 
			{ t.ctm[3][0], t.ctm[3][1], t.ctm[3][2], t.ctm[3][3]}, 
		};
		
		ctm[0][0] = (ctm[0][0] * temp[0][0]) + (ctm[0][1] * temp[1][0]) + 
					(ctm[0][2] * temp[2][0]) + (ctm[0][3] * temp[3][0]);
		ctm[0][1] = (ctm[0][0] * temp[0][1]) + (ctm[0][1] * temp[1][1]) + 
					(ctm[0][2] * temp[2][1]) + (ctm[0][3] * temp[3][1]);
		ctm[0][2] = (ctm[0][0] * temp[0][2]) + (ctm[0][1] * temp[1][2]) + 
					(ctm[0][2] * temp[2][2]) + (ctm[0][3] * temp[3][2]);
		ctm[0][3] = (ctm[0][0] * temp[0][3]) + (ctm[0][1] * temp[1][3]) + 
					(ctm[0][2] * temp[2][3]) + (ctm[0][3] * temp[3][3]);
		ctm[1][0] = (ctm[1][0] * temp[0][0]) + (ctm[1][1] * temp[1][0]) + 
					(ctm[1][2] * temp[2][0]) + (ctm[1][3] * temp[3][0]);
		ctm[1][1] = (ctm[1][0] * temp[0][1]) + (ctm[1][1] * temp[1][1]) + 
					(ctm[1][2] * temp[2][1]) + (ctm[1][3] * temp[3][1]);
		ctm[1][2] = (ctm[1][0] * temp[0][2]) + (ctm[1][1] * temp[1][2]) + 
					(ctm[1][2] * temp[2][2]) + (ctm[1][3] * temp[3][2]);
		ctm[1][3] = (ctm[1][0] * temp[0][3]) + (ctm[1][1] * temp[1][3]) + 
					(ctm[1][2] * temp[2][3]) + (ctm[1][3] * temp[3][3]);
		ctm[2][0] = (ctm[2][0] * temp[0][0]) + (ctm[2][1] * temp[1][0]) + 
					(ctm[2][2] * temp[2][0]) + (ctm[2][3] * temp[3][0]);
		ctm[2][1] = (ctm[2][0] * temp[0][1]) + (ctm[2][1] * temp[1][1]) + 
					(ctm[2][2] * temp[2][1]) + (ctm[2][3] * temp[3][1]);
		ctm[2][2] = (ctm[2][0] * temp[0][2]) + (ctm[2][1] * temp[1][2]) + 
					(ctm[2][2] * temp[2][2]) + (ctm[2][3] * temp[3][2]);
		ctm[2][3] = (ctm[2][0] * temp[0][3]) + (ctm[2][1] * temp[1][3]) + 
					(ctm[2][2] * temp[2][3]) + (ctm[2][3] * temp[3][3]);
		ctm[3][0] = (ctm[3][0] * temp[0][0]) + (ctm[3][1] * temp[1][0]) + 
					(ctm[3][2] * temp[2][0]) + (ctm[3][3] * temp[3][0]);
		ctm[3][1] = (ctm[3][0] * temp[0][1]) + (ctm[3][1] * temp[1][1]) + 
					(ctm[3][2] * temp[2][1]) + (ctm[3][3] * temp[3][1]);
		ctm[3][2] = (ctm[3][0] * temp[0][2]) + (ctm[3][1] * temp[1][2]) + 
					(ctm[3][2] * temp[2][2]) + (ctm[3][3] * temp[3][2]);
		ctm[3][3] = (ctm[3][0] * temp[0][3]) + (ctm[3][1] * temp[1][3]) + 
					(ctm[3][2] * temp[2][3]) + (ctm[3][3] * temp[3][3]);
	}
	
		/**
		 * Applies a transform t to this transform.
		 * So it is :
		 *	[ c.ctm ] * [ ctm ]
		 */
	public void applyFrom(Transform t)
	{
		if (!(t instanceof Transform3D)) return ;
		
		float temp[][] = 
		{
			{ ctm[0][0], ctm[0][1], ctm[0][2], ctm[0][3]}, 
			{ ctm[1][0], ctm[1][1], ctm[1][2], ctm[1][3]}, 
			{ ctm[2][0], ctm[2][1], ctm[2][2], ctm[2][3]}, 
			{ ctm[3][0], ctm[3][1], ctm[3][2], ctm[3][3]}, 
		};
		
		ctm[0][0] = (t.ctm[0][0] * temp[0][0]) + (t.ctm[0][1] * temp[1][0]) + 
					(t.ctm[0][2] * temp[2][0]) + (t.ctm[0][3] * temp[3][0]);
		ctm[0][1] = (t.ctm[0][0] * temp[0][1]) + (t.ctm[0][1] * temp[1][1]) + 
					(t.ctm[0][2] * temp[2][1]) + (t.ctm[0][3] * temp[3][1]);
		ctm[0][2] = (t.ctm[0][0] * temp[0][2]) + (t.ctm[0][1] * temp[1][2]) + 
					(t.ctm[0][2] * temp[2][2]) + (t.ctm[0][3] * temp[3][2]);
		ctm[0][3] = (t.ctm[0][0] * temp[0][3]) + (t.ctm[0][1] * temp[1][3]) + 
					(t.ctm[0][2] * temp[2][3]) + (t.ctm[0][3] * temp[3][3]);
		ctm[1][0] = (t.ctm[1][0] * temp[0][0]) + (t.ctm[1][1] * temp[1][0]) + 
					(t.ctm[1][2] * temp[2][0]) + (t.ctm[1][3] * temp[3][0]);
		ctm[1][1] = (t.ctm[1][0] * temp[0][1]) + (t.ctm[1][1] * temp[1][1]) + 
					(t.ctm[1][2] * temp[2][1]) + (t.ctm[1][3] * temp[3][1]);
		ctm[1][2] = (t.ctm[1][0] * temp[0][2]) + (t.ctm[1][1] * temp[1][2]) + 
					(t.ctm[1][2] * temp[2][2]) + (t.ctm[1][3] * temp[3][2]);
		ctm[1][3] = (t.ctm[1][0] * temp[0][3]) + (t.ctm[1][1] * temp[1][3]) + 
					(t.ctm[1][2] * temp[2][3]) + (t.ctm[1][3] * temp[3][3]);
		ctm[2][0] = (t.ctm[2][0] * temp[0][0]) + (t.ctm[2][1] * temp[1][0]) + 
					(t.ctm[2][2] * temp[2][0]) + (t.ctm[2][3] * temp[3][0]);
		ctm[2][1] = (t.ctm[2][0] * temp[0][1]) + (t.ctm[2][1] * temp[1][1]) + 
					(t.ctm[2][2] * temp[2][1]) + (t.ctm[2][3] * temp[3][1]);
		ctm[2][2] = (t.ctm[2][0] * temp[0][2]) + (t.ctm[2][1] * temp[1][2]) + 
					(t.ctm[2][2] * temp[2][2]) + (t.ctm[2][3] * temp[3][2]);
		ctm[2][3] = (t.ctm[2][0] * temp[0][3]) + (t.ctm[2][1] * temp[1][3]) + 
					(t.ctm[2][2] * temp[2][3]) + (t.ctm[2][3] * temp[3][3]);
		ctm[3][0] = (t.ctm[3][0] * temp[0][0]) + (t.ctm[3][1] * temp[1][0]) + 
					(t.ctm[3][2] * temp[2][0]) + (t.ctm[3][3] * temp[3][0]);
		ctm[3][1] = (t.ctm[3][0] * temp[0][1]) + (t.ctm[3][1] * temp[1][1]) + 
					(t.ctm[3][2] * temp[2][1]) + (t.ctm[3][3] * temp[3][1]);
		ctm[3][2] = (t.ctm[3][0] * temp[0][2]) + (t.ctm[3][1] * temp[1][2]) + 
					(t.ctm[3][2] * temp[2][2]) + (t.ctm[3][3] * temp[3][2]);
		ctm[3][3] = (t.ctm[3][0] * temp[0][3]) + (t.ctm[3][1] * temp[1][3]) + 
					(t.ctm[3][2] * temp[2][3]) + (t.ctm[3][3] * temp[3][3]);
	}
	
		/**
		 * Rotate along the x axis.
		 * This is simply multiplication by the matrix:
		 * 
		 *	1	0	0	0
		 *	0	c	-s	0
		 *	0	s	c	0
		 *	0	0	0	1
		 * 
		 * c and s are cos and sin theta respectively
		 */
	public void rotateX(float c, float s)
	{
		// row 0 and row 3 dont change...
		float tmp[][] = 
		{
			{ ctm[1][0], ctm[2][0] },
			{ ctm[1][1], ctm[2][1] },
			{ ctm[1][2], ctm[2][2] },
			{ ctm[1][3], ctm[2][3] },
		};
		
			// row 1 is c * row 1 - s * row 2
		ctm[1][0] = (c * tmp[0][0] - s * tmp[0][1]);
		ctm[1][1] = (c * tmp[1][0] - s * tmp[1][1]);
		ctm[1][2] = (c * tmp[2][0] - s * tmp[2][1]);
		ctm[1][3] = (c * tmp[3][0] - s * tmp[3][1]);
		
			// row 2 is s * row 1 + c * row 2
		ctm[2][0] = (s * tmp[0][0] + c * tmp[0][1]);
		ctm[2][1] = (s * tmp[1][0] + c * tmp[1][1]);
		ctm[2][2] = (s * tmp[2][0] + c * tmp[2][1]);
		ctm[2][3] = (s * tmp[3][0] + c * tmp[3][1]);
	}
	
		/**
		 * Rotate along the y axis.
		 * This is simply multiplication by the matrix:
		 * 
		 *	c	0	s	0
		 *	0	1	0	0
		 *	-s	0	c	0
		 *	0	0	0	1
		 * 
		 * c and s are cos and sin theta respectively
		 */
	public void rotateY(float c, float s)
	{
		// row 1 and row 3 dont change...
		float tmp[][] = 
		{
			{ ctm[0][0], ctm[2][0] },
			{ ctm[0][1], ctm[2][1] },
			{ ctm[0][2], ctm[2][2] },
			{ ctm[0][3], ctm[2][3] },
		};
		
			// row 0 is c * row 0 - s * row 2
		ctm[0][0] = (c * tmp[0][0] - s * tmp[0][1]);
		ctm[0][1] = (c * tmp[1][0] - s * tmp[1][1]);
		ctm[0][2] = (c * tmp[2][0] - s * tmp[2][1]);
		ctm[0][3] = (c * tmp[3][0] - s * tmp[3][1]);
		
			// row 2 is s * row 0 + c * row 2
		ctm[2][0] = (s * tmp[0][0] + c * tmp[0][1]);
		ctm[2][1] = (s * tmp[1][0] + c * tmp[1][1]);
		ctm[2][2] = (s * tmp[2][0] + c * tmp[2][1]);
		ctm[2][3] = (s * tmp[3][0] + c * tmp[3][1]);
	}
	
		/**
		 * Rotate along the z axis.
		 * This is simply multiplication by the matrix:
		 * 
		 *	|	c	-s	0	0	|
		 *	|	s	c	0	0	|	[ ctm ]
		 *	|	0	0	1	0	|
		 *	|	0	0	0	1	|
		 * 
		 * c and s are cos and sin theta respectively
		 */
	public void rotateZ(float c, float s)
	{
		float tmp[][] = 
		{
			{ ctm[0][0], ctm[1][0] },
			{ ctm[0][1], ctm[1][1] },
			{ ctm[0][2], ctm[1][2] },
			{ ctm[0][3], ctm[1][3] },
		};
		
			// row two and three do not change.
		
			// row one is s * row 0 + c * row 1;
		ctm[1][0] = (s * tmp[0][0] + c * tmp[0][1]);
		ctm[1][1] = (s * tmp[1][0] + c * tmp[1][1]);
		ctm[1][2] = (s * tmp[2][0] + c * tmp[2][1]);
		ctm[1][3] = (s * tmp[3][0] + c * tmp[3][1]);
		
			// row zero is c * row 0 - s * row 1
		ctm[0][0] = (c * tmp[0][0] - s * tmp[0][1]);
		ctm[0][1] = (c * tmp[1][0] - s * tmp[1][1]);
		ctm[0][2] = (c * tmp[2][0] - s * tmp[2][1]);
		ctm[0][3] = (c * tmp[3][0] - s * tmp[3][1]);
	}

		/**
		 * Rotate along the x axis.
		 * This is simply multiplication by the matrix:
		 * 
		 *	1	0	0	0
		 *	0	c	-s	0
		 *	0	s	c	0
		 *	0	0	0	1
		 * 
		 * c and s are cos and sin theta respectively
		 */
	public void rotateX(float c, float s, float wrtX, float wrtY, float wrtZ)
	{
		// row 0 and row 3 dont change...
		float tmp[][] = 
		{
			{ ctm[1][0], ctm[2][0] },
			{ ctm[1][1], ctm[2][1] },
			{ ctm[1][2], ctm[2][2] },
			{ ctm[1][3], ctm[2][3] },
		};
		
			// row 1 is c * row 1 - s * row 2
		ctm[1][0] = (c * tmp[0][0] - s * tmp[0][1]);
		ctm[1][1] = (c * tmp[1][0] - s * tmp[1][1]);
		ctm[1][2] = (c * tmp[2][0] - s * tmp[2][1]);
		ctm[1][3] = (c * tmp[3][0] - s * tmp[3][1]);
		
			// row 2 is s * row 1 + c * row 2
		ctm[2][0] = (s * tmp[0][0] + c * tmp[0][1]);
		ctm[2][1] = (s * tmp[1][0] + c * tmp[1][1]);
		ctm[2][2] = (s * tmp[2][0] + c * tmp[2][1]);
		ctm[2][3] = (s * tmp[3][0] + c * tmp[3][1]);
	}
	
		/**
		 * Rotate along the y axis.
		 * This is simply multiplication by the matrix:
		 * 
		 *	c	0	s	0
		 *	0	1	0	0
		 *	-s	0	c	0
		 *	0	0	0	1
		 * 
		 * c and s are cos and sin theta respectively
		 */
	public void rotateY(float c, float s, float wrtX, float wrtY, float wrtZ)
	{
		// row 1 and row 3 dont change...
		float tmp[][] = 
		{
			{ ctm[0][0], ctm[2][0] },
			{ ctm[0][1], ctm[2][1] },
			{ ctm[0][2], ctm[2][2] },
			{ ctm[0][3], ctm[2][3] },
		};
		
			// row 0 is c * row 0 - s * row 2
		ctm[0][0] = (c * tmp[0][0] - s * tmp[0][1]);
		ctm[0][1] = (c * tmp[1][0] - s * tmp[1][1]);
		ctm[0][2] = (c * tmp[2][0] - s * tmp[2][1]);
		ctm[0][3] = (c * tmp[3][0] - s * tmp[3][1]);
		
			// row 2 is s * row 0 + c * row 2
		ctm[2][0] = (s * tmp[0][0] + c * tmp[0][1]);
		ctm[2][1] = (s * tmp[1][0] + c * tmp[1][1]);
		ctm[2][2] = (s * tmp[2][0] + c * tmp[2][1]);
		ctm[2][3] = (s * tmp[3][0] + c * tmp[3][1]);
	}
	
		/**
		 * Rotate along the z axis.
		 * This is simply multiplication by the matrix:
		 * 
		 *	|	c	-s	0	0	|
		 *	|	s	c	0	0	|	[ ctm ]
		 *	|	0	0	1	0	|
		 *	|	0	0	0	1	|
		 * 
		 * c and s are cos and sin theta respectively
		 */
	public void rotateZ(float c, float s, float wrtX, float wrtY, float wrtZ)
	{
		float tmp[][] = 
		{
			{ ctm[0][0], ctm[1][0] },
			{ ctm[0][1], ctm[1][1] },
			{ ctm[0][2], ctm[1][2] },
			{ ctm[0][3], ctm[1][3] },
		};
		
			// row two and three do not change.
		
			// row one is s * row 0 + c * row 1;
		ctm[1][0] = (s * tmp[0][0] + c * tmp[0][1]);
		ctm[1][1] = (s * tmp[1][0] + c * tmp[1][1]);
		ctm[1][2] = (s * tmp[2][0] + c * tmp[2][1]);
		ctm[1][3] = (s * tmp[3][0] + c * tmp[3][1]);
		
			// row zero is c * row 0 - s * row 1
		ctm[0][0] = (c * tmp[0][0] - s * tmp[0][1]);
		ctm[0][1] = (c * tmp[1][0] - s * tmp[1][1]);
		ctm[0][2] = (c * tmp[2][0] - s * tmp[2][1]);
		ctm[0][3] = (c * tmp[3][0] - s * tmp[3][1]);
	}
	
		/**
		 * Finds the inverse of this matrix and copies
		 * it into the given matrix.
		 */
	public void calcInverse(Transform t)
	{
		if (!(t instanceof Transform3D)) return ;
	}
}
