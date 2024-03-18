import javax.lang.model.type.IntersectionType;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

// this class ended up not being used but may be in future development.
public class Reference 
{
    // reference is set at 0,0 upon creation. The values will never change. what will change is the offset, which will graphically change up stuff. like you take the x and y and add the 
    // offsets to get the true value. 
    private int xpos=0;
    private int ypos=0;

    public Reference()
    {

    }
    public void draw(Graphics g,int size)
    {
        
    }
    public int getX()
    {
        return xpos;
    }
    public int getY()
    {
        return ypos;
    }
    public void setX(int x)
    {
        xpos = xpos+x;
    }
    public void setY(int y)
    {
        ypos = ypos+y; 
    }

}
