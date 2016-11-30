package com.sri.apps.off;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class OffViewerPanel extends Panel implements CameraListener
{
    static ColorModel ourCM = new DirectColorModel(32, 0xff0000,0x00ff00, 
                                         0x0000ff,0xff000000);
    Scene currScene;
    Image rendered = null;
    Camera camera;
    Dimension prefSize = new Dimension();

        /**
         * Constructor.
         */
    public OffViewerPanel()
    {
    }

    public void setScene(Scene cs)
    {
        currScene = cs;
        rendered = null;
    }

    public void setCamera(Camera ca)
    {
        camera = ca;
        rendered = null;
        camera.addCameraListener(this);
    }

    public void showMessage(String mesg)
    {
    }

    public Dimension getPreferredSize()
    {
        if (camera != null)
        {
            prefSize.width = camera.width;
            prefSize.height = camera.height;
        }
        return prefSize;
    }

    public void render()
    {
        rendered = null;
        if (camera == null || currScene == null) return ;

        camera.processScene(currScene);
    }

    public void imageReady(SImage simage)
    {
        System.out.println("Image Ready...");
        rendered = null;
        MemoryImageSource mis = new MemoryImageSource(simage.getCols(), simage.getRows(), ourCM, simage.getPixelArray(), 0, simage.getCols());
        rendered = createImage(mis);
        paint(getGraphics());
    }

    public void paint(Graphics g)
    {
        if (currScene == null || rendered == null) return ;
        g.drawImage(rendered, 0, 0, null);
    }
}
