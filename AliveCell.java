import javax.lang.model.type.IntersectionType;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

public class AliveCell 
{
    // make the logic position consistant so we can easily scale it for size.
    // gather the 8 positions around every live cell (checking that something doesnt exist there)
    private int xpos;
    private int ypos;
    private Color c= Color.blue;
    private boolean alive = true;

    public AliveCell(int x, int y)
    {
        xpos = x;
        ypos = y;
    }
    public void draw(Graphics g,int size,Reference r)
    {
        g.setColor(c);
        //System.out.println(size);
        g.fillRect((xpos*size)+r.getX(),(ypos*size)+r.getY(),size,size);
    }
    public void alive(boolean b)
    {
        alive = b;
    }
    public boolean isAlive()
    {
        return alive;
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
    public void setColor(Color c)
    {
        this.c = c;
    }
    public String toString()
    {
        return "("+xpos+","+ypos+")";
    }

}
