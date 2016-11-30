
package com.sri.apps.off;

import java.io.*;
import java.net.*;
import java.applet.*;
import java.util.*;
import java.util.zip.*;
import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.*;

public class OffApplet extends Applet implements ActionListener, KeyListener, ItemListener, WindowListener, MouseListener, CameraListener
{
    Button showFrameButton = new Button("Show Frame");
    Choice objectsAvail = new Choice();
    Checkbox sortFacesCheck = new Checkbox("Sort Faces", true);
    java.awt.List objectList = new java.awt.List();
    java.awt.List lightList = new java.awt.List();
    Label mesgLabel = new Label("");

    TextArea console = new TextArea("", 10, 50);
    TextField commandText = new TextField("", 50);
    NumField rotX = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField rotY = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField rotZ = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField transX = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField transY = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField transZ = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField scaleX = new NumField("1",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField scaleY = new NumField("1",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField scaleZ = new NumField("1",NumField.FLOAT, NumField.NEGATIVE, 3);

    Choice shadingChoices = new Choice();

    NumField nText = new NumField("100",NumField.INT,NumField.POSITIVE, 5);
    NumField kaText = new NumField("0.3",NumField.FLOAT,NumField.POSITIVE,5);
    NumField kdText = new NumField("0.4",NumField.FLOAT,NumField.POSITIVE,5);
    NumField ksText = new NumField("0.5",NumField.FLOAT,NumField.POSITIVE,5);

    NumField lightX = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField lightY = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField lightZ = new NumField("0",NumField.FLOAT, NumField.NEGATIVE, 3);
    NumField lightInt = new NumField("1", NumField.FLOAT,NumField.POSITIVE,3);

    NumField ambText = new NumField("0.8",NumField.FLOAT,NumField.POSITIVE,3);
    NumField widthText = new NumField("1000",NumField.INT,NumField.POSITIVE,3);
    NumField heightText = new NumField("800",NumField.INT,NumField.POSITIVE,3);
    NumField focalText = new NumField("1000",NumField.FLOAT,NumField.POSITIVE,3);

    Button apply = new Button("Render");
    Button addObject = new Button("Add Object");
    Button addLight = new Button("Add Light");
    Button remObject = new Button("Remove Object");
    Button remLight = new Button("Remove Light");

        // transform matrix for the current object...
    Transform coMatrix = new Transform();

    OffViewerPanel ofPanel = new OffViewerPanel();

    ScrollPane scroller = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    Frame mainFrame = new Frame("Off Viewer");
    Dialog controlDialog = new Dialog(mainFrame,"Options", false);

        // list of all the available off objects
    Vector polyObjects = new Vector();
        // the current polyhedral object
    Polyhedron currPoly = null;
    Light currLight = null;

    Camera camera = new Camera(1000, 1000, 800);
    Scene scene = new Scene();

    public void init(InputStream input)
    {
        init3();
        mesgLabel.setText("Reading Models...");
        if (input != null)
        {
            int i = 1;
            try
            {
                TarExtractor te = new TarExtractor(input);

                while (te.hasMoreFiles())
                {
                    InputStream in = te.nextFile();
                    String fName = te.getFileName();
                    //System.out.println(fName);
                    currPoly = OFFReader.readOffStream(in);
                    if (currPoly != null)
                    {
                        polyObjects.addElement(currPoly);
                        objectsAvail.add(fName);
                        i++;
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (objectsAvail.getItemCount() == 0) addObject.setEnabled(false);
        mesgLabel.setText("Opening Windows...");
        init4();
        mesgLabel.setText("Have Fun.");
    }

    public void init3()
    {
        initComponents();

                // also add a few lights...
        Light lights[] = {
            new Light(0.7, 1, 1, 1),
        };

        for (int i = 0;i < lights.length;i++)
        {
            scene.addLight(lights[i]);
            lightList.add("Light " + (i + 1));
        }

        currLight = lights[lights.length - 1];
        setLightTexts();

        setListeners();
    }

    void init4()
    {
        mainFrame.setBounds(50, 50, 800, 600);
        mainFrame.setVisible(true);
        mainFrame.toFront();
        controlDialog.pack();
        controlDialog.setSize(150, 400);
        controlDialog.setResizable(true);
        //controlDialog.setBounds(50, 50, 300, 300);
        controlDialog.setVisible(true);
        controlDialog.toFront();

                // and a few objects...
        String offs[] =
        {
            "ktests/space_shuttle.off 1 1 1 -60 30 20 00 -120 -800",
            "ktests/x29_plane.off 3 3 3 140.0 115.0 -110.0 000.0 -20.0 -300.0",
            "ktests/head.off 009 009 009 200.0 40.0 180.0 -150.0 -050.0 -400.0",
            "ktests/cube.off 070.0 070.0 070.0 -45.0 30.0 -65.0 090.0 -000.0 -500.0"
        };

        for (int i = 0;i < offs.length;i++)
        {
            processOffCommand(new StringTokenizer(offs[i]," \t",false));
        }
        renderIt();
    }

    public void init()
    {
        String filearchive = getParameter("file-archive");
        try
        {
            URL offFile = new URL(getCodeBase(), filearchive);
            InputStream input = offFile.openStream();;
            if (filearchive.endsWith(".gz"))
            {
                input = new GZIPInputStream(input);
            }
            init(input);
        } catch (Exception e)
        {
            e.printStackTrace();
            init4();
        }
    }

    protected void setListeners()
    {
        camera.addCameraListener(this);
        shadingChoices.addItemListener(this);
        sortFacesCheck.addItemListener(this);
        lightList.addItemListener(this);
        objectList.addItemListener(this);

        showFrameButton.addActionListener(this);
        addObject.addActionListener(this);
        remObject.addActionListener(this);
        addLight.addActionListener(this);
        remLight.addActionListener(this);
        apply.addActionListener(this);
        console.setEditable(false);
        commandText.addActionListener(this);
        rotX.addKeyListener(this);
        rotY.addKeyListener(this);
        rotZ.addKeyListener(this);
        transX.addKeyListener(this);
        transY.addKeyListener(this);
        transZ.addKeyListener(this);
        scaleX.addKeyListener(this);
        scaleY.addKeyListener(this);
        scaleZ.addKeyListener(this);

        lightX.addKeyListener(this);
        lightY.addKeyListener(this);
        lightZ.addKeyListener(this);
        lightInt.addKeyListener(this);

        ambText.addKeyListener(this);
        widthText.addKeyListener(this);
        heightText.addKeyListener(this);
        focalText.addKeyListener(this);

        mainFrame.addMouseListener(this);
        controlDialog.addWindowListener(this);
        mainFrame.addWindowListener(this);
        mainFrame.addWindowListener(this);
    }

    public void imageReady(SImage image)
    {
        apply.setLabel("Render");
    }

    public void showMessage(String mesg)
    {
        console.append(mesg);
    }

    public void destroy()
    {
        controlDialog.setVisible(false);
        mainFrame.setVisible(false);
        controlDialog.dispose();
        mainFrame.dispose();
    }

    public void mousePressed(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { mouseReleased(e); }
    public void mouseExited(MouseEvent e) { }
    public void mouseReleased(MouseEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        if (e.getSource() == controlDialog)
        {
            controlDialog.setVisible(false);
        } else if (e.getSource() == mainFrame)
        {
            mainFrame.setVisible(false);
        }
    }
    public void windowOpened(WindowEvent e) { }
    public void windowClosed(WindowEvent e)
    {
    }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
    public void windowActivated(WindowEvent e) { }

    public void keyTyped(KeyEvent e)
    {
        Object src = e.getSource();
        if (src instanceof TextField)
        {
            apply.setEnabled(true);
        }
    }

    public void keyPressed(KeyEvent e){ }
    public void keyReleased(KeyEvent e){ }

    public void itemStateChanged(ItemEvent e)
    {
        Object src = e.getSource();
        if (src == lightList)
        {
            int ind = lightList.getSelectedIndex();
            if (ind < 0) return ;
            currLight = scene.getLight(ind);
            setLightTexts();
        } else if (src == objectList)
        {
            int ind = objectList.getSelectedIndex();
            if (ind < 0) return ;
            currPoly = scene.getObject(ind);

            rotX.setText(currPoly.rx + "");
            rotY.setText(currPoly.ry + "");
            rotZ.setText(currPoly.rz + "");
            
            scaleX.setText(currPoly.sx + "");
            scaleY.setText(currPoly.sy + "");
            scaleZ.setText(currPoly.sz + "");
            
            transX.setText(currPoly.tx + "");
            transY.setText(currPoly.ty + "");
            transZ.setText(currPoly.tz + "");
        }
    }

    protected void renderIt()
    {
        updateTransMatrix();
        updateLightInfo();
        updateShadingInfo();

        if (currPoly == null) return ;

        currPoly.applyTransformation(coMatrix, false);

        /*if (sortFacesCheck.getState())
        {
            int nob = scene.getObjectCount();
            for (int i = 0;i < nob;i++)
            {
                scene.getObject(i).sortFaces();
            }
        }*/

        apply.setLabel("Stop");
        ofPanel.render();
        ofPanel.repaint();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if (src == showFrameButton)
        {
            mainFrame.setVisible(true);
            controlDialog.setVisible(true);
        }
        else if (src == apply)
        {
            if (apply.getLabel().equals("Render"))
            {
                console.setText("");
                apply.setLabel("Stop");
                renderIt();
            } else 
            {
                apply.setLabel("Render");
                console.append("\n-------  Rendering  Aborted --------");
                camera.stopRendering();
            }
        } else if (src == remObject)
        {
            int ind = objectList.getSelectedIndex();
            if (ind >= 0) objectList.remove(ind);
            scene.removeObject(ind);
        } else if (src == remLight)
        {
            int ind = lightList.getSelectedIndex();
            if (ind >= 0) lightList.remove(ind);
            scene.removeLight(ind);
        } else if (src == commandText)
        {
            String com = commandText.getText().trim();
            commandText.setText("");
            if (com.length() == 0) return ;
            StringTokenizer tokens = new StringTokenizer(com, " \t");
            String cType = tokens.nextToken();
            if (cType.equalsIgnoreCase("off"))
            {
                processOffCommand(tokens);
            } else if (cType.equalsIgnoreCase("light"))
            {
                processLightCommand(tokens);
            } else if (cType.equalsIgnoreCase("render"))
            {
                renderIt();
            } else if (processDoubleParamCommand(cType, tokens))
            {
            } else if (processSingleParamCommand(cType, tokens))
            {
            } else printHelp();
        } else if (src == addLight)
        {
            double lx, ly, lz, li;

            li = (new Double(lightInt.getText())).doubleValue();
            lx = (new Double(lightX.getText())).doubleValue();
            ly = (new Double(lightY.getText())).doubleValue();
            lz = (new Double(lightZ.getText())).doubleValue();

            if (lx == 0 && ly == 0 && lz == 0) return ;

            currLight = new Light(li, -lx, -ly, -lz);
            scene.addLight(currLight);

            setLightTexts();

            lightList.add("Light " + (lightList.getItemCount() + 1));
        } else if (src == addObject)
        {
            int ind = objectsAvail.getSelectedIndex();
            addNewObject(ind);
        }
    }

    void setLightTexts()
    {
        lightX.setText(-currLight.x + "");
        lightY.setText(-currLight.y + "");
        lightZ.setText(-currLight.z + "");
        lightInt.setText(currLight.intensity + "");
    }
    
    protected void addNewObject(int ind)
    {
        if (ind < 0) return ;
        currPoly = new Polyhedron((Polyhedron)polyObjects.elementAt(ind));
        //currPoly = (Polyhedron)polyObjects.elementAt(ind);
        objectList.add(objectsAvail.getItem(ind));
        scene.addObject(currPoly);
    }

    boolean processDoubleParamCommand(String com, StringTokenizer tokens)
    {
        try
        {
            if (com.equalsIgnoreCase("dim"))
            {
                int w = Integer.parseInt(tokens.nextToken());
                int h = Integer.parseInt(tokens.nextToken());
                if (w <= 0 || h <= 0)
                {
                    console.setText("Dimensions MUST be positive.");
                    return true;
                }
                widthText.setText(w + "");
                heightText.setText(h + "");
                camera.setDimension(w, h);
                return true;
            }
        } catch (Exception e)
        {
            printHelp();
        }
        return false;
    }

    protected void printHelp()
    {
        String mesg = 
            "Options: \n" + 
            "   off offfile sx sy sz rx ry rz tx ty tz\n" +
            "   light intensity xDir yDir zDir\n" + 
            "   n nValue\n" + 
            "   dim width height\n" + 
            "   focal focalLength\n" + 
            "   ambient ambient-light\n" + 
            "   ka ambient-reflection-coefficient\n" + 
            "   kd diffuse-reflection-coefficient\n" + 
            "   ks specular-reflection-coefficient\n" + 
            "   render ";
        console.setText(mesg);
    }

    boolean processSingleParamCommand(String com, StringTokenizer tokens)
    {
        try
        {
            if (com.equalsIgnoreCase("ka"))
            {
                kaText.setText(tokens.nextToken());
            } else if (com.equalsIgnoreCase("ks"))
            {
                kdText.setText(tokens.nextToken());
            } else if (com.equalsIgnoreCase("focal"))
            {
                focalText.setText(tokens.nextToken());
            } else if (com.equalsIgnoreCase("ks"))
            {
                ksText.setText(tokens.nextToken());
            }  else if (com.equalsIgnoreCase("ambient"))
            {
                ambText.setText(tokens.nextToken());
            }  else if (com.equalsIgnoreCase("n"))
            {
                nText.setText(tokens.nextToken());
            } else return false;
        } catch (Exception e)
        {
            printHelp();
        }
        return true;
    }

    protected void processLightCommand(StringTokenizer cType)
    {
        try
        {
            double lx, ly, lz, li;
            li = (new Double(cType.nextToken())).doubleValue();
            lx = (new Double(cType.nextToken())).doubleValue();
            ly = (new Double(cType.nextToken())).doubleValue();
            lz = (new Double(cType.nextToken())).doubleValue();

            if (lx == 0 && ly == 0 && lz == 0) 
            {
                console.setText("At least one parameter must be non-zero.");
                return ;
            }

            currLight = new Light(li, -lx, -ly, -lz);
            scene.addLight(currLight);

            setLightTexts();
            lightList.add("Light " + (lightList.getItemCount() + 1));
        } catch (Exception e)
        {
            printHelp();
        }
    }

    protected int findObject(String n)
    {
        int nob = objectsAvail.getItemCount();
        for (int i = 0;i < nob;i++)
        {
            if (objectsAvail.getItem(i).equals(n)) return i;
        }
        return -1;
    }

    protected void processOffCommand(StringTokenizer cType)
    {
        try
        {
            String oname = cType.nextToken();
            int ind = findObject(oname);

            if (ind < 0)
            {
                console.setText(oname + " not found.");
            }
            currPoly = new Polyhedron((Polyhedron)polyObjects.elementAt(ind));
            //currPoly = (Polyhedron)polyObjects.elementAt(ind);
            objectList.add(objectsAvail.getItem(ind));
            double sX = (new Double(cType.nextToken())).doubleValue();
            double sY = (new Double(cType.nextToken())).doubleValue();
            double sZ = (new Double(cType.nextToken())).doubleValue();

            double rX = (new Double(cType.nextToken())).doubleValue();
            double rY = (new Double(cType.nextToken())).doubleValue();
            double rZ = (new Double(cType.nextToken())).doubleValue();

            double tX = (new Double(cType.nextToken())).doubleValue();
            double tY = (new Double(cType.nextToken())).doubleValue();
            double tZ = (new Double(cType.nextToken())).doubleValue();

            currPoly.sx = sX; currPoly.sy = sY; currPoly.sz = sZ;
            currPoly.rx = rX; currPoly.ry = rY; currPoly.rz = rZ;
            currPoly.tx = tX; currPoly.ty = tY; currPoly.tz = tZ;

            scaleX.setText(sX + "");
            scaleY.setText(sY + "");
            scaleZ.setText(sZ + "");

            rotX.setText(rX + "");
            rotY.setText(rY + "");
            rotZ.setText(rZ + "");

            transX.setText(tX + "");
            transY.setText(tY + "");
            transZ.setText(tZ + "");

            coMatrix.reset();
            coMatrix.scale(sX, sY, sZ);
            coMatrix.rotate(Math.cos(rX * Math.PI / 180.0),
                            Math.sin(rX * Math.PI / 180.0),
                            Math.cos(rY * Math.PI / 180.0),
                            Math.sin(rY * Math.PI / 180.0),
                            Math.cos(rZ * Math.PI / 180.0),
                            Math.sin(rZ * Math.PI / 180.0));
            coMatrix.translate(tX, tY, tZ);
            currPoly.applyTransformation(coMatrix, false);

            scene.addObject(currPoly);
        } catch (Exception e)
        {
            printHelp();
        }
    }

    protected void initComponents()
    {
        setLayout(new BorderLayout());
        showFrameButton = new Button("Show Frame");
        Panel appletCenterPanel = new Panel(new BorderLayout());
        appletCenterPanel.add("Center", showFrameButton);
        appletCenterPanel.add("South", mesgLabel);
        add("Center", appletCenterPanel);
        
        Panel transPanel = new EtchedPanel("Transformations:");
        Panel currObjects = new EtchedPanel("Objects:");
        transPanel.setLayout(new BorderLayout());
        Panel westPanel = new Panel(new GridLayout(4, 1, 5, 5));
        westPanel.add(new Label(""));
        westPanel.add(new Label("Rotation: "));
        westPanel.add(new Label("Scaling: "));
        westPanel.add(new Label("Translation: "));
        Panel centerPanel = new Panel(new GridLayout(3, 1, 5, 5));
        Panel headingPanel = new Panel(new GridLayout(1, 3, 5, 5));
        headingPanel.add(new Label("X"));
        headingPanel.add(new Label("Y"));
        headingPanel.add(new Label("Z"));

        Panel p3[] = {
            new Panel(new GridLayout(1, 3, 5, 5)), 
            new Panel(new GridLayout(1, 3, 5, 5)), 
            new Panel(new GridLayout(1, 3, 5, 5)), 
        };

        p3[0].add(rotX); p3[0].add(rotY); p3[0].add(rotZ);
        p3[1].add(scaleX); p3[1].add(scaleY); p3[1].add(scaleZ);
        p3[2].add(transX); p3[2].add(transY); p3[2].add(transZ);

        ofPanel.setScene(scene);
        ofPanel.setCamera(camera);

        mainFrame.setLayout(new BorderLayout());
        scroller.add(ofPanel);
        mainFrame.add("Center", scroller);

        centerPanel.add(p3[0]); centerPanel.add(p3[1]); centerPanel.add(p3[2]);
        Panel transNP = new Panel(new BorderLayout());
        transNP.add("North", headingPanel);
        transNP.add("Center", centerPanel);
        transPanel.add("West", westPanel);
        transPanel.add("Center", transNP);
        //transPanel.add("North", headingPanel);
        currObjects.setLayout(new BorderLayout());
        currObjects.add("Center", objectList);

        Panel bPanel = new Panel();

        //bPanel.add(addObject);
        bPanel.add(remObject);
        bPanel.add(apply);

        Panel currObjectsPanel = new Panel(new BorderLayout());
        currObjectsPanel.add("Center", currObjects);
        currObjectsPanel.add("East", transPanel);

        Panel objectsAvailPanel = new Panel(new BorderLayout());
        objectsAvailPanel.add("Center", objectsAvail);
        objectsAvailPanel.add("East", addObject);

        Panel lightPanel = new EtchedPanel("Light: ");
        lightPanel.setLayout(new BorderLayout());
        Panel lnPanel = new Panel(new GridLayout(4, 2, 5, 5));
        lnPanel.add(new Label("Light X: "));
        lnPanel.add(lightX);
        lnPanel.add(new Label("Light Y: "));
        lnPanel.add(lightY);
        lnPanel.add(new Label("Light Z: "));
        lnPanel.add(lightZ);
        lnPanel.add(new Label("Light I: "));
        lnPanel.add(lightInt);

        Panel cPanel = new Panel();
        cPanel.setLayout(new BorderLayout());
        cPanel.add("North", objectsAvailPanel);
        cPanel.add("Center", currObjectsPanel);
        cPanel.add("South", bPanel);

        Panel b2Panel = new Panel();
        b2Panel.add(addLight);
        b2Panel.add(remLight);
        Panel eastLightPanel = new Panel(new BorderLayout());
        eastLightPanel.add("North", lnPanel);
        eastLightPanel.add("South", b2Panel);

        shadingChoices.add("Wire Frame");
        shadingChoices.add("Flat");
        shadingChoices.add("Gouraud");
        shadingChoices.add("Real Phong");
        shadingChoices.select(3);

        Panel othersPanel = new EtchedPanel("Others: ");
        othersPanel.setLayout(new GridLayout(5, 4, 5, 5));
        othersPanel.add(new Label("Width: "));
        othersPanel.add(widthText);
        othersPanel.add(new Label("Height: "));
        othersPanel.add(heightText);
        othersPanel.add(new Label("Focal Length: "));
        othersPanel.add(focalText);
        othersPanel.add(new Label("Ambience: "));
        othersPanel.add(ambText);
        othersPanel.add(new Label("N: "));
        othersPanel.add(nText);
        othersPanel.add(new Label("Ka: "));
        othersPanel.add(kaText);
        othersPanel.add(new Label("Kd: "));
        othersPanel.add(kdText);
        othersPanel.add(new Label("Ks: "));
        othersPanel.add(ksText);
        othersPanel.add(new Label("Shading Type: "));
        othersPanel.add(shadingChoices);
        othersPanel.add(sortFacesCheck);

        lightPanel.add("East", eastLightPanel);
        lightPanel.add("Center", lightList);

        Panel estPanel = new Panel(new BorderLayout());

        Panel cmainPanel = new Panel(new BorderLayout());
        cmainPanel.add("North", cPanel);
        cmainPanel.add("Center", lightPanel);
        cmainPanel.add("South", othersPanel);
        controlDialog.setLayout(new BorderLayout());

        Panel n2Panel = new Panel(new BorderLayout());
        n2Panel.add("West", new Label("Command: "));
        n2Panel.add("Center", commandText);
        Panel consolePanel = new Panel(new BorderLayout());
        //consolePanel.add("East", shPanel);
        consolePanel.add("Center", console);
        consolePanel.add("North", n2Panel);

        //controlDialog.add("South", consolePanel);
        //controlDialog.add("Center", cmainPanel);

        ScrollPane controlDialogPanel = new ScrollPane();
        controlDialogPanel.add("South", consolePanel);
        controlDialogPanel.add("Center", cmainPanel);
        controlDialog.add("Center", controlDialogPanel);
    }

    void updateShadingInfo()
    {
        int ind = shadingChoices.getSelectedIndex();
        if (ind == 0)
        {
            camera.shader = Camera.WIRE_SHADER;
        } else if (ind == 1)
        {
            camera.shader = Camera.FLAT_SHADER;
        } else if (ind == 2)
        {
            camera.shader = Camera.GOURAUD_SHADER;
        } else if (ind == 3)
        {
            camera.shader = Camera.PHONG_SHADER;
        }
        camera.shader.n = Integer.parseInt(nText.getText());
        camera.shader.ka = (new Double(kaText.getText())).doubleValue();
        camera.shader.kd = (new Double(kdText.getText())).doubleValue();
        camera.shader.ks = (new Double(ksText.getText())).doubleValue();
    }

    void updateLightInfo()
    {
        int w = Integer.parseInt(widthText.getText());
        int h = Integer.parseInt(heightText.getText());
        double fl = ((new Double(focalText.getText()))).doubleValue();
        double amb = ((new Double(ambText.getText()))).doubleValue();

        scene.setAmbientLight(amb);
        camera.setFocalLength(fl);
        camera.setDimension(w, h);
        mainFrame.validate();

        double lx = (new Double(lightX.getText())).doubleValue();
        double ly = (new Double(lightY.getText())).doubleValue();
        double lz = (new Double(lightZ.getText())).doubleValue();
        double li = (new Double(lightInt.getText())).doubleValue();

        if (currLight == null && lightList.getItemCount() > 0)
        {
            int ind = lightList.getSelectedIndex();
            if (ind < 0)
            {
                ind = 0;
                lightList.select(0);
            }
            currLight = scene.getLight(ind);
        }
        if (currLight == null) return ;
        currLight.intensity = li;
        currLight.x = lx;
        currLight.y = ly;
        currLight.z = lz;
    }

        /**
         * Update the transform matrix.
         */
    void updateTransMatrix()
    {
        double rX = (new Double(rotX.getText())).doubleValue();
        double rY = (new Double(rotY.getText())).doubleValue();
        double rZ = (new Double(rotZ.getText())).doubleValue();

        double sX = (new Double(scaleX.getText())).doubleValue();
        double sY = (new Double(scaleY.getText())).doubleValue();
        double sZ = (new Double(scaleZ.getText())).doubleValue();

        double tX = (new Double(transX.getText())).doubleValue();
        double tY = (new Double(transY.getText())).doubleValue();
        double tZ = (new Double(transZ.getText())).doubleValue();

            // apply the coMatrix to the current object...
        if (currPoly == null && objectList.getItemCount() > 0)
        {
            int ind = objectList.getSelectedIndex();
            if (ind < 0) ind = 0;
            currPoly = scene.getObject(ind);
        }

        if (currPoly != null)
        {
            currPoly.sx = sX; currPoly.sy = sY; currPoly.sz = sZ;
            currPoly.rx = rX; currPoly.ry = rY; currPoly.rz = rZ;
            currPoly.tx = tX; currPoly.ty = tY; currPoly.tz = tZ;
        }

        coMatrix.reset();
        coMatrix.scale(sX, sY, sZ);
        coMatrix.rotate(Math.cos(rX * Math.PI / 180.0),
                        Math.sin(rX * Math.PI / 180.0),
                        Math.cos(rY * Math.PI / 180.0),
                        Math.sin(rY * Math.PI / 180.0),
                        Math.cos(rZ * Math.PI / 180.0),
                        Math.sin(rZ * Math.PI / 180.0));
        coMatrix.translate(tX, tY, tZ);
    }

    public static void main(String args[])
    {
        try
        {
            InputStream input = new FileInputStream(args[0]);

            if (args[0].endsWith(".gz"))
            {
                input = new GZIPInputStream(input);
            }
            (new OffApplet()).init(input);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
