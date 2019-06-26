package App;

import PreseasonTest.helloWorld;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import static sun.plugin.javascript.navig.JSType.Embed;
public class mainActivity extends JPanel{
    private JComboBox comboBox1;
    static boolean line = false;
    static boolean curve = false;
    static boolean select = true;
    static double mouseX = 0;
    static double mouseY = 0;
    static boolean nPressed = false;
    static boolean gPressed = false;
    static boolean alreadyAdded = false;
    static boolean mouseExited = true;
    static boolean mouseClicked = false;
    //private static JLabel mimage;


    private static void createAndShowGUI() throws IOException {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        JComponent newContentPane = new mainActivity();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        mouse.initListener(frame);  //Mouse Listener Initialize
        key.initListener(frame);  //Keyboard Listener Initialize

        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();

        threads.execute(frame);
    }
    public static class threads extends Thread{
        public static void execute(JFrame lp){
            int unstoppable = 1;
            Thread one = new Thread() {
                public void run() {
                    while(unstoppable == 1){
                        if(mouseClicked) {
                            lp.requestFocus();
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
        layeredPane.setPreferredSize(new Dimension(900, 900));
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
        /*jLabel.setIcon(imageIcon);
        jLabel.setLayout(new GridLayout(1, 2));
        layeredPane.add(jLabel,1,0);*/
        //frame.getContentPane().add(jLabel, BorderLayout.CENTER);


        JComboBox jComboBox1 = new JComboBox();  //Dropdown box creator
        jComboBox1.addItem("[Select]");  //Dropdown options
        jComboBox1.addItem("Line");
        jComboBox1.addItem("Curve");
        jComboBox1.setFont(jComboBox1.getFont().deriveFont(15f));
        //Object cmboitem = jComboBox1.getSelectedItem();
        //System.out.println(cmboitem);
        jComboBox1.addItemListener(new ItemChangeListener());
        jComboBox1.setPreferredSize(new Dimension(200, 30));
        jComboBox1.setFocusable(false);
        layeredPane.add(jComboBox1,2,0);
        //frame.getContentPane().add(jComboBox1, BorderLayout.BEFORE_FIRST_LINE);

        JButton button = new JButton("Get Path Data");  //"Get Path Data" button
        button.setPreferredSize(new Dimension(115, 20));
        button.setFocusable(false);
        layeredPane.add(button, 3,0);
        //frame.getContentPane().add(button, BorderLayout.WEST);

        JTextField tf = new JTextField();  //Output Text Field
        tf.setPreferredSize(new Dimension(100, 30));
        tf.setFont(tf.getFont().deriveFont(18f));
        tf.setFocusable(true);
        layeredPane.add(button,4,0);
        //frame.getContentPane().add(tf, BorderLayout.SOUTH);

        draw.backgroundTransparent(true);
        draw.circle(layeredPane);

        layeredPane.moveToBack(jLabel);
        draw.putToFront(layeredPane);

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

        add(jLabel);
        add(jComboBox1);
        add(button);
        add(tf);
        layeredPane.moveToBack(jLabel);
        add(layeredPane);
    }

    static class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {  //Which dropdown option is selected?
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

    public static class mouse implements MouseMotionListener, MouseListener {   //Detect mouse location on picture (to draw points)
        public static void initListener(JFrame lp) {
            lp.addMouseMotionListener(new mouse());
            lp.addMouseListener(new mouse());
        }
        public static void resetFocus(JTextField tf){
            tf.setFocusable(false);
            tf.setFocusable(true);
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
            if(e.getKeyCode()==KeyEvent.VK_N) {
                System.out.println("'N'");
                nPressed = true;
                gPressed = false;
                if(!mouseExited){
                    draw.newDimension(15,15);
                    draw.backgroundTransparent(false);
                    draw.redraw();
                }
            } else if(e.getKeyCode()==KeyEvent.VK_G){
                System.out.println("'G'");
                gPressed = true;
                nPressed = false;
            }
        }
        @Override
        public void keyReleased(KeyEvent e)  //Key is released
        {
            if(e.getKeyCode()==KeyEvent.VK_N) {
                nPressed = false;
                alreadyAdded = false;
                gPressed = false;
            } else if(e.getKeyCode()==KeyEvent.VK_G){
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
        static boolean opaque = false;
        public static void circle(JLayeredPane lp){
            System.out.println("drawCircle");
            lp.add(paintPanel,5,0);
            //frame.getContentPane().add(paintPanel, BorderLayout.CENTER);
        }
        public static void newDimension(int width, int height){
            wid = width;
            hei = height;
        }
        public static void backgroundTransparent(boolean transparent){
            if(transparent){
                opaque = false;
            } else {
                opaque = true;
            }
        }
        public static void redraw(){
            paintPanel.repaint();
        }
        public static void putToFront(JLayeredPane lp){
            lp.moveToFront(paintPanel);
        }
        public static void putToBack(JLayeredPane lp){
            lp.moveToBack(paintPanel);
        }
        static final JPanel paintPanel = new JPanel(){
        @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2ds = (Graphics2D) g;
                Ellipse2D.Double circle = new Ellipse2D.Double(mouseX - 120, mouseY - 60, wid, hei);
                paintPanel.setOpaque(opaque);
                g2ds.setColor(Color.YELLOW);
                g2ds.fill(circle);
                g2ds.draw(circle);
            }
        };
    }

   /* @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //Shape line = new Line2D.Double(3, 3, 303, 303);
        Shape circle = new Ellipse2D.Double(mouseX, mouseY, 20, 20);
        g2.setColor(Color.BLACK);
        g2.draw(circle);
    }*/
}
