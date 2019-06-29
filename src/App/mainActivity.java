package App;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class mainActivity extends JPanel{
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
    static int yOffset = -40;


    private static void createAndShowGUI() throws IOException {
        JFrame frame = new JFrame("HelloWorldSwing");  //Create and set up the window.
        JComponent newContentPane = new mainActivity();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        mouse.initListener(frame);  //Mouse Listener Initialize
        key.initListener(frame);  //Keyboard Listener Initialize

        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();

        threads.executeFocus(frame);  //See "threads" class
    }
    public static class threads extends Thread{  //If mouse clicks away from JTextbox, it will unfocus JTextbox (for keyPressedListener)
        public static void executeFocus(JFrame frame){
            boolean unstoppable = true;
            Thread one = new Thread() {
                public void run() {
                    while(unstoppable){
                        if(mouseClicked) {
                            frame.requestFocus();
                            try {
                                Thread.sleep(100);
                            } catch(Exception e){
                                e.printStackTrace();
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
        jLabel.setBounds(0,0,900,900);

        JLabel l1 = new JLabel("Drive Options:");  //Labels
        l1.setFont(l1.getFont().deriveFont(15f));
        l1.setBounds(1000,50,200,30);
        l1.setFocusable(false);

        JLabel l2 = new JLabel("Motor Options:");
        l2.setFont(l1.getFont().deriveFont(15f));
        l2.setBounds(1250,50,200,30);
        l2.setFocusable(false);

        JComboBox jComboBox1 = new JComboBox();  //Dropdown box creator for Drive Options
        jComboBox1.addItem("[Select]");  //Dropdown options for Drive Options
        jComboBox1.addItem("Line");
        jComboBox1.addItem("Curve");
        jComboBox1.setFont(jComboBox1.getFont().deriveFont(15f));
        jComboBox1.addItemListener(new ItemChangeListener());
        jComboBox1.setBounds(950,100,200,30);
        jComboBox1.setFocusable(false);

        JComboBox jComboBox2 = new JComboBox();  //Dropdown box creator for Motor Options
        jComboBox2.addItem("[Select]");  //Dropdown options for Motor Options
        jComboBox2.setFont(jComboBox1.getFont().deriveFont(15f));
        jComboBox2.addItemListener(new ItemChangeListener2());
        jComboBox2.setBounds(1200,100,200,30);
        jComboBox2.setFocusable(false);

        JButton button = new JButton("Get Path Data");  //"Get Path Data" button
        button.setBounds(1075,200,200,50);
        button.setFocusable(false);

        JTextField tf = new JTextField();  //Output Text Field
        tf.setBounds(950,325,500,30);
        tf.setFont(tf.getFont().deriveFont(18f));
        tf.setFocusable(true);

        layeredPane.moveToBack(jLabel);

        draw.backgroundTransparent(true);  //Change settings of the Dot (circle)
        draw.setColor("Yellow");
        draw.newDimension(0,0,0,0);
        draw.visibility(false);

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

        layeredPane.add(l1, new Integer(8),0);  //Add all components to layeredPane and set overlap sequence
        layeredPane.add(l2, new Integer(7),0);
        layeredPane.add(jComboBox2, new Integer(6),0);
        layeredPane.add(draw.paintPanel,new Integer(5),0);
        layeredPane.add(jLabel, new Integer(4),0);
        layeredPane.add(jComboBox1, new Integer(3),0);
        layeredPane.add(button, new Integer(2), 0);
        layeredPane.add(tf, new Integer(1), 0);
        add(layeredPane);
    }

    static class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {  //Which dropdown option is selected for Drive Options?
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                if (String.valueOf(item).equals("Line")) {
                    System.out.println("Line");
                    line = true;
                    select = false;
                    curve = false;
                } else if (String.valueOf(item).equals("Curve")) {
                    System.out.println("Curve");
                    curve = true;
                    line = false;
                    select = false;
                } else if (String.valueOf(item).equals("[Select]")) {
                    System.out.println("Select");
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
        public void mouseClicked(MouseEvent evt){

        }
        public void mouseEntered(MouseEvent evt){  //Mouse entered window
            System.out.println("mouseExitedFalse");
            mouseExited = false;
        }
        public void mouseExited(MouseEvent evt){  //Mouse exited window
            System.out.println("mouseExitedTrue");
            mouseExited = true;
        }
        public void mousePressed(MouseEvent evt){
            mouseClicked = true;
        }
        public void mouseReleased(MouseEvent evt){
            mouseClicked = false;
        }
        public void mouseDragged(MouseEvent evt) {}
        public void mouseMoved(MouseEvent evt){  //Mouse is moved
            mouseX = evt.getXOnScreen();
            mouseY = evt.getYOnScreen();
            //System.out.println("mouseX = " + mouseX);
            //System.out.println("mouseY = " + mouseY);
        }
        public void actionPerformed(ActionEvent e) {}
    }

    public static class key implements KeyListener{  //Keyboard listener (which key is pressed?)
        public static void initListener(JFrame lp){
            lp.addKeyListener(new key());
        }
        @Override
        public void keyPressed(KeyEvent e)  //Key is pressed
        {
            if(e.getKeyCode()==KeyEvent.VK_N) {  //If N is pressed
                System.out.println("'N'");
                nPressed = true;
                gPressed = false;
                if(!mouseExited && mouseX+xOffset <= 875  && mouseY+yOffset <= 875 &&  //Checks if mouse is in the screen & in field image
                mouseX+xOffset >= 10 && mouseY+yOffset >= 10){
                    draw.visibility(true);
                    draw.setColor("yellow");
                    draw.newDimension(mouseX+xOffset,mouseY+yOffset,15,15);
                    draw.backgroundTransparent(false);
                    draw.redraw();
                }
            } else if(e.getKeyCode()==KeyEvent.VK_G){  //If G is pressed
                System.out.println("'G'");
                gPressed = true;
                nPressed = false;
            }
        }
        @Override
        public void keyReleased(KeyEvent e)  //Key is released
        {
            if(e.getKeyCode()==KeyEvent.VK_N) {  //If N is released
                nPressed = false;
                alreadyAdded = false;
                gPressed = false;
            } else if(e.getKeyCode()==KeyEvent.VK_G){  //If G is released
                gPressed = false;
                alreadyAdded = false;
                nPressed = false;
            }
        }
        @Override
        public void keyTyped(KeyEvent e)
        {
        }
    }

    public static void main(String[] args){
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
    public static class draw extends JPanel{  //Draw a dot
        static int wid = 0;
        static int hei = 0;
        static boolean white = false;       //Change color of dot (Not efficient at all LOL)
        static boolean lightgray = false;
        static boolean gray = false;
        static boolean darkgray = false;
        static boolean black = false;
        static boolean red = false;
        static boolean pink = false;
        static boolean orange = false;
        static boolean yellow = false;
        static boolean magenta = false;
        static boolean cyan = false;
        static boolean opaque = false;
        public static void newDimension(int x,int y,int width, int height){  //Change dimensions of circle
            paintPanel.setBounds(x,y,width,height);
            wid = width;
            hei = height;
        }
        public static void visibility(boolean visible){  //Change visibility of circle
            paintPanel.setVisible(visible);
        }
        public static void backgroundTransparent(boolean transparent){  //Change Opaque value
            if(transparent){
                opaque = false;
            } else {
                opaque = true;
            }
        }
        public static void redraw(){  //Repaints the dot
            paintPanel.repaint();
        }
        public static void putToFront(JLayeredPane lp){  //Move dot to front of layeredPane
            lp.moveToFront(paintPanel);
        }
        public static void putToBack(JLayeredPane lp){  //Move dot to back of layeredPane
            lp.moveToBack(paintPanel);
        }
        public static void setColor(String color){  //Set color (Once again not very efficient)
            if(color.equalsIgnoreCase("white")){
                white = true;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("light gray")){
                lightgray = true;
                white = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("gray")){
                gray = true;
                white = false;
                lightgray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("dark gray")){
                darkgray = true;
                white = false;
                lightgray = false;
                gray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("black")){
                black = true;
                white = false;
                lightgray = false;
                gray = false;
                darkgray = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("red")){
                red = true;
                white = false;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("pink")){
                pink = true;
                white = false;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("orange")){
                orange = true;
                white = false;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                yellow = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("yellow")){
                yellow = true;
                white = false;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                magenta = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("magenta")){
                magenta = true;
                white = false;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                cyan = false;
            } else if(color.equalsIgnoreCase("cyan")){
                cyan = true;
                white = false;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
            } else {
                System.out.println("Unknown/Unregistered Color");
            }
        }
        public static JPanel paintPanel = new JPanel() {  //Sets paintComponent as JPanel -> JPanel then set on layout
            @Override
            public void paintComponent(Graphics g) {  //Draws circle over JPanel
                super.paintComponent(g);
                Graphics2D g2ds = (Graphics2D) g;
                Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, wid, hei);
                paintPanel.setOpaque(opaque);
                if(white){  //Change color of circle
                    g2ds.setColor(Color.WHITE);
                } else if(lightgray){
                    g2ds.setColor(Color.LIGHT_GRAY);
                } else if(gray){
                    g2ds.setColor(Color.GRAY);
                } else if(darkgray){
                    g2ds.setColor(Color.DARK_GRAY);
                } else if(black){
                    g2ds.setColor(Color.BLACK);
                } else if(red){
                    g2ds.setColor(Color.RED);
                } else if(pink){
                    g2ds.setColor(Color.PINK);
                } else if(orange){
                    g2ds.setColor(Color.orange);
                } else if(yellow){
                    g2ds.setColor(Color.YELLOW);
                } else if(magenta){
                    g2ds.setColor(Color.MAGENTA);
                } else if(cyan){
                    g2ds.setColor(Color.CYAN);
                } else {
                    g2ds.setColor(Color.WHITE);
                }
                white = true;
                lightgray = false;
                gray = false;
                darkgray = false;
                black = false;
                red = false;
                pink = false;
                orange = false;
                yellow = false;
                magenta = false;
                cyan = false;
                g2ds.fill(circle);
                g2ds.draw(circle);
                System.out.println("circleDrawed");
            }
        };
    }
}
