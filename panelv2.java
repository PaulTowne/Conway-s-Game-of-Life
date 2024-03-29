import javax.lang.model.type.IntersectionType;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.MappedByteBuffer;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

// This is my first working version of the game. The timer is not set, which means the spacebar controls how the generations are iterated through. 
public class panelv2 extends JPanel
{
    // creates movement variables
    boolean w;
    boolean a;
    boolean d;
    boolean s;
    boolean space=false;
    int accelerate = 0;
    // object storage
    ArrayList<AliveCell> matrix = new ArrayList<AliveCell>();
    ArrayList<DeadCell> deadCells = new ArrayList<DeadCell>();
    ArrayList<AliveCell> copy = new ArrayList<AliveCell>();
    ArrayList<AliveCell> nowAlive = new ArrayList<AliveCell>();
    // time and scaling
    int scale =20;
    Timer t;
    Timer t2;

    // create a reference cell which doesnt move. but it stores the offsets
    Reference ref = new Reference();
    // constructor
    public panelv2()
    {
        addKeyListener(new KeyEventDemo());
        //addMouseListener(new MouseClass());
        //Timer tdraw = new Timer(1,new TimeListenerDraw());
        t = new Timer(1, new TimeListener());
        t2 = new Timer(1000, new TimeListener2());
        //Timer t2= new Timer(20,new TimeListener2());
        addMouseWheelListener(new MouseWheel());
        addMouseListener(new MouseClick());
        setFocusable(true);
        t.start();
        //t2.start();

        
    }
    void moveObjects(int notch)
    {
        
    }
    // does all the heavy lifting for computing which cells survive or die out.
    void calculate()
    {
        copy.clear();
        for(int i=0;i<matrix.size();i++)
        {
            copy.add(matrix.get(i));
        }
        ArrayList<ArrayList<AliveCell>> range = new ArrayList<ArrayList<AliveCell>>();
        range.clear();
        //System.out.println("in calculate");
        //System.out.println("size of copy: "+copy.size());
        int loop = 0;
        double variabledist = 10;
        boolean added = false;
        // this breaks up the live cells into separate arraylists so that it is easier to check for collisions between them.
        while(!copy.isEmpty())
        {
            //System.out.println("in while loop");
            variabledist = 10;
            Random rand = new Random();
            Color rainbow = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
            int randomChoice = rand.nextInt(copy.size()); // well setting the origin is now random for each cluster. idk how better it is but its cool looking
            // setting rainbow color to be just blue for right now
            //rainbow = Color.blue;
            copy.get(randomChoice).setColor(rainbow);
            range.add(new ArrayList<AliveCell>());
            range.get(loop).add(copy.get(randomChoice));
            copy.remove(randomChoice);
            // loops until every live cell is in an arraylist.
            do{
                //System.out.println("in do while loop");
                 added = false;
                for(int j=0;j<copy.size();j++)
                {
                    int objx = range.get(loop).get(0).getX() - copy.get(j).getX();
                    int objy = range.get(loop).get(0).getY() - copy.get(j).getY();
                    double dis = Math.sqrt(Math.pow(objx,2)+Math.pow(objy,2));
                    if(dis<=variabledist)  
                    {
                        copy.get(j).setColor(rainbow);
                        range.get(loop).add(copy.get(j));
                        copy.remove(copy.get(j));
                        j--;
                        added = true;
                    }
                }
                if(added)
                    variabledist= variabledist+2;

                //System.out.println("variabledist ="+variabledist);
            }while(added);
            
            loop++;
        }
        // print block to check for lost data
        //System.out.println("-----------------------------------------------------------------");
        for(int i=0;i<range.size();i++)
        {
            //System.out.print(""+i+": ");
            for(int k=0;k<range.get(i).size();k++)
            {
                //System.out.print(range.get(i).get(k)+" ");
            }
            //System.out.println();
        }
        // checks for over/under population.
        int neighbor=0;
        for(int i=0;i<range.size();i++)
        {
            for(int j=0;j<range.get(i).size();j++)
            {
                AliveCell alive = range.get(i).get(j);
                neighbor=0;
                for(int k=0;k<range.get(i).size();k++)
                {
                    //if alive is adjecant to any of the other cells in the list other than itself
                    if(k!=j)
                    {
                        int x = alive.getX()-range.get(i).get(k).getX();
                        int y = alive.getY()-range.get(i).get(k).getY();
                        // substract and check to see if any Math.abs(>1); 
                        if(Math.abs(x)<=1 && Math.abs(y)<=1)
                        {
                            neighbor++;
                        }
                    }
                }
                // if neighbor is >3 then cell is set to dead. so lets either delete
                // the cells now or set a death function that gets called later.
                if(neighbor<2)
                    alive.alive(false);
                else if(neighbor>3)
                    alive.alive(false);
                else if(true)
                {
                    alive.alive(true);
                }
            }
           // System.out.println("neighbor: "+neighbor);
        }

        // now we check for cells that reproduce. 
        // This is checking all the neighbor cells which could come alive due to reproduction.
        nowAlive.clear();
        for(int i=0;i<range.size();i++)
        {
            ArrayList<AliveCell> temp = new ArrayList<AliveCell>();
            temp.clear();
            for(int k=0;k<range.get(i).size();k++)
            {
                AliveCell val = range.get(i).get(k);
                //System.out.println("Alive x and y :"+alive.getX()+" "+alive.getY());

                temp.add(new AliveCell(val.getX()-1,val.getY()-1));
                temp.add(new AliveCell(val.getX()+1,val.getY()-1));
                temp.add(new AliveCell(val.getX(),val.getY()-1));
                temp.add(new AliveCell(val.getX()-1,val.getY()));
                temp.add(new AliveCell(val.getX()+1,val.getY()));
                temp.add(new AliveCell(val.getX()-1,val.getY()+1));
                temp.add(new AliveCell(val.getX(),val.getY()+1));
                temp.add(new AliveCell(val.getX()+1,val.getY()+1));

                for(int l=0;l<range.get(i).size();l++)
                {
                    for(int o=0;o<temp.size();o++)
                    {
                        // we need to take out deadcells that overlap with live cells. 
                        if(temp.get(o).getX()==range.get(i).get(l).getX() && temp.get(o).getY() == range.get(i).get(l).getY())
                        {
                            temp.remove(o);
                            o--;
                        }
                    }
                }
                
            }
            // print out all deadcells
            //System.out.println("--------------------DeadCells---------------------");
            for(int j=0;j<temp.size();j++)
            {
                //System.out.print(" "+temp.get(j));
            }
            //System.out.println();
            // lets now loop over the temp array and look for duplicates
            int dup = 0;
            // deals with duplicates and adds "nowAlive" cells to the main arraylist.
            for(int j=0;j<temp.size();j++)
            {
                dup =0;
                AliveCell al = temp.get(j);
                for(int p=0;p<temp.size();p++)
                {
                    if(al.getX()==temp.get(p).getX() && al.getY() == temp.get(p).getY() && p!=j)
                    {
                        temp.remove(p);
                        dup++;
                        p--; 
                    }
                    /* 
                    System.out.println("--------------------Inside of dup++---------------------");
                    for(int u=0;u<temp.size();u++)
                    {
                        System.out.print(" "+temp.get(u));
                    }
                    System.out.println();
                    */
                }
                if(dup==2)
                {
                    nowAlive.add(al);
                    //System.out.println("Duplicate added: "+al);
                }
                temp.remove(j);
                j--;
            }

            // re printing Deadcells cause im curious to see if anything gets filtered
            //System.out.println("--------------DeadCells Post delete---------------------");
            for(int j=0;j<temp.size();j++)
            {
                //System.out.print(" "+temp.get(j));
            }
            //System.out.println();
        }

       
        // adds them back to copy
        for(int i=0;i<range.size();i++)
        {
            for(int j=0;j<range.get(i).size();j++)
            {
                copy.add(range.get(i).get(j));
            }
        }
        // we now need to add the alive ones and kill the not alive ones
        for(int i=0;i<copy.size();i++)
        {
            if(copy.get(i).isAlive()==false)
            {
                copy.remove(i);
                i--;
            }
        }
        // adding nowalive
        for(int i=0;i<nowAlive.size();i++)
        {
            copy.add(nowAlive.get(i));
        }




        matrix.clear();
        for(int i=0;i<copy.size();i++)
        {
            matrix.add(copy.get(i));
        }
    }
    public boolean nowAlive(ArrayList<AliveCell> arr, DeadCell c)
    {
        return false;
    }
    // controls all the graphics.
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g); 
        g.setColor(Color.white);
        g.fillRect(0,0,1000,1000);
        double grid = 1000/scale;
        g.setColor(Color.black);
        int gridx =ref.getX();
        int gridy =ref.getY();
        //System.out.println("gridx and y : "+gridx+" "+gridy);
        while(Math.abs(gridx)>scale)
        {
            if(gridx>0)
                gridx -= scale;
            else 
                gridx +=scale;
        }
        while(Math.abs(gridy)>scale)
        {
            if(gridy>0)
                gridy -= scale;
            else 
                gridy +=scale;
        }
        
        for(int i=0;i<1000;i++)
        {
            g.drawLine((i*scale)+gridx,0,(i*scale)+gridx,1000);
            g.drawLine(0,(i*scale)+gridy,1000,(i*scale)+gridy);
        }
        //System.out.println("gridx and y 2: "+gridx+" "+gridy);
        
        for(int i=0;i<matrix.size();i++)
        {
            matrix.get(i).draw(g, scale,ref);
        }
        
        //System.out.println("matrix size = "+matrix.size());
    }

    // action listener that controls traversing the plane by changing the reference.
    public class TimeListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int move = 9;
            if(w==true)
            {
                ref.setY(move);
            }
            if(a==true)
            {
                ref.setX(move);
            }
            if(s==true)
            {
                ref.setY(-move);
            }
            if(d==true)
            {
                ref.setX(-move);
            }
        
            repaint();
            
        }
    }
    public class TimeListener2 implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
           
            //collectDead();

        }
    }
    // adds or deletes cells based on user's choice.
    public class MouseClick extends MouseInputAdapter
    {
        public void mousePressed(MouseEvent me)
        {
            int x = me.getX();
            int y = me.getY();
            x = x - ref.getX();
            y = y -ref.getY();
            //System.out.println("x,y "+x+" "+y);
            while(x%scale !=0)
            {
                x--;
            }
            while(y%scale !=0)
            {
                y--;  
            }
            //System.out.println("x and y "+x+" "+y);
            boolean copied = false;
            for(int i=0;i<matrix.size();i++)
            {
                if((x/scale)== matrix.get(i).getX() && (y/scale)==matrix.get(i).getY())
                {
                    copied = true;
                    matrix.remove(i);
                    i--;
                }

            }
            if(copied ==false)
                matrix.add(new AliveCell(x/scale, y/scale));
        }
        public void mouseMoved(MouseEvent me)
        {

        }
        public void mouseDragged(MouseEvent me)
        {

        }
    }
    // allows for zooming in and out of plane.
    public class MouseWheel implements MouseWheelListener 
    {
        
        public void mouseWheelMoved(MouseWheelEvent e) {
           int notches = e.getWheelRotation();
           scale= scale+notches;
           if(scale<1)
            scale = 1;

           if(notches ==-1)
           {
                
               ref.setX(20);
               ref.setY(20);
           }
           else if(notches ==1)
           {
               ref.setX(- 20);
               ref.setY(-20);
           }
        }
    }
    // checks for wasd keys which control movement of plane.
    public class KeyEventDemo implements KeyListener
    {
        public void keyTyped(KeyEvent e){}
        public void keyReleased(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_W)
            {
                w = false;
                
            }
            if(e.getKeyCode() == KeyEvent.VK_A)
            {
                a = false;
              
            }
            if(e.getKeyCode() == KeyEvent.VK_S)
            {
                s = false;
                
            }
            if(e.getKeyCode() == KeyEvent.VK_D)
            {
                d = false;
                
            }
            if(e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                //space = false;
            }
            if(e.getKeyCode() == KeyEvent.VK_E)
            {
                //e1= false;
            }
        }
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_W)
            {
                w = true;
                
            }
            if(e.getKeyCode() == KeyEvent.VK_A)
            {
                a = true;
                
                //System.out.println("click a");
            }
            if(e.getKeyCode() == KeyEvent.VK_S)
            {
                s = true;
                
            }
            if(e.getKeyCode() == KeyEvent.VK_D)
            {
                d = true;
                
            }
            if(e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                 calculate();
            }
            
        }
    }

}
