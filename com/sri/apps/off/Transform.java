package com.sri.apps.off;

class Transform
{
    final static double EPS = 1e-5;

    final static boolean isZero(double num)
    {
        return (Math.abs(num) < EPS);
    }
        /**
         * The Current Transform Matrix.
         */
    double ctm[][] = new double[4][4];

        /**
         * Constructor.
         */
    Transform()
    {
        reset();
    }


        /**
         * A Copy constructor.
         */
    Transform(Transform another)
    {
        copyFrom(another);
    }


        /**
         * Copies values from another transformation matrix.
         */
    void copyFrom(Transform another)
    {
        // copy row 0
        ctm[0][0] = another.ctm[0][0]; ctm[0][1] = another.ctm[0][1];
        ctm[0][2] = another.ctm[0][2]; ctm[0][3] = another.ctm[0][3];

        // copy row 1
        ctm[1][0] = another.ctm[1][0]; ctm[1][1] = another.ctm[1][1];
        ctm[1][2] = another.ctm[1][2]; ctm[1][3] = another.ctm[1][3];

        // copy row 2
        ctm[2][0] = another.ctm[2][0]; ctm[2][1] = another.ctm[2][1];
        ctm[2][2] = another.ctm[2][2]; ctm[2][3] = another.ctm[2][3];

        // copy row 3
        ctm[3][0] = another.ctm[3][0]; ctm[3][1] = another.ctm[3][1];
        ctm[3][2] = another.ctm[3][2]; ctm[3][3] = another.ctm[3][3];
    }


        /**
         * Resets this matrix to the identity matrix.
         */
    void reset()
    {
        ctm[0][1] = ctm[0][2] = ctm[0][3] = 0;
        ctm[1][0] = ctm[1][2] = ctm[1][3] = 0;
        ctm[2][0] = ctm[2][1] = ctm[2][3] = 0;
        ctm[3][0] = ctm[3][1] = ctm[3][2] = 0;

        ctm[0][0] = ctm[1][1]= ctm[2][2] = ctm[3][3] = 1.0;
    }

        /**
         * Returns a row of the matrix.
         */
    double []getRow(int index)
    {
        return ctm[index];
    }

