package PreseasonTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;

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
        layeredPane.setBounds(0,0,200,200);
        layeredPane.setPreferredSize(new Dimension(300, 310));

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        label.setBounds(0,0,10,10);
        label.setOpaque(true);
        layeredPane.add(label,  1, 0);

        JLabel label2 = new JLabel("Hello Earth");
        label2.setBounds(0,0,10,10);
        label.setOpaque(true);
        layeredPane.add(label2, 2, 0);

        //layeredPane.setBorder(BorderFactory.createTitledBorder("World on Top"));
        layeredPane.add(paintPanel,3,0);
        add(paintPanel);
        //add(label);
        //add(label2);
        add(Box.createRigidArea(new Dimension(0, 10)));
        layeredPane.moveToFront(paintPanel);
        layeredPane.setLayer(paintPanel,1,1);
        add(layeredPane);
    }
    public static JPanel paintPanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2ds = (Graphics2D) g;
            Ellipse2D.Double circle = new Ellipse2D.Double(100,100,10,10);
            paintPanel.setOpaque(true);
            g2ds.setColor(Color.BLUE);
            g2ds.fill(circle);
            g2ds.draw(circle);
            System.out.println("circleDrawed");
        }
    };
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
