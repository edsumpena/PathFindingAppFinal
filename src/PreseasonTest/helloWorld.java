package PreseasonTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import static PreseasonTest.helloWorld.showAllCircles.paintPanel;

public class helloWorld extends JComponent {
    static ArrayList<Integer> circles = new ArrayList<>();
    static ArrayList<Ellipse2D> circleList = new ArrayList<>();
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
        circles.add(100);
        circles.add(100);
        circles.add(200);
        circles.add(200);
        circles.add(300);
        circles.add(300);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLayeredPane layeredPane = new JLayeredPane();
        //setLayout(new LayeredPaneManager(layeredPane));
        setPreferredSize(new Dimension(700,700));
        layeredPane.setOpaque(false);
        //layeredPane.setBounds(0,0,200,200);
        //layeredPane.setPreferredSize(new Dimension(300, 310));
        //Add the ubiquitous "Hello World" label.
        JButton button = new JButton("Hello World");
        button.setBounds(150,150,100,100);
        button.setBackground(Color.GREEN);
        button.setOpaque(true);
       // layeredPane.add(button,  1, 0);

        JLabel label2 = new JLabel("Hello Earth");
        label2.setBounds(130,130,50,30);
        label2.setBackground(Color.RED);
        label2.setOpaque(true);
        //layeredPane.add(label2, 2, 0);

        layeredPane.setBorder(BorderFactory.createTitledBorder("World on Top"));
        layeredPane.add(button,new Integer(1),0);
        layeredPane.add(label2,new Integer(2),0);
        showAllCircles.loop(layeredPane);
        add(layeredPane);
        button.addActionListener(new ActionListener() {  //Button onClickListener
            @Override
            public void actionPerformed(ActionEvent arg0) {
                paintPanel.repaint();
            }
        });
    }
    public static class showAllCircles extends JPanel {
        public static JPanel paintPanel;
        static int x = 0;
        static int y = 0;
        public static void loop(JLayeredPane lp) {
        while(x < circles.size()/2) {
            paintPanel = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2ds = (Graphics2D) g;
                    Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 10, 10);
                    paintPanel.setOpaque(true);
                    g2ds.setColor(Color.BLUE);
                    g2ds.fill(circle);
                    g2ds.draw(circle);
                    System.out.println("circleDrawed");
                }
            };
            paintPanel.setBounds(circles.get(y),circles.get(y+1),10,10);
            lp.add(paintPanel,new Integer(x+4),0);
            x = x + 1;
            y = y + 2;
        }
    }
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
