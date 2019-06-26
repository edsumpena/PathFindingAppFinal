package PreseasonTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class helloWorld extends JComponent {
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        JComponent newContentPane = new helloWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }



    public helloWorld() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLayeredPane layeredPane = new JLayeredPane();
        //layeredPane.setPreferredSize(new Dimension(300, 310));

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        label.setBounds(100,100,100,100);
        label.setOpaque(true);
        layeredPane.add(label,  1, 0);

        JLabel label2 = new JLabel("Hello Earth");
        label2.setBounds(100,100,100,100);
        label.setOpaque(true);
        layeredPane.add(label2, 2, 0);

        layeredPane.setBorder(BorderFactory.createTitledBorder(
                "World on Top"));
        add(label);
        add(label2);
        layeredPane.moveToFront(label);
        add(layeredPane);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