        /**
         * Pre multiplies another matrix to this matrix.
         *
         * So 
         * CTM = another.CTM * CTM
         */
    void preMultiply(Transform t)
    {
        double temp[][] = 
        {
            { ctm[0][0], ctm[0][1], ctm[0][2], ctm[0][3]    }, 
            { ctm[1][0], ctm[1][1], ctm[1][2], ctm[1][3]    }, 
            { ctm[2][0], ctm[2][1], ctm[2][2], ctm[2][3]    }, 
            { ctm[3][0], ctm[3][1], ctm[3][2], ctm[3][3]    }, 
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
         * Post multiplies another matrix to this matrix.
         */
    void postMultiply(Transform t)
    {
        double temp[][] =
        {
            { t.ctm[0][0], t.ctm[0][1], t.ctm[0][2], t.ctm[0][3]    }, 
            { t.ctm[1][0], t.ctm[1][1], t.ctm[1][2], t.ctm[1][3]    }, 
            { t.ctm[2][0], t.ctm[2][1], t.ctm[2][2], t.ctm[2][3]    }, 
            { t.ctm[3][0], t.ctm[3][1], t.ctm[3][2], t.ctm[3][3]    }, 
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
         * Given a point x, y, z, transforms it.
         */
    void transform(Vertex v)
    {
        v.transX= ctm[0][0] * v.x + ctm[0][1] * v.y + ctm[0][2] * v.z+ctm[0][3];
        v.transY= ctm[1][0] * v.x + ctm[1][1] * v.y + ctm[1][2] * v.z+ctm[1][3];
        v.transZ= ctm[2][0] * v.x + ctm[2][1] * v.y + ctm[2][2] * v.z+ctm[2][3];
    }


        /**
         * Given a point x, y, z, transforms it and stores it
         * in newX, newY, newZ.
         */
    void transform(double x, double y, double z, Vertex v)
    {
        v.x = ctm[0][0] * x + ctm[0][1] * y + ctm[0][2] * z + ctm[0][3];
        v.y = ctm[1][0] * x + ctm[1][1] * y + ctm[1][2] * z + ctm[1][3];
        v.z = ctm[2][0] * x + ctm[2][1] * y + ctm[2][2] * z + ctm[2][3];
    }


        /**
         * Given a point x,y,z returns the x coordinate of the
         * transformed point.
         */
    double transformX(double x, double y, double z)
    {
        return ctm[0][0] * x + ctm[0][1] * y + ctm[0][2] * z + ctm[0][3];
    }


        /**
         * Given a point x,y,z returns the y coordinate of the
         * transformed point.
         */
    double transformY(double x, double y, double z)
    {
        return ctm[1][0] * x + ctm[1][1] * y + ctm[1][2] * z + ctm[1][3];
    }


        /**
         * Given a point x,y,z returns the z coordinate of the
         * transformed point.
         */
    double transformZ(double x, double y, double z)
    {
        return ctm[2][0] * x + ctm[2][1] * y + ctm[2][2] * z + ctm[2][3];
    }


        /**
         * Apply a translation to this matrix.
         *
         * So 
         * CTM = CTM * another.CTM
         */
    void translate(double deltaX, double deltaY, double deltaZ)
    {
        if (!isZero(deltaX))
        {
            ctm[0][0] += (deltaX * ctm[3][0]);
            ctm[0][1] += (deltaX * ctm[3][1]);
            ctm[0][2] += (deltaX * ctm[3][2]);
            ctm[0][3] += (deltaX * ctm[3][3]);
        }
        if (!isZero(deltaY))
        {
            ctm[1][0] += (deltaY * ctm[3][0]);
            ctm[1][1] += (deltaY * ctm[3][1]);
            ctm[1][2] += (deltaY * ctm[3][2]);
            ctm[1][3] += (deltaY * ctm[3][3]);
        }
        if (!isZero(deltaZ))
        {
            ctm[2][0] += (deltaZ * ctm[3][0]);
            ctm[2][1] += (deltaZ * ctm[3][1]);
            ctm[2][2] += (deltaZ * ctm[3][2]);
            ctm[2][3] += (deltaZ * ctm[3][3]);
        }
    }


        /**
         * Applies a translation on the X axis.
         *
         * Note that, even though we have a general translation function 
         * that translates on all axes, some times we might be interested
         * in translating only on the X axis.  Which means that we dont 
         * have to apply a translation on the other two 
         * axes.  This is done so that we would not have to waste
         * time by doing additional multiplications than
         * necessary.
         */
    void translateX(double tx)
    {
        if (!isZero(tx))
        {
            ctm[0][0] += (tx * ctm[3][0]);
            ctm[0][1] += (tx * ctm[3][1]);
            ctm[0][2] += (tx * ctm[3][2]);
            ctm[0][3] += (tx * ctm[3][3]);
        }
    }


        /**
         * Applies a translation on the Y axis.
         *
         * Note that, even though we have a general translation function 
         * that translates on all axes, some times we might be interested
         * in translating only on the Y axis.  Which means that we dont 
         * have to apply a translation on the other two 
         * axes.  This is done so that we would not have to waste
         * time by doing additional multiplications than
         * necessary.
         */
    void translateY(double ty)
    {
        if (!isZero(ty))
        {
            ctm[1][0] += (ty * ctm[3][0]);
            ctm[1][1] += (ty * ctm[3][1]);
            ctm[1][2] += (ty * ctm[3][2]);
            ctm[1][3] += (ty * ctm[3][3]);
        }
    }


        /**
         * Applies a translation on the Z axis.
         *
         * Note that, even though we have a general translation function 
         * that translates on all axes, some times we might be interested
         * in translating only on the Z axis.  Which means that we dont 
         * have to apply a translation on the other two 
         * axes.  This is done so that we would not have to waste
         * time by doing additional multiplications than
         * necessary.
         */
    void translateZ(double tz)
    {
        if (!isZero(tz))
        {
            ctm[2][0] += (tz * ctm[3][0]);
            ctm[2][1] += (tz * ctm[3][1]);
            ctm[2][2] += (tz * ctm[3][2]);
            ctm[2][3] += (tz * ctm[3][3]);
        }
    }


        /**
         * Applies a rotation about the X axes, with the given angle.
         *
         * Note that instead of spec    ifying the angles, theta, 
         * cos theta and sine theta are spec    ified.
         *
         * This is mainly done in order to ensure that cos and 
         * sine values already exist and this object does not care
         * how these values are calculated.
         * For instance,  sometimes cos and sin are simply found from
         * vector values and to find the angles, an inverse cos or sin
         * needs to be applied.  This step can be skipped by passing
         * cos and sin values rather than angles.
         *
         * This is simply pre multiplication by the matrix:
         * 
         *          | 1    0    0    0 |
         *  A =     | 0    c    -s   0 |
         *          | 0    s    c    0 |
         *          | 0    0    0    1 |
         *
         * Another trick we are using is that the current matrix 
         * is always in the form:
         *
         *          | a1 b1 c1 d1 |
         *  ctm =   | a2 b2 c2 d2 |
         *          | a3 b3 c3 d3 |
         *          | 0  0  0  1  |
         *
         * ie the last row will be 0 0 0 1
         *
         * So after the transform this matrix will be 
         * A * ctm which is:
         *
         * same as ctm but row 0 and row 3 are unchanged.
         * and row an entry in row 1 is :
         *      (c * row 1 entry) - (s * row 2 entry)
         *
         * and for row 2:
         *      (s * row 1 entry) + (c * row 2 entry)
         */
    void rotateX(double c,double s)
    {
        // row 0 and row 3 dont change...
        // copy rows 0 and 1 into a temporary matrix...
        double row1[] =     { ctm[1][0], ctm[1][1], ctm[1][2], ctm[1][3]     };
        double row2[] =     { ctm[2][0], ctm[2][1], ctm[2][2], ctm[2][3]     };
    
        // row 1 is c * row 1 - s * row 2
        ctm[1][0] = (c * row1[0]) - (s * row2[0]);
        ctm[1][1] = (c * row1[1]) - (s * row2[1]);
        ctm[1][2] = (c * row1[2]) - (s * row2[2]);
        ctm[1][3] = (c * row1[3]) - (s * row2[3]);
    
        // row 2 is s * row 1 + c * row 2
        ctm[2][0] = (row1[0] * s) + (c * row2[0]);
        ctm[2][1] = (row1[1] * s) + (c * row2[1]);
        ctm[2][2] = (row1[2] * s) + (c * row2[2]);
        ctm[2][3] = (row1[3] * s) + (c * row2[3]);
    }



        /**
         * Applies a rotation about the Y axes, with the given angle.
         *
         * Note that instead of spec    ifying the angles, theta, 
         * cos theta and sine theta are spec    ified.
         *
         * This is mainly done in order to ensure that cos and 
         * sine values already exist and this object does not care
         * how these values are calculated.
         * For instance,  sometimes cos and sin are simply found from
         * vector values and to find the angles, an inverse cos or sin
         * needs to be applied.  This step can be skipped by passing
         * cos and sin values rather than angles.
         *
         * This is simply pre multiplication by the matrix:
         * 
         *          | c    0    s    0 |
         *  A =     | 0    1    0    0 |
         *          | -s   0    c    0 |
         *          | 0    0    0    1 |
         *
         * Once again the current matrix is always in the form:
         *
         *          | a1 b1 c1 d1 |
         *  ctm =   | a2 b2 c2 d2 |
         *          | a3 b3 c3 d3 |
         *          | 0  0  0  1  |
         *
         * ie the last row will be 0 0 0 1
         *
         * So after the transform this matrix will be 
         * A * ctm which is:
         *
         * same as ctm but row 1 and row 3 are unchanged.
         *
         * and row an entry in row 0 is :
         *      (c * row 0 entry) + (s * row 2 entry)
         *
         * and for row 2:
         *      (c * row 2 entry) - (s * row 0 entry)
         */
    void rotateY(double c,double s)
    {
        // row 1 and row 3 dont change...
        // copy rows 0 and 2 into a temporary matrix...
        double row0[] =     { ctm[0][0], ctm[0][1], ctm[0][2], ctm[0][3]     };
        double row2[] =     { ctm[2][0], ctm[2][1], ctm[2][2], ctm[2][3]     };
    
        // row 0 is c * row 0 + s * row 2
        ctm[0][0] = (c * row0[0]) + (s * row2[0]);
        ctm[0][1] = (c * row0[1]) + (s * row2[1]);
        ctm[0][2] = (c * row0[2]) + (s * row2[2]);
        ctm[0][3] = (c * row0[3]) + (s * row2[3]);
    
        // row 2 is c * row 2 - s * row 0
        ctm[2][0] = (row2[0] * c) - (s * row0[0]);
        ctm[2][1] = (row2[1] * c) - (s * row0[1]);
        ctm[2][2] = (row2[2] * c) - (s * row0[2]);
        ctm[2][3] = (row2[3] * c) - (s * row0[3]);
    }


        /**
         * Applies a rotation about the Z axes, with the given angle.
         *
         * Note that instead of spec    ifying the angles, theta, 
         * cos theta and sine theta are spec    ified.  
         *
         * This is mainly done in order to ensure that cos and 
         * sine values already exist and this object does not care
         * how these values are calculated.
         * For instance,  sometimes cos and sin are simply found from
         * vector values and to find the angles, an inverse cos or sin
         * needs to be applied.  This step can be skipped by passing
         * cos and sin values rather than angles.
         *
         * This is simply pre multiplication by the matrix:
         * 
         *          | c    -s   0    0 |
         *  A =     | s    c    0    0 |
         *          | 0    0    1    0 |
         *          | 0    0    0    1 |
         *
         * Once again the current matrix is always in the form:
         *
         *          | a1 b1 c1 d1 |
         *  ctm =   | a2 b2 c2 d2 |
         *          | a3 b3 c3 d3 |
         *          | 0  0  0  1  |
         *
         * ie the last row will be 0 0 0 1
         *
         * So after the transform this matrix will be 
         * A * ctm which is:
         *
         * same as ctm but row 2 and row 3 are unchanged.
         *
         * and row an entry in row 0 is :
         *      (c * row 0 entry) - (s * row 1 entry)
         *
         * and for row 1:
         *      (s * row 0 entry) + (c * row 1 entry)
         */
    void rotateZ(double c,double s)
    {
        // row 2 and row 3 dont change...
        // copy rows 0 and 1 into a temporary matrix...
        double row0[] = { ctm[0][0], ctm[0][1], ctm[0][2], ctm[0][3]     };
        double row1[] = { ctm[1][0], ctm[1][1], ctm[1][2], ctm[1][3]     };
    
        // row 0 is c * row 0 - s * row 1
        ctm[0][0] = (c * row0[0]) - (s * row1[0]);
        ctm[0][1] = (c * row0[1]) - (s * row1[1]);
        ctm[0][2] = (c * row0[2]) - (s * row1[2]);
        ctm[0][3] = (c * row0[3]) - (s * row1[3]);
    
        // row 1 is s * row 0 + c * row 1
        ctm[1][0] = (row0[0] * s) + (c * row1[0]);
        ctm[1][1] = (row0[1] * s) + (c * row1[1]);
        ctm[1][2] = (row0[2] * s) + (c * row1[2]);
        ctm[1][3] = (row0[3] * s) + (c * row1[3]);
    }


        /**
         * Applies a rotation about the X axis, followed by the Y 
         * axis followed by the Z axis.
         *
         * Note that instead of spec    ifying the angles, theta, 
         * cos theta and sine theta are spec    ified.
         *
         * This is mainly done in order to ensure that cos and 
         * sine values already exist and this object does not care
         * how these values are calculated.
         *
         * The other advantege is that these cos and sin values
         * are independent of whether we are using radians or degrees.
         */
    void rotate(double cX,double sX,double cY,double sY,double cZ,double sZ)
    {
        rotateX(cX, sX);        // first apply the rotation about the x axis
        rotateY(cY, sY);        // first apply the rotation about the y axis
        rotateZ(cZ, sZ);        // first apply the rotation about the z axis
    }

        /**
         * Applies a scaling on this transform object.
         *
         * | sx 0 0 0 |
         * | 0 sy 0 0 | * thismatrix
         * | 0 0 sz 0 |
         * | 0 0  1 0 |
         */
    void scale(double sx, double sy, double sz)
    {
        if (!isZero(sx))
        {
            ctm[0][0] *= sx;
            ctm[0][1] *= sx;
            ctm[0][2] *= sx;
            ctm[0][3] *= sx;
        }
        if (!isZero(sy))
        {
            ctm[1][0] *= sy;
            ctm[1][1] *= sy;
            ctm[1][2] *= sy;
            ctm[1][3] *= sy;
        }
        if (!isZero(sz))
        {
            ctm[2][0] *= sz;
            ctm[2][1] *= sz;
            ctm[2][2] *= sz;
            ctm[2][3] *= sz;
        }
    }


        /**
         * Applies a scaling with respect to the X axis. 
         *
         * Note that, even though we have a general scale function that
         * scales on all axes, some times we might be interested
         * in scaling only with respect to the X axes.  Which means
         * that we dont have to apply a scaling on the other two 
         * axes.  This is done so that we would not have to waste
         * time by doing additional multiplications than
         * necessary.
         */
    void scaleX(double sx)
    {
        if (!isZero(sx))
        {
            ctm[0][0] *= sx;
            ctm[0][1] *= sx;
            ctm[0][2] *= sx;
            ctm[0][3] *= sx;
        }
    }


        /**
         * Applies a scaling with respect to the Y axis. 
         *
         * Note that, even though we have a general scale function that
         * scales on all axes, some times we might be interested
         * in scaling only with respect to the Y axes.  Which means
         * that we dont have to apply a scaling on the other two 
         * axes.  This is done so that we would not have to waste
         * time by doing additional multiplications than
         * necessary.
         */
    void scaleY(double sy)
    {
        if (!isZero(sy))
        {
            ctm[1][0] *= sy;
            ctm[1][1] *= sy;
            ctm[1][2] *= sy;
            ctm[1][3] *= sy;
        }
    }


        /**
         * Applies a scaling with respect to the Z axis. 
         *
         * Note that, even though we have a general scale function that
         * scales on all axes, some times we might be interested
         * in scaling only with respect to the Z axes.  Which means
         * that we dont have to apply a scaling on the other two 
         * axes.  This is done so that we would not have to waste
         * time by doing additional multiplications than
         * necessary.
         */
    void scaleZ(double sz)
    {
        if (!isZero(sz))
        {
            ctm[2][0] *= sz;
            ctm[2][1] *= sz;
            ctm[2][2] *= sz;
            ctm[2][3] *= sz;
        }
    }
}
