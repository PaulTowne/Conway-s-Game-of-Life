import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.JFrame;

public class Conway 
{
    // main class to run a chosen frame.
    public static void main(String [] args)
    {
        JFrame frame = new JFrame("Conway's Game of Life");
        frame.setSize(800,800);
        frame.setContentPane(new panelOptimize());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    }    
}
