package App;

/*
 * List of current features:
 * N to create new dot
 * Click & Drag dot with mouse to move
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class mainActivity extends JPanel {
    static boolean line = false;
    static boolean curve = false;
    static boolean select = true;
    static int mouseX = 0;
    static int mouseY = 0;
    static boolean nPressed = false;
    static boolean gPressed = false;
    static boolean alreadyAdded = false;
    static boolean mouseExited = true;
    static boolean mouseClicked = false;
    static int xOffset = -15;
    static int yOffset = -43;
    static ArrayList<Integer> circles = new ArrayList<>();
    static int z = 0;
    static int v = 0;
    static int index = 9;
    static int clearX = 0;
    static int clearY = 0;

    private static void createAndShowGUI() throws IOException {
        JFrame frame = new JFrame("HelloWorldSwing");  //Create and set up the window.
        JComponent newContentPane = new mainActivity();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        newContentPane.setOpaque(true);     //Makes background not transparent
        frame.setContentPane(newContentPane);

        mouse.initListener(frame);  //Mouse Listener Initialize
        key.initListener(frame);  //Keyboard Listener Initialize

        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();   //Sets JFrame as main window

        threads.executeFocus(frame);  //See "threads" class
    }

    public static class threads extends Thread {    //Threads to house infinite loops
        public static void executeFocus(JFrame frame) {     //All JFrame related loops
            boolean unstoppable = true;
            Thread one = new Thread() {
                public void run() {
                    while (unstoppable) {
                        if (mouseClicked) {     //Mouse clicks away from JTextbox -> unfocus JTextbox (for keyPressedListener)
                            frame.requestFocus();
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            one.start();
        }

        public static void executeRepaintAndClear(JLayeredPane lp) {    //All JLayeredPane related loops
            boolean unstoppable = true;
            Thread one = new Thread() {
                public void run() {
                    while (unstoppable) {
                        if (draw.redraw) {    //Check if redraw() called--Lets me call elsewhere without JLayeredPane parameter
                            draw.showAllCircles(lp);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (draw.clear) {     //Check if clearOldCircle() called--Lets me call elsewhere without JLayeredPane parameter
                            draw.clear = false;
                            int counter = 0;
                            int q = 0;
                            while (lp.getComponentCount() > q) {
                                //System.out.println("searched component: " + lp.getComponent(q));
                                //System.out.println("matcher Variables: " + clearX + ", " + clearY + " @ index#: " + q);
                                if (lp.getComponent(q).toString().contains(String.valueOf(clearX)) &&
                                        lp.getComponent(q).toString().contains(String.valueOf(clearY))) {
                                    lp.getComponent(q).setVisible(false);   //Finding correct component index for circle + clearing it
                                    lp.remove(q);                //Loop is necessary since component index is always changing + unknown
                                    lp.revalidate();
                                    draw.redraw();      //Calling repaint() method
                                    q = 0;
                                    counter = counter + 1;
                                    if (counter >= 10) {   //Deals with a bug where calling .remove() sometimes doesn't clear circle
                                        break;
                                    }
                                }
                                q = q + 1;
                            }
                        }
                    }
                }
            };
            one.start();
        }
    }

    public mainActivity() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1500, 900));
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/images/ftcOldField.jpg"));  //Import FTC Field Image
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Image newImage = image.getScaledInstance(900, 900, java.awt.Image.SCALE_SMOOTH);  //Scale up the image in size
        ImageIcon imageIcon = new ImageIcon(newImage);
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        jLabel.setBounds(0, 0, 900, 900);

        JLabel l1 = new JLabel("Drive Options:");  //Labels
        l1.setFont(l1.getFont().deriveFont(15f));
        l1.setBounds(1000, 50, 200, 30);
        l1.setFocusable(false);

        JLabel l2 = new JLabel("Motor Options:");
        l2.setFont(l1.getFont().deriveFont(15f));
        l2.setBounds(1250, 50, 200, 30);
        l2.setFocusable(false);

        JComboBox jComboBox1 = new JComboBox();  //Dropdown box creator for Drive Options
        jComboBox1.addItem("[Select]");  //Dropdown options for Drive Options
        jComboBox1.addItem("Line");
        jComboBox1.addItem("Curve");
        jComboBox1.setFont(jComboBox1.getFont().deriveFont(15f));
        jComboBox1.addItemListener(new ItemChangeListener());
        jComboBox1.setBounds(950, 100, 200, 30);
        jComboBox1.setFocusable(false);

        JComboBox jComboBox2 = new JComboBox();  //Dropdown box creator for Motor Options
        jComboBox2.addItem("[Select]");  //Dropdown options for Motor Options
        jComboBox2.setFont(jComboBox1.getFont().deriveFont(15f));
        jComboBox2.addItemListener(new ItemChangeListener2());
        jComboBox2.setBounds(1200, 100, 200, 30);
        jComboBox2.setFocusable(false);

        JButton button = new JButton("Get Path Data");  //"Get Path Data" button
        button.setBounds(1075, 200, 200, 50);
        button.setFocusable(false);

        JTextField tf = new JTextField();  //Output Text Field
        tf.setBounds(950, 325, 500, 30);
        tf.setFont(tf.getFont().deriveFont(18f));
        tf.setFocusable(true);

        layeredPane.moveToBack(jLabel);

        button.addActionListener(new ActionListener() {  //Button onClickListener
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (line) {
                    tf.setText("Line Selected");
                } else if (select) {
                    tf.setText("Please Select Mode");
                } else if (curve) {
                    tf.setText("Curve Selected");
                }
            }
        });

        draw.backgroundTransparent(false);  //Change settings of the Dot (circle)
        draw.setColor("Yellow");
        draw.visibility(false);

        layeredPane.add(l1, new Integer(8), 0);  //Add all components to layeredPane and set overlap sequence
        layeredPane.add(l2, new Integer(7), 0);
        layeredPane.add(jComboBox2, new Integer(6), 0);
        layeredPane.add(jLabel, new Integer(4), 0);
        layeredPane.add(jComboBox1, new Integer(3), 0);
        layeredPane.add(button, new Integer(2), 0);
        layeredPane.add(tf, new Integer(1), 0);

        draw.showAllCircles(layeredPane);   //Draws invisible circle--Allows us to access + change paintComponent after runtime

        threads.executeRepaintAndClear(layeredPane);    //See "threads" class
        circles.clear();    //Reset ArrayList of circles

        add(layeredPane);       //Put layeredPane in mainActivity()
    }

    static class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {  //Which dropdown option is selected for Drive Options?
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                if (String.valueOf(item).equals("Line")) {
                    line = true;
                    select = false;
                    curve = false;
                } else if (String.valueOf(item).equals("Curve")) {
                    curve = true;
                    line = false;
                    select = false;
                } else if (String.valueOf(item).equals("[Select]")) {
                    select = true;
                    line = false;
                    curve = false;
                }
            }
        }
    }

    static class ItemChangeListener2 implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {  //Which dropdown option is selected for Motor Options?
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                if (String.valueOf(item).equals("[Select")) {
                    System.out.println("Line");
                } else if (String.valueOf(item).equals("")) {
                    System.out.println("");
                } else if (String.valueOf(item).equals(".")) {
                    System.out.println("");
                }
            }
        }
    }

    public static class mouse implements MouseMotionListener, MouseListener {   //Detect mouse location on picture (to draw points)
        public static void initListener(JFrame lp) {
            lp.addMouseMotionListener(new mouse());
            lp.addMouseListener(new mouse());
        }

        public void mouseClicked(MouseEvent evt) {
        }

        public void mouseEntered(MouseEvent evt) {  //Mouse entered window
            mouseExited = false;
        }

        public void mouseExited(MouseEvent evt) {  //Mouse exited window
            mouseExited = true;
        }

        public void mousePressed(MouseEvent evt) {  //Mouse is pressed down -> Check if clicking on circle
            z = 0;
            v = 0;
            if (!circles.isEmpty()) {
                while (z < circles.size() / 3) {
                    if (circles.get(v) + 15 >= mouseX && circles.get(v) - 15 <= mouseX &&
                            circles.get(v + 1) + 15 >= mouseY && circles.get(v + 1) - 15 <= mouseY) {
                        mouseClicked = true;
                        break;
                    }
                    z = z + 1;
                    v = v + 3;
                }
            }
        }

        public void mouseReleased(MouseEvent evt) {   //Mouse released -> Draw circle at new location + remove old one
            if (mouseClicked && mouseX + xOffset <= 875 && mouseY + yOffset <= 875 &&   //Make sure mouse release on field image
                    mouseX + xOffset >= 12 && mouseY + yOffset >= 10) {
                clearX = circles.get(v) + xOffset;
                clearY = circles.get(v + 1) + yOffset;
                mouseClicked = false;
                circles.set(v, mouseX);
                circles.set(v + 1, mouseY);
                draw.setColor("Red");
                draw.clearOldCircle();
            }
            v = 0;
            z = 0;
        }

        public void mouseDragged(MouseEvent evt) {
            if (SwingUtilities.isLeftMouseButton(evt)) {  //Left click is held down and mouse is moved
                mouseX = evt.getX();
                mouseY = evt.getY();
                //System.out.println("mouseX = " + mouseX);
                //System.out.println("mouseY = " + mouseY);
            }
        }

        public void mouseMoved(MouseEvent evt) {  //Mouse is moved
            mouseX = evt.getX();
            mouseY = evt.getY();
            //System.out.println("mouseX = " + mouseX);
            //System.out.println("mouseY = " + mouseY);
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    public static class key implements KeyListener {  //Keyboard listener (which key is pressed?)
        public static void initListener(JFrame lp) {
            lp.addKeyListener(new key());
        }

        @Override
        public void keyPressed(KeyEvent e)  //Key is pressed
        {
            if (e.getKeyCode() == KeyEvent.VK_N) {  //If N is pressed
                nPressed = true;
                gPressed = false;
                if (!mouseExited && mouseX + xOffset <= 875 && mouseY + yOffset <= 875 &&
                        mouseX + xOffset >= 12 && mouseY + yOffset >= 10 && !select) {  //Checks if mouse is in the screen & in field image
                    circles.add(mouseX);
                    circles.add(mouseY);
                    draw.setDimension(15, 15);
                    draw.backgroundTransparent(true);
                    draw.visibility(true);
                    draw.setColor("red");
                    draw.redraw();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e)  //Key is released
        {
            if (e.getKeyCode() == KeyEvent.VK_N) {  //If N is released
                nPressed = false;
                alreadyAdded = false;
                gPressed = false;
            } else if (e.getKeyCode() == KeyEvent.VK_G) {  //If G is released
                gPressed = false;
                alreadyAdded = false;
                nPressed = false;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {  //Creating and showing this application's GUI
            public void run() {
                try {
                    createAndShowGUI();
                } catch (IOException ex) {
                    // handle exception...
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static class draw extends JPanel {  //Draw a dot
        static int wid = 0;
        static int hei = 0;
        static boolean opaque = true;
        public static JPanel paintPanel;
        static boolean redraw = false;
        static int loopStopper = 0;
        static int componentChecker = 0;
        static boolean isVisible = false;
        static boolean clear = false;

        public static void setDimension(int width, int height) {        //Set width and height of circle
            wid = width;
            hei = height;
        }

        public static void clearOldCircle() {       //If circle is moved manually with mouse, old circle is cleared
            clear = true;
        }

        public static void visibility(boolean visible) {  //Change visibility of circle
            isVisible = visible;
        }       //Changes circle's visibility

        public static void backgroundTransparent(boolean transparent) {  //Change Opaque value
            if (transparent) {
                opaque = false;
            } else {
                opaque = true;
            }
        }

        public static void redraw() {  //Repaints the dot
            redraw = true;
        }       //Allows paintComponent method to refresh after runtime

        public static void setColor(String color) {  //Adds color choice to ArrayList of circles (Once again not very efficient)
            if (color.equalsIgnoreCase("white")) {
                circles.add(0);
            } else if (color.equalsIgnoreCase("light gray")) {
                circles.add(1);
            } else if (color.equalsIgnoreCase("gray")) {
                circles.add(2);
            } else if (color.equalsIgnoreCase("dark gray")) {
                circles.add(3);
            } else if (color.equalsIgnoreCase("black")) {
                circles.add(4);
            } else if (color.equalsIgnoreCase("red")) {
                circles.add(5);
            } else if (color.equalsIgnoreCase("pink")) {
                circles.add(6);
            } else if (color.equalsIgnoreCase("orange")) {
                circles.add(7);
            } else if (color.equalsIgnoreCase("yellow")) {
                circles.add(8);
            } else if (color.equalsIgnoreCase("magenta")) {
                circles.add(9);
            } else if (color.equalsIgnoreCase("cyan")) {
                circles.add(10);
            }
        }

        public static void showAllCircles(JLayeredPane lp) {
            loopStopper = 0;
            componentChecker = 0;
            while (loopStopper < circles.size() / 3) {      //Loops until all circles in ArrayList have been drawn
                paintPanel = new JPanel() {  //Sets paintComponent as JPanel -> JPanel then set on layout
                    @Override
                    public void paintComponent(Graphics g) {  //Draws circle over JPanel
                        super.paintComponent(g);
                        paintPanel.setOpaque(opaque);
                        paintPanel.setVisible(isVisible);
                        Graphics2D g2ds = (Graphics2D) g;
                        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, wid, hei);     //Creates the circle
                        switch (circles.get(circles.size() - 1)) {      //Sets color
                            case (0):
                                g2ds.setColor(Color.WHITE);
                                break;
                            case (1):
                                g2ds.setColor(Color.LIGHT_GRAY);
                                break;
                            case (2):
                                g2ds.setColor(Color.GRAY);
                                break;
                            case (3):
                                g2ds.setColor(Color.DARK_GRAY);
                                break;
                            case (4):
                                g2ds.setColor(Color.BLACK);
                                break;
                            case (5):
                                g2ds.setColor(Color.RED);
                                break;
                            case (6):
                                g2ds.setColor(Color.PINK);
                                break;
                            case (7):
                                g2ds.setColor(Color.ORANGE);
                                break;
                            case (8):
                                g2ds.setColor(Color.YELLOW);
                                break;
                            case (9):
                                g2ds.setColor(Color.MAGENTA);
                                break;
                            case (10):
                                g2ds.setColor(Color.CYAN);
                                break;
                        }
                        g2ds.fill(circle);
                        g2ds.draw(circle);
                    }
                };
                if (redraw) {
                    paintPanel.repaint();
                    redraw = false;
                }
                paintPanel.setOpaque(opaque);
                paintPanel.setBounds(circles.get(componentChecker) + xOffset, circles.get(componentChecker + 1)
                        + yOffset, wid, hei);           //Inits/draws all circles
                lp.add(paintPanel, new Integer(index), 0);      //Sets object constraints, a value that determines layering
                loopStopper = loopStopper + 1;
                componentChecker = componentChecker + 3;
                index = index + 1;
            }
        }
    }
}
