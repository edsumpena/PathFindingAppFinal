package PreseasonTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

public class helloWorld extends JComponent {
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        helloWorld newContentPane = new helloWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newContentPane.setOpaque(false);
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }



    public helloWorld() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLayeredPane layeredPane = new JLayeredPane();
        //setLayout(new LayeredPaneManager(layeredPane));
        setPreferredSize(new Dimension(500,500));
        layeredPane.setOpaque(false);
        //layeredPane.setBounds(0,0,200,200);
        //layeredPane.setPreferredSize(new Dimension(300, 310));
        //Add the ubiquitous "Hello World" label.
        JButton button = new JButton("Hello World");
        button.setBounds(100,100,100,100);
        button.setBackground(Color.GREEN);
        button.setOpaque(true);
       // layeredPane.add(button,  1, 0);

        JLabel label2 = new JLabel("Hello Earth");
        label2.setBounds(100,100,50,30);
        label2.setBackground(Color.RED);
        label2.setOpaque(true);
        //layeredPane.add(label2, 2, 0);

        layeredPane.setBorder(BorderFactory.createTitledBorder("World on Top"));
        paintPanel.setBounds(300,300,100,100);
        layeredPane.add(paintPanel,new Integer(3),0);
        layeredPane.add(button,new Integer(1),0);
        layeredPane.add(label2,new Integer(2),0);
        add(layeredPane);
        button.addActionListener(new ActionListener() {  //Button onClickListener
            @Override
            public void actionPerformed(ActionEvent arg0) {
                paintPanel.repaint();
            }
        });
    }
    public JPanel paintPanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2ds = (Graphics2D) g;
            Ellipse2D.Double circle = new Ellipse2D.Double(0,0,100,100);
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
