package com.sri.apps.off;

import java.util.*;

class Camera implements Runnable
{
        /**
         * The algorithm used to shade each face.
         */
    FaceShader shader;

    protected Thread renderer = new Thread(this);

    protected SImage image = new SImage(10, 10);

    protected Scene scene;

        /**
         * if this flag is true, then the window bounds
         * are set as the maximum and minimum values of 
         * the scene.
         */
    boolean autoBounds;

        /**
         * The focal length of the camera.
         */
    double focalLength;

        /**
         * The width of the camera.
         */
    int width;

        /**
         * The height of the camera.
         */
    int height;

        /**
         * The View Up Vector.
         */
    SVector vup;

        /**
         * The View Point Normal.
         */
    SVector vpn;

        /**
         * The bounds of the window on the projection plane.
         */
    double umax, umin, vmax, vmin;

        /**
         * The view reference point of the camera.
         */
    Vertex vrp;

        /**
         * The projection reference point.
         */
    Vertex prp;

    final static int PERSPECTIVE_PROJECTION = 0;
    final static int PARALLEL_PROJECTION = 1;

    Vector clisteners = new Vector();

        /**
         * Flat Shading Flag.
         */
    static FaceShader WIRE_SHADER = new WireShader();

        /**
         * Flat Shading Flag.
         */
    static FaceShader FLAT_SHADER = new FlatShader();

        /**
         * Gouraud Shading Flag.
         */
    static FaceShader GOURAUD_SHADER = new GouraudShader();

        /**
         * Phong Shading Flag.
         */
    static FaceShader PHONG_SHADER = new PhongShader();

        /**
         * Creates a new camera;
         */
    Camera(double fl, int w, int h)
    {
        setFocalLength(fl);
        setDimension(w, h);

                // create the default shader...
        shader = FLAT_SHADER;
    }

    void setDimension(int w, int h)
    {
        this.width = w;
        this.height = h;
        image.setSize(w, h);
    }

    void setFocalLength(double fl)
    {
        this.focalLength = fl;

        vrp = new Vertex(0, 0, 0);
        prp = new Vertex(0, 0, focalLength);
        vpn = new SVector(0,0,1);
        vup = new SVector(0,1,0);

        autoBounds = true;
    }

        /**
         * Sets the face shader.
         */
    void setShader(FaceShader sh)
    {
        this.shader = sh;
    }

        /**
         * Gets the face shader.
         */
    FaceShader getShader()
    {
        return shader;
    }

        /**
         * Sets the view reference point.
         */
    void setVRP(double x, double y, double z)
    {
        vrp.x = x;
        vrp.y = y;
        vrp.z = z;
    }

        /**
         * Sets the direction of the camera.
         *
         * The VPN and VUP HAVE to be perpendicular to each other
         * ie their dot product MUST be zero.
         *
         * The output is whether the two vectors are 
         * perpendicular or not.
         */
    boolean setViewSVectors(SVector vup, SVector vpn)
    {
        if (!vup.isPerpendicularTo(vpn)) return false;

        this.vpn.setValues(vpn.x, vpn.y, vpn.z);
        this.vup.setValues(vup.x, vup.y, vup.z);

        return true;
    }

        /**
         * The camera will be located at the origin and 
         * facing the Z direction by the right hand coordinate
         * convention.
         */
    void setDefaults()
    {
            // by default vrp is the origin
        vrp.x = vrp.y = 0;
        vrp.z = 0;

        prp.x = prp.y = 0;
        prp.z = focalLength;

            // by default the VUP vector is the y axes
        vup.setValues(0,1,0);

            // by default the vpn is the vector is the z axes
        vpn.setValues(0,0,1);
    }

    Transform calcTransform()
    {
        Transform transform = new Transform();

                //////////    Step  1       ////////////////
                // if the camera is located at x, y, z it is
                // equivalent to translating the objects 
                // by -x, -y, -z.
        transform.translate(-vrp.x, -vrp.y, -vrp.z);

                /////////////   Step 2      ///////////////////
                // now to find the parameters of the rotation matrix..
                // | r1x r2x r3x 0 |
                // | r1y r2y r3y 0 |
                // | r1z r2z r3z 0 |
                // |  0   0   0  1 |
        Transform rotation = new Transform();

            // Rz is the normalized version of vpn
        vpn.normalize();

        vup.normalize();

            //rx = VUP x VPN
        SVector ry = vup;
        SVector rz = vpn;
        SVector rx = ry.cross(rz);

        rx.normalize();

        rotation.ctm[0][0] = rx.x;
        rotation.ctm[0][1] = rx.y;
        rotation.ctm[0][2] = rx.z;

        rotation.ctm[1][0] = ry.x;
        rotation.ctm[1][1] = ry.y;
        rotation.ctm[1][2] = ry.z;

        rotation.ctm[2][0] = rz.x;
        rotation.ctm[2][1] = rz.y;
        rotation.ctm[2][2] = rz.z;

            // apply the rotation onto the transform...
        transform.preMultiply(rotation);

        return transform;
    }

    public void run()
    {
        double infDepth = -1e100;

                // set z buffer to infinite depth..
        image.initZBuffer(infDepth);

                // how many objects are in the scene???
        int nObjects = scene.getObjectCount();

                // set shader's ambient light parameter
        if (shader != null) shader.ia = scene.getAmbientLight();

            // initialise
        scene.createAET(scene.maxVertices);

        System.gc();

        for (int i = 0;i < nObjects;i++)
        {
            Polyhedron obj = scene.getObject(i);

            if (shader != null) shader.vertices = obj.vertices;

            showMessage("\nProcessing Object "+(i + 1) +":-\n" +
                        "   Calculating Vertex Normals...");
            obj.calculateVertexNormals();
            showMessage(" Done.\n");

            if (shader != null)
            {
                showMessage("   Calculating Vertex Intensities...");
                obj.calculateVertexIntensities(scene.getLights(), shader);
                showMessage(" Done.\n");
            }

            showMessage("   Projecting object...");
            obj.calcPixelValues(width, height, focalLength);
            showMessage(" Done.\n");

            showMessage("   Rendering to image buffer...");
            obj.draw(shader, image, scene.getLights(),
                        scene.aet, scene.globalET, focalLength);
            showMessage(" Done.\n");
        }
        int nl = clisteners.size();
        for (int i = 0;i < nl;i++)
        {
            ((CameraListener)clisteners.elementAt(i)).imageReady(image);
        }
    }

        /**
         * Processes a scene and projects the objects on the
         * scene to this camera and the output is an image.
         */
    void processScene(Scene scene)
    {
        this.scene = scene;
        image.change();
        renderer = new Thread(this);
        renderer.start();
    }

    void stopRendering()
    {
        if (renderer != null && renderer.isAlive()) renderer.stop();
        int nl = clisteners.size();
        for (int i = 0;i < nl;i++)
        {
            ((CameraListener)clisteners.elementAt(i)).imageReady(image);
        }
    }

    void addCameraListener(CameraListener l)
    {
        if (l != null && !clisteners.contains(l))
        {
            clisteners.addElement(l);
        }
    }

    void showMessage(String mesg)
    {
        int nl = clisteners.size();
        for (int i = 0;i < nl;i++)
        {
            ((CameraListener)clisteners.elementAt(i)).showMessage(mesg);
        }
    }
}
