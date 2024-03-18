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

// This is my first iteration of the program. This version does not work, but I used it a lot for testing different algorithms. 

public class panel extends JPanel
{
    // global variables
    // control movement
    boolean w;
    boolean a;
    boolean d;
    boolean s;
    boolean space=false;
    int accelerate = 0;
    // controls object categories
    ArrayList<AliveCell> matrix = new ArrayList<AliveCell>();
    ArrayList<DeadCell> deadCells = new ArrayList<DeadCell>();
    ArrayList<AliveCell> copy = new ArrayList<AliveCell>();
    ArrayList<AliveCell> nowAlive = new ArrayList<AliveCell>();
    // scaling and time variables
    int scale =20;
    Timer t;
    Timer t2;

    // create a reference cell which doesnt move. but it stores the offsets
    Reference ref = new Reference();
    //creates listeners and timers
    public panel()
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
    // make it where I can scale the size of the viewing window. make so i can pan around the environment. set different speeds, 
    // wasd will change the position of every alive square, we wont have to actually edit the map per say.
    // we also change depending on the scale shift
    // lets make a universal shift towards the center. so we keep everything consistent. separate logic of scaling of cells and grids, work them independently
    void moveObjects(int notch)
    {
        
    }
    
    // lets create a 2d arraylist where I can sort the cells. This will allow me to most effectively see which cells are near each other. 
    // or make multiple arralists where each one is like a chunk of cells that are within some distance of any of the cells in the array. so like a cell
    // will be tested to see if it is near any of them, if not itll make a new arraylist.
    // we check the deadcells algorithm for each arraylist individually so we dont have to check them if they are super far away.
    // we keep making arraylists to make room, but delete arraylists that are like smaller or like we can combine with others. 
    // we need to every so often check to see if arraylists need to combine.// or we kick out objects that get too far from the average position of the list 
    // we can also compare the average distances of other arraylists and see if they are near each other.  

