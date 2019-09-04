package PreseasonTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;


public class helloWorld extends JComponent {
    static int flag = 0;
    static ArrayList<Integer> circles = new ArrayList<>();
    static ArrayList<Integer> lines = new ArrayList<>();
    private static long startTime;

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
        circles.add(400);
        circles.add(400);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLayeredPane layeredPane = new JLayeredPane();
        //setLayout(new LayeredPaneManager(layeredPane));
        setPreferredSize(new Dimension(700, 700));
        layeredPane.setOpaque(false);
        //layeredPane.setBounds(0,0,200,200);
        //layeredPane.setPreferredSize(new Dimension(300, 310));
        //Add the ubiquitous "Hello World" label.
        JButton button = new JButton("Hello World");
        button.setBounds(150, 150, 100, 100);
        button.setBackground(Color.GREEN);
        button.setOpaque(true);
        // layeredPane.add(button,  1, 0);

        JLabel label2 = new JLabel("Hello Earth");
        label2.setBounds(130, 130, 50, 30);
        label2.setBackground(Color.RED);
        label2.setOpaque(true);
        //layeredPane.add(label2, 2, 0);

        layeredPane.setBorder(BorderFactory.createTitledBorder("World on Top"));
        layeredPane.add(button, new Integer(1), 0);
        layeredPane.add(label2, new Integer(2), 0);
        showAllCircles.loop(layeredPane);
        init.curvedLines(layeredPane);
        add(layeredPane);
        button.addActionListener(new ActionListener() {  //Button onClickListener
            @Override
            public void actionPerformed(ActionEvent arg0) {
                drawLines.create(layeredPane);
            }
        });
    }

    public static class showAllCircles extends JPanel {
        public static JPanel paintPanel;
        static int x = 0;
        static int y = 0;

        public static void loop(JLayeredPane lp) {
            while (x < circles.size() / 2) {
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
                paintPanel.setBounds(circles.get(y), circles.get(y + 1), 10, 10);
                lp.add(paintPanel, new Integer(x + 4), 0);
                //System.out.println(lp.getComponent(0));
                x = x + 1;
                y = y + 2;
                if (circles.size() >= 4) {
                    while (lines.size() < 4) {
                        lines.add(null);
                    }
                    lines.set(0, circles.get(circles.size() - 4));
                    lines.set(1, circles.get(circles.size() - 3));
                    lines.set(2, circles.get(circles.size() - 2));
                    lines.set(3, circles.get(circles.size() - 1));
                    //drawLines.create(lp);
                }
            }
        }
    }

    public static void viewAllComponents(JLayeredPane lp) {
        int q = 0;
        while (lp.getComponentCount() > q) {
            System.out.println("searched component: " + lp.getComponent(q));
            q = q + 1;
        }
    }

    public static class drawLines extends JPanel {
        public static JPanel linePanel;

        public static void create(JLayeredPane lp) {
            linePanel = new JPanel() {  //Sets paintComponent as JPanel -> JPanel then set on layout
                @Override
                public void paintComponent(Graphics g) {  //Draws circle over JPanel
                    super.paintComponent(g);
                    linePanel.setOpaque(false);
                    linePanel.setVisible(true);
                    Graphics2D g2ds = (Graphics2D) g;
                    g2ds.setColor(Color.BLACK);
                    Line2D.Double line = new Line2D.Double(lines.get(0), lines.get(1),
                            lines.get(2), lines.get(3));
                    g2ds.fill(line);
                    g2ds.draw(line);
                }

            };
            linePanel.setOpaque(false);
            //linePanel.setBounds(0,0,2000,2000);
            if (circles.get(3) > circles.get(1)) {
                linePanel.setBounds(0, 0,
                        lines.get(2), lines.get(3));
            } else {
                linePanel.setBounds(0, 0,
                        lines.get(0), lines.get(1));
            }
            lp.add(linePanel, 20, 0);
            viewAllComponents(lp);

        }
    }

    public static class init extends JPanel {
        static int numTimes2Run2 = 0;
        static int loopflag2 = 0;
        static int lineInitVar2 = 0;
        static int midX = 0;
        static int midY = 0;
        static int index = 0;
        public static JPanel curvePanel;

        public static void curvedLines(JLayeredPane lp) {
            numTimes2Run2 = 0;
            loopflag2 = 0;
            lineInitVar2 = 0;
            midX = 0;
            midY = 0;
            index = 25;
            if (circles.size() / 2 == 0 || circles.size() / 2 == 1) {
                numTimes2Run2 = -100;
            } else {
                numTimes2Run2 = circles.size() / 2 - 1;
            }
            while (loopflag2 < numTimes2Run2) {
                try {
                    if (circles.get(lineInitVar2) > circles.get(lineInitVar2 + 2)) {
                        midX = (circles.get(lineInitVar2) - circles.get(lineInitVar2 + 2)) / 2 + circles.get(lineInitVar2 + 2);
                    } else if (circles.get(lineInitVar2 + 2) > circles.get(lineInitVar2)) {
                        midX = (circles.get(lineInitVar2 + 2) - circles.get(lineInitVar2)) / 2 + circles.get(lineInitVar2);
                    } else if (circles.get(lineInitVar2).equals(circles.get(lineInitVar2 + 2))) {
                        midX = circles.get(lineInitVar2);
                    }
                    if (circles.get(lineInitVar2 + 3) > circles.get(lineInitVar2 + 1)) {
                        midY = (circles.get(lineInitVar2 + 3) - circles.get(lineInitVar2 + 1)) / 2 + circles.get(lineInitVar2 + 1);
                    } else if (circles.get(lineInitVar2 + 1) > circles.get(lineInitVar2 + 3)) {
                        midY = (circles.get(lineInitVar2 + 1) - circles.get(lineInitVar2 + 3)) / 2 + circles.get(lineInitVar2 + 3);
                    } else if (circles.get(lineInitVar2 + 1).equals(circles.get(lineInitVar2 + 3))) {
                        midY = circles.get(lineInitVar2 + 1);
                    }
                } catch (Exception e) {
                }
                curvePanel = new JPanel() {  //Sets paintComponent as JPanel -> JPanel then set on layout
                    @Override
                    public void paint(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(Color.BLACK);
                        Path2D shape = new Path2D.Double();
                        curvePanel.setOpaque(false);
                        curvePanel.setVisible(true);
                        try {
                            shape.curveTo(circles.get(lineInitVar2), circles.get(lineInitVar2 + 1),
                                    midX, midY, circles.get(lineInitVar2 + 2), circles.get(lineInitVar2 + 3));
                            shape.closePath();
                            g2d.draw(shape);
                        } catch (Exception e) {
                        }
                    }
                };
                curvePanel.setOpaque(false);
                try {
                    System.out.println((circles.get(lineInitVar2)) + ", " + (circles.get(lineInitVar2 + 1)) + ", " +
                            midX + ", " + midY + ", " + (circles.get(lineInitVar2 + 2)) + ", " + (circles.get(lineInitVar2 + 3)));
                    System.out.println(loopflag2 + " < " + numTimes2Run2 + ", circles.get(" + lineInitVar2 + ", " + (lineInitVar2 + 1) +
                            ", " + (lineInitVar2 + 2) + ", " + (lineInitVar2 + 3) + ")");
                    if (circles.get(lineInitVar2 + 3) > circles.get(lineInitVar2 + 1)) {
                        curvePanel.setBounds(0, 0,
                                circles.get(lineInitVar2 + 2), circles.get(lineInitVar2 + 3));
                        lp.add(curvePanel, index, 0);
                        index = index + 1;
                    } else {
                        curvePanel.setBounds(0, 0,
                                circles.get(lineInitVar2), circles.get(lineInitVar2 + 1));
                        lp.add(curvePanel, index, 0);
                        index = index + 1;
                    }
                } catch (Exception e) {
                }
                //viewAllComponents(lp);
                loopflag2 = loopflag2 + 1;
                lineInitVar2 = lineInitVar2 + 2;
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
