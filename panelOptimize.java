
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;
import java.util.List;

// This class is a copy of the panel2 class with a slight change in the calculate function. This program runs slightly faster than the original panel file.
// I changed how I grouped the cells together and how I deleted or created cells. I tried to eliminate places where I was unnecessarily transferring objects from one place to another.


public class panelOptimize extends JPanel
{
    boolean w; 
    boolean a;
    boolean d;
    boolean s;
    boolean space=false;
    int accelerate = 0;

    ArrayList<AliveCell> matrix = new ArrayList<AliveCell>();
    ArrayList<DeadCell> deadCells = new ArrayList<DeadCell>();
    ArrayList<AliveCell> copy = new ArrayList<AliveCell>();
    ArrayList<AliveCell> nowAlive = new ArrayList<AliveCell>();
    ArrayList<ArrayList<AliveCell>> range = new ArrayList<ArrayList<AliveCell>>();

    int scale =20;
    Timer t;
    Timer t2;
    Reference ref = new Reference();
    Random rand = new Random();

    public panelOptimize()
    {
        addKeyListener(new KeyEventDemo());
        t = new Timer(10, new TimeListener());
        t2 = new Timer(10, new TimeListener2());
        addMouseWheelListener(new MouseWheel());
        addMouseListener(new MouseClick());
        setFocusable(true);
        t.start();
        t2.start();

        
        
    }
    void calculate() // took 1:14 to complete "t" test
    {
        
        range.clear();
        int loop = 0;
        double variabledist = 2;
        boolean added = false;
        while(!matrix.isEmpty())
        {
            variabledist = 2;
            rand = new Random();
            int randomChoice = rand.nextInt(matrix.size()); 
            range.add(new ArrayList<AliveCell>());
            range.get(loop).add(matrix.get(randomChoice));
            matrix.remove(randomChoice);
            do{
                 added = false;
                for(int j=0;j<matrix.size();j++)
                {
                    int objx = range.get(loop).get(0).getX() - matrix.get(j).getX();
                    int objy = range.get(loop).get(0).getY() - matrix.get(j).getY();
                    double dis = Math.sqrt(Math.pow(objx,2)+Math.pow(objy,2));
                    if(dis<=variabledist)  
                    {
                        range.get(loop).add(matrix.get(j));
                        matrix.remove(matrix.get(j));
                        j--;
                        added = true;
                    }
                }
                if(added)
                    variabledist= variabledist+2;

            }while(added);
            
            loop++;
        }
        int neighbor=0;
        for(int i=0;i<range.size();i++)
        {
            for(int j=0;j<range.get(i).size();j++)
            {
                AliveCell alive = range.get(i).get(j);
                neighbor=0;
                for(int k=0;k<range.get(i).size();k++)
                {
                    if(k!=j)
                    {
                        int x = alive.getX()-range.get(i).get(k).getX();
                        int y = alive.getY()-range.get(i).get(k).getY();
                        if(Math.abs(x)<=1 && Math.abs(y)<=1)
                        {
                            neighbor++;
                        }
                    }
                }
                if(neighbor<2)
                    alive.alive(false);
                else if(neighbor>3)
                    alive.alive(false);
                else if(true)
                {
                    alive.alive(true);
                }
            }
        }
        nowAlive.clear();
        ArrayList<AliveCell> temp = new ArrayList<AliveCell>();
        for(int i=0;i<range.size();i++)
        {
            temp.clear();
            for(int k=0;k<range.get(i).size();k++)
            {
                AliveCell val = range.get(i).get(k);
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
                        if(temp.get(o).getX()==range.get(i).get(l).getX() && temp.get(o).getY() == range.get(i).get(l).getY())
                        {
                            temp.remove(o);
                            o--;
                        }
                    }
                }
                
            }
            int dup = 0;
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
                }
                if(dup==2)
                {
                    nowAlive.add(al);
                }
                temp.remove(j);
                j--;
            }
        }
        range.stream()
        .flatMap(List::stream)
        .forEach(matrix::add);
        
        for(int i=0;i<matrix.size();i++)
        {
            if(matrix.get(i).isAlive()==false)
            {
                matrix.remove(i);
                i--;
            }
        }
        // adding nowalive
        matrix.addAll(nowAlive);
        
    }
    public boolean nowAlive(ArrayList<AliveCell> arr, DeadCell c)
    {
        return false;
    }
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
        for(int i=0;i<matrix.size();i++)
        {
            matrix.get(i).draw(g, scale,ref);
        }
        
    }
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
            calculate();
        }
    }
    public class MouseClick extends MouseInputAdapter
    {
        public void mousePressed(MouseEvent me)
        {
            int x = me.getX();
            int y = me.getY();
            x = x - ref.getX();
            y = y -ref.getY();
            while(x%scale !=0)
            {
                x--;
            }
            while(y%scale !=0)
            {
                y--;  
            }
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
               ref.setX(-20);
               ref.setY(-20);
           }
        }
    }
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
                 if(space ==true)
                    space = false;
                 else   
                    space = true;
                
                if(space)
                    t2.stop();
                else 
                    t2.start();
            }
            if(e.getKeyCode() == KeyEvent.VK_T)
            {
                for(int i=0;i<40;i++)
                {
                    for(int j=0;j<40;j++)
                    {
                        matrix.add(new AliveCell(i, j));
                    }
                }

                for(int i=0;i<20;i++)
                {
                    for(int j=0;j<20;j++)
                    {
                        matrix.add(new AliveCell(i+200,j));
                    }
                }
            }
            
        }
    }

}