    // does all the heavy lifting for the program. Analyzes the live cells, determines which are dead cells (neighbors) and uses algorithms to choose which ones die or live.
    void calculate()
    {
        // we still need to split it up a bit to help out. I think we have to brute force something somewhere. 
        copy.clear();
        // so we don't affect the original data
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
        // sets "origin" points which allow us to group cells together that are next to one another.
        // these origin points and the cells that are nearby are grouped into arraylists. 
        // the output is an arraylist of arraylists filled with AliveCells.
        while(!copy.isEmpty())
        {
            //System.out.println("in while loop");
            variabledist = 10;
            Random rand = new Random();
            Color rainbow = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
            int randomChoice = rand.nextInt(copy.size()); // well setting the origin is now random for each cluster. idk how better it is but its cool looking
            // setting rainbow color to be just blue for right now
            rainbow = Color.blue;
            copy.get(randomChoice).setColor(rainbow);
            range.add(new ArrayList<AliveCell>());
            range.get(loop).add(copy.get(randomChoice));
            copy.remove(randomChoice);
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
        //System.out.println("size of copy:"+copy.size());
        // we are going to re-add everything to the matrix so we can make sure this doesnt like lose data. cause all this should do is break it up. lets also add a tostring at some point 
        // ^this should add everything back. this is my check to make sure
        // I am not losing data in the sorting.
        // yo it works lol. Next steps is to get deadcells and start figuring out if cells die or not.
        // lets do the check for over and under population for live cells, we dont care about
        // dead cells yet. 

        // This determines rule 1, 2 and 3. It determines how many neighbors a cell has which determines whether it lives or dies.
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

        // lets add up all the dead cells for each arraylist
        // add all of them to a temp arraylist and count for duplicates
        // exactly 3 duplicates = reproduction

        // what if we took ever dead cell that wasn't a live cell and checked how close
        // it was to any live cell instead of checking for duplicates.I think the duplicates
        // is a bit too abstract and prone to errors.
        // so  we would take temp for each range array and check against all live cells

        // This determines which neighboring cells (the 8 surrounding 1 live cell) will become alive. This satisfies rule 4.
        ArrayList<DeadCell> temp = new ArrayList<DeadCell>();
        for(int i=0;i<range.size();i++)
        {
            for(int j=0;j<range.get(i).size();j++)
            {
                AliveCell val = range.get(i).get(j);
                temp.add(new DeadCell(val.getX()-1,val.getY()-1));
                temp.add(new DeadCell(val.getX()+1,val.getY()-1));
                temp.add(new DeadCell(val.getX(),val.getY()-1));
                temp.add(new DeadCell(val.getX()-1,val.getY()));
                temp.add(new DeadCell(val.getX()+1,val.getY()));
                temp.add(new DeadCell(val.getX()-1,val.getY()+1));
                temp.add(new DeadCell(val.getX(),val.getY()+1));
                temp.add(new DeadCell(val.getX()+1,val.getY()+1));
            }
            for(int k=0;k<range.get(i).size();k++)
            { // this checks to make sure a dead cell isnt taking a place of a live cell
                AliveCell sd = range.get(i).get(k);
                for(int l =0;l<temp.size();l++)
                {
                    if(sd.getX() == temp.get(l).getX() && sd.getY()==temp.get(l).getY())
                    {
                        temp.remove(l);
                        l--;
                    }
                }
            }
        }
        deadCells = temp;
        // we need to check that the deadcell added is not equal to any of the live
        //cells. Cause we dont care about live cell neighbors when it comes to counting
        //dead cells
        // so we are going to compare each deadcell to every live cell in the list 

        

        // we have all the deadcells added, time to check for duplicates
        // If a neighbor cell is duplicated 3 times, it means 3 live cells contributed, meaning it is now alive.
        nowAlive = new ArrayList<AliveCell>();
        nowAlive.clear();
        for(int i=0;i<deadCells.size();i++)
        {
            DeadCell c = deadCells.get(i);
            int dup =0;
            for(int j=0;j<deadCells.size();j++)
            {
                if(c.getX()==deadCells.get(j).getX() && c.getY()==deadCells.get(j).getY() && i!=j)
                {
                    dup++; 
                    deadCells.remove(j);
                    j--;
                }
            }
            if(dup ==2)
            {
                //this means "c"is a duplicate. so we set its status as alive.
                nowAlive.add(new AliveCell(c.getX(),c.getY()));
                deadCells.remove(i);
            }
        }
        // adds nowalive cells to main list.
        range.add(nowAlive);
        // this adds everything together and prints to update me for testing.
        System.out.println("matrix  range   deadcell    nowAlive");
        System.out.println(matrix.size()+"        "+range.size()+"        "+deadCells.size()+"            "+nowAlive.size());
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
        matrix.clear();
        for(int i=0;i<copy.size();i++)
        {
            matrix.add(copy.get(i));
        }
    }
    // paint component which does all the graphics output.
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
        // drawing dead cells for error checking
        for(int i=0;i<deadCells.size();i++)
        {
            deadCells.get(i).draw(g,scale,ref);
        }
        // this puts the copy arraylist which is the "copied data" after
        // it was sorted. it should be identical to matrix.

        //now lets look at "now alive"
        for(int i=0;i<nowAlive.size();i++)
        {
            nowAlive.get(i).setColor(Color.red);
            nowAlive.get(i).draw(g, scale, ref);
        }
        for(int i=0;i<copy.size();i++)
        {
            //copy.get(i).draw(g, scale,ref);
        }
        //this circle should represent the range at which the main cell is referencing for each group
        
        
        //System.out.println("matrix size = "+matrix.size());
    }
    // listener allows you to scroll but resetting a reference.
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
    // previously used in testing
    public class TimeListener2 implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
           
            //collectDead();

        }
    }
    // allows the user to create and delete cells
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
    // allow the user to scroll in and out. changing the scale.
    public class MouseWheel implements MouseWheelListener 
    {
        
        public void mouseWheelMoved(MouseWheelEvent e) {
           int notches = e.getWheelRotation();
           scale= scale+notches;
           if(scale<1)
            scale = 1;
           //System.out.println(notches);
           //moveObjects(notches);
           // add a offset in ref whenever I scale so it centers instead of skewing left
           System.out.println("notch and scale = "+notches+" "+scale);
        }
    }
    // allows for smooth traversing of the plane.
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
                //System.out.println("true?");
                 calculate();
                 /* 
                if(space==true)
                {
                    t2.start();
                    space = false;
                }
                else
                {
                    space = true;
                    t2.stop();
                }
                */
                
                
            }
            
        }
    }

}
