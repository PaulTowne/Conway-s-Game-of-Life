import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

import javax.swing.JFrame;

public class Conway 
{
    public static void main(String [] args)
    {
        JFrame frame = new JFrame("Conway's Game of Life");
        frame.setSize(800,800);
        frame.setContentPane(new panelOptimize());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    }    
}
