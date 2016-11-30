package com.sri.gui.ext.drawing;

import java.util.*;

public interface ShapeListener extends EventListener
{
	public void shapeMoved(Shape sh) ;
	public void shapeResized(Shape sh);
	public void shapeDeleted(Shape sh);
}
