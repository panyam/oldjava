package svtool.gui.anim;

import java.awt.*;

/**
 * Super class of layer objects.
 */
public interface AnimationLayer 
{
        /**
         * Paints the layer in the current frame.
         */
    public void draw(Graphics g, Dimension d);

        /**
         * Goes forward by "numSteps" frames.
         */
    public void forward(int numSteps);

        /**
         * Goes backward by "numSteps" frames.
         */
    public void backward(int numSteps);
}
