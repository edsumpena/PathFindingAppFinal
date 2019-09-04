package App;

/*
 * List of current features:
 * N to create new dot
 * Click & Drag dot with mouse to move
 * Save Path File Output: .path compressed file containing .cir & .line text editor files
 * .cir & .line text files contain serialized "circles" & "lineSettingsAndParameters" arrays:
 * Example output for 2 circle + 1 line:
 *  -ArrayList "circles": {circle1XVal, circle1YVal, circle1Color, circle2XVal, circle2YVal, circle2Color}
 *  -ArrayList "lineSettingsAndParameters.get(0)": {line1Type}
 *  -ArrayList "lineSettingsAndParameters.get(1)": {lineX1, lineY1}
 *  -ArrayList "lineSettingsAndParameters.get(2)": {lineX2, lineY2}
 *  -ArrayList "lineSettingsAndParameters.get(3)": {lineX3, lineY3}
 */

import App.Converters.FromAndToPose2D;
import App.Debugger.cmdLine;
import App.ReadingAndWriting.MotorSetup;
import App.ReadingAndWriting.SerializeAndDeserialize;
import App.ReadingAndWriting.ZipAndUnzip;
import App.Wrappers.TrajBuilderWrapper;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

public class MainActivity extends JPanel {
    static boolean line = false;
    static boolean curve = false;
    static boolean select = true;
    static boolean strafe = false;
    static boolean reverse = false;
    static int mouseX = 0;
    static int mouseY = 0;
    static boolean nPressed = false;
    static boolean gPressed = false;
    static boolean alreadyAdded = false;
    static boolean mouseExited = true;
    static boolean mouseClicked = false;
    static boolean mouseClickedFocus = false;
    static int xOffset = -15;
    static int yOffset = -40;
    static ArrayList<Integer> circles = new ArrayList<>();
    static ArrayList<ArrayList<String>> lineSettingsAndParameters = new ArrayList<>();
    static ArrayList<String> settings = new ArrayList<>();
    static ArrayList<String> params1 = new ArrayList<>();
    static ArrayList<String> params2 = new ArrayList<>();
    static ArrayList<String> params3 = new ArrayList<>();
    static ArrayList<Integer> motorExecutedLocation = new ArrayList<>();
    static ArrayList<String> motorNames = new ArrayList<>();
    static String currentlySelected = "";
    static int z = 0;
    static int v = 0;
    static int index = 9;
    static int clearX = 0;
    static int clearY = 0;
    static int lineTypesLooped = 0;
    static File prevFilePath;
    static ObjectMapper objectMapper = new ObjectMapper();

    public static class threads extends Thread {    //Threads to house infinite loops
        static boolean unstoppable = true;

        public static void executeFocus(JFrame frame) {     //All JFrame related loops
            Thread one = new Thread() {
                public void run() {
                    while (unstoppable) {
                        if (mouseClickedFocus) {     //Mouse clicks away from JTextbox -> unfocus JTextbox (for keyPressedListener)
                            frame.requestFocus();
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            };
            one.start();
        }

        public static void executeRepaintAndClear(JLayeredPane lp, JLabel jl) {    //All JLayeredPane related loops
            Thread one = new Thread() {
                public void run() {
                    while (unstoppable) {
                        lp.moveToBack(jl);
                        if (draw.redrawCircle || draw.redrawLine || draw.redrawCurve) {    //Check if redraw() called--Lets me call elsewhere without JLayeredPane parameter
                            draw.showAllCirclesAndLines(lp);
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
                                        lp.getComponent(q).toString().contains(String.valueOf(clearY)) &&
                                        lp.getComponent(q).toString().contains("15x15")) {
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
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            };
            one.start();
        }
    }

    public MainActivity() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1300, 750));
        BufferedImage image = null;
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        name.add("WheelDcMotors");
        name.add("ArmDcMotor");
        name.add("SmallServoArm");
        types.add("DCWheel");
        types.add("DCArm");
        types.add("Servo");
        MotorSetup.exportMotors(name,types);
        try {
            image = ImageIO.read(new File("res/images/ruckus_field_lines.png"));  //Import FTC Field Image
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Image newImage = image.getScaledInstance(750, 750, java.awt.Image.SCALE_SMOOTH);  //Scale up the image in size
        ImageIcon imageIcon = new ImageIcon(newImage);
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        jLabel.setBounds(0, 0, 750, 750);

        JLabel l1 = new JLabel("Drive Options:");  //Labels
        l1.setFont(l1.getFont().deriveFont(15f));
        l1.setBounds(850, 50, 200, 30);
        l1.setFocusable(false);

        JLabel l2 = new JLabel("Motor Options:");
        l2.setFont(l1.getFont().deriveFont(15f));
        l2.setBounds(1100, 50, 200, 30);
        l2.setFocusable(false);

        JComboBox jComboBox1 = new JComboBox();  //Dropdown box creator for Drive Options
        jComboBox1.addItem("[Select]");  //Dropdown options for Drive Options
        jComboBox1.addItem("Line");
        jComboBox1.addItem("Curve");
        jComboBox1.addItem("Strafe");
        jComboBox1.addItem("Reverse");
        jComboBox1.addItem("Spline To");
        jComboBox1.setFont(jComboBox1.getFont().deriveFont(13f));
        jComboBox1.addItemListener(new ItemChangeListener());
        jComboBox1.setBounds(800, 100, 200, 30);
        jComboBox1.setFocusable(false);

        JComboBox jComboBox2 = new JComboBox();  //Dropdown box creator for Motor Options
        jComboBox2.addItem("[Select]");  //Dropdown options for Motor Options
        cmdLine.debugger.dispVar("motors",MotorSetup.importMotors().get(0),0,"N/A");
        if(!MotorSetup.importMotors().get(0).get(0).contains("Error")){
            int i = 0;
            while (MotorSetup.importMotors().size() >= i){
                jComboBox2.addItem(MotorSetup.importMotors().get(0).get(i) + " (" + MotorSetup.importMotors().get(1).get(i) + ")");
                i += 1;
            }
        }
        jComboBox2.setFont(jComboBox1.getFont().deriveFont(13f));
        jComboBox2.addItemListener(new ItemChangeListener2());
        jComboBox2.setBounds(1050, 100, 200, 30);
        jComboBox2.setFocusable(false);

        JButton button = new JButton("Get Path Data");  //"Get Path Data" button
        button.setBounds(925, 200, 200, 50);
        button.setFocusable(false);

        JButton saveButton = new JButton("Save Path");
        saveButton.setBounds(1100, 400, 100,35);
        saveButton.setFocusable(false);

        JButton openPathButton = new JButton("Open Path");
        openPathButton.setBounds(850, 400, 100,35);
        openPathButton.setFocusable(false);

        JTextField tf = new JTextField();  //Output Text Field
        tf.setBounds(775, 325, 500, 30);
        tf.setFont(tf.getFont().deriveFont(13f));
        tf.setFocusable(true);

        layeredPane.moveToBack(jLabel);

        button.addActionListener(new ActionListener() {  //Button onClickListener
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    TrajectoryBuilder driveTraj = TrajBuilderWrapper.getWheelTrajectoryBuilder(FromAndToPose2D.pointsToPose2d(circles,
                            0,1,3), motorNames);
                    String motors = objectMapper.writeValueAsString(motorNames);
                    String moveMotorLocation = objectMapper.writeValueAsString(motorExecutedLocation);
                    String trajectory = objectMapper.writeValueAsString(driveTraj);
                    String encodedTraj = Base64.getEncoder().encodeToString(trajectory.getBytes());
                    String encodedLoc = Base64.getEncoder().encodeToString(moveMotorLocation.getBytes());
                    String encodedMotors = Base64.getEncoder().encodeToString(motors.getBytes());
                    tf.setText("TRAJ:" + encodedTraj + ",MOTORS;" + encodedMotors + ",LOCATION-" + encodedLoc);
                }
                catch (Exception e){
                    System.err.println(e.toString());
                }
            }
        });
        openPathButton.addActionListener(new ActionListener() {  //Button onClickListener
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Open Path");
                fileChooser.setPreferredSize(new Dimension(800,600));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());  //remove the default file filter
                FileFilter filter = new FileNameExtensionFilter("PATH file", "path");
                fileChooser.addChoosableFileFilter(filter); //add PATH file filter

                if(prevFilePath != null){
                    fileChooser.setCurrentDirectory(prevFilePath);
                }
                int result = fileChooser.showOpenDialog(layeredPane);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File fileToRead = fileChooser.getSelectedFile();
                    System.out.println(fileToRead);
                    ZipAndUnzip.unzipFolder(String.valueOf(fileToRead), String.valueOf(fileChooser.getCurrentDirectory()));
                    String name = String.valueOf(fileToRead).substring(String.valueOf(fileToRead).lastIndexOf("\\") + 1,
                            String.valueOf(fileToRead).indexOf("."));
                    String cirDir = fileChooser.getCurrentDirectory() + "\\" + name + "Circles.cir";
                    String lineDir = fileChooser.getCurrentDirectory() + "\\" + name + "Traj.line";
                    circles = SerializeAndDeserialize.deserialize(cirDir,false);
                    lineSettingsAndParameters = SerializeAndDeserialize.deserialize(lineDir,true);
                    ZipAndUnzip.deleteAndOrRename(cirDir,"","",true,false);
                    ZipAndUnzip.deleteAndOrRename(lineDir,"","",true,false);
                    prevFilePath = fileChooser.getCurrentDirectory();
                    ArrayList<Integer> indexTempVals = new ArrayList<>();
                    indexTempVals.add(0);
                    indexTempVals.add(1);
                    indexTempVals.add(2);
                    indexTempVals.add(3);
                    indexTempVals.add(4);
                    indexTempVals.add(5);
                    cmdLine.debugger.dispVar("circles",circles,indexTempVals,-1000);
                    cmdLine.debugger.dispVar("lineSettingsAndParameters.get(0)",lineSettingsAndParameters.get(0),indexTempVals,"N/A");
                    cmdLine.debugger.dispVar("lineSettingsAndParameters.get(1)",lineSettingsAndParameters.get(1),indexTempVals,"N/A");
                    cmdLine.debugger.dispVar("lineSettingsAndParameters.get(2)",lineSettingsAndParameters.get(2),indexTempVals,"N/A");
                } else if (result == JFileChooser.CANCEL_OPTION) {
                    prevFilePath = fileChooser.getCurrentDirectory();
                } else if (result == JFileChooser.ERROR_OPTION) {
                    prevFilePath = fileChooser.getCurrentDirectory();
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {  //Button onClickListener
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser(){
                };
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());  //remove the default file filter
                FileFilter filter = new FileNameExtensionFilter("PATH file", "path");
                fileChooser.addChoosableFileFilter(filter); //add PATH file filter

                fileChooser.setDialogTitle("Save Path");
                fileChooser.setPreferredSize(new Dimension(800,600));
                fileChooser.setSelectedFile(new File("untitled.path"));
                if(prevFilePath != null){
                    fileChooser.setCurrentDirectory(prevFilePath);
                }
                String fileNoExt = "";
                int userSelection = fileChooser.showSaveDialog(layeredPane);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    if(!String.valueOf(f).contains(".path")){
                        int dialogButton = JOptionPane.ERROR_MESSAGE;
                        JOptionPane.showMessageDialog(null, "Error: File must contain '.path' extension","Error",dialogButton);
                    } else if(f.exists()){
                        int dialogButton2 = JOptionPane.YES_NO_OPTION;
                        int dialogResult = JOptionPane.showConfirmDialog (null,
                                "File Already Exists! Do you want to Replace?","File Exists",dialogButton2);
                        if(dialogResult == JOptionPane.YES_OPTION){
                            f.setExecutable(false);
                            f.setReadable(true);
                            f.setWritable(true);
                            f.delete();
                            File fileToSave = fileChooser.getSelectedFile();
                            fileChooser.setCurrentDirectory(fileChooser.getSelectedFile());
                            if (String.valueOf(fileToSave).contains(".path")) {
                                fileNoExt = String.valueOf(fileToSave).substring(String.valueOf(fileToSave).lastIndexOf("\\") + 1,
                                        String.valueOf(fileToSave).indexOf("."));
                            }
                            prevFilePath = fileChooser.getCurrentDirectory();
                            SerializeAndDeserialize.serialize(circles, lineSettingsAndParameters, String.valueOf(fileToSave),
                                    fileNoExt);
                            ZipAndUnzip.zipFolder(fileToSave.getAbsolutePath(),fileNoExt);
                        }
                    } else {
                        File fileToSave = fileChooser.getSelectedFile();
                        if (String.valueOf(fileToSave).contains(".path")) {
                            fileNoExt = String.valueOf(fileToSave).substring(String.valueOf(fileToSave).lastIndexOf("\\") + 1,
                                    String.valueOf(fileToSave).indexOf("."));
                        }
                        prevFilePath = fileChooser.getCurrentDirectory();
                        SerializeAndDeserialize.serialize(circles, lineSettingsAndParameters, String.valueOf(fileToSave),
                                fileNoExt);

                        ZipAndUnzip.zipFolder(fileToSave.getAbsolutePath(),fileNoExt);
                    }
                } else if (userSelection == JFileChooser.CANCEL_OPTION) {
                    prevFilePath = fileChooser.getCurrentDirectory();
                } else if (userSelection == JFileChooser.ERROR_OPTION) {
                    prevFilePath = fileChooser.getCurrentDirectory();
                }
            }
        });

        draw.backgroundTransparent(false);  //Change settings of the Dot (circle)
        draw.setColor("Yellow");
        draw.visibility(false);

        layeredPane.add(openPathButton, 10, 0);
        layeredPane.add(saveButton, 9, 0);
        layeredPane.add(l1, 8, 0);  //Add all components to layeredPane and set overlap sequence
        layeredPane.add(l2, 7, 0);
        layeredPane.add(jComboBox2, 6, 0);
        layeredPane.add(jLabel, 4, 0);
        layeredPane.add(jComboBox1, 3, 0);
        layeredPane.add(button, 2, 0);
        layeredPane.add(tf, 1, 0);

        draw.showAllCirclesAndLines(layeredPane);   //Draws invisible circle--Allows us to access + change paintComponent after runtime

        threads.executeRepaintAndClear(layeredPane, jLabel);    //See "threads" class
        circles.clear();    //Reset ArrayList of circles

        add(layeredPane);       //Put layeredPane in MainActivity()
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
                    strafe = false;
                    reverse = false;
                } else if (String.valueOf(item).equals("Curve")) {
                    curve = true;
                    line = false;
                    select = false;
                    strafe = false;
                    reverse = false;
                } else if (String.valueOf(item).equals("[Select]")) {
                    select = true;
                    line = false;
                    curve = false;
                    strafe = false;
                    reverse = false;
                } else if (String.valueOf(item).equals("Reverse")){
                    line = false;
                    select = false;
                    curve = false;
                    strafe = false;
                    reverse = true;
                } else if(String.valueOf(item).equals("Strafe")){
                    line = false;
                    select = false;
                    curve = false;
                    strafe = true;
                    reverse = false;
                }
            }
        }
    }

    static class ItemChangeListener2 implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {  //Which dropdown option is selected for Motor Options?
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                if (String.valueOf(item).equals("[Select]")) {
                    currentlySelected = "[Select]";
                }
                if(!MotorSetup.importMotors().get(0).get(0).contains("Error")) {
                    int i = 0;
                    while (MotorSetup.importMotors().get(0).size() >= i) {
                        if (MotorSetup.importMotors().get(0).get(i).equals(String.valueOf(item))){
                            currentlySelected = MotorSetup.importMotors().get(0).get(i);
                        }
                        i += 1;
                    }
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
            mouseClickedFocus = true;
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
            mouseClickedFocus = false;
            if (mouseClicked && mouseX + xOffset <= 735 && mouseY + yOffset <= 735 &&
                    mouseX + xOffset >= 0 && mouseY + yOffset >= 0) {
                clearX = circles.get(v) + xOffset;
                clearY = circles.get(v + 1) + yOffset;
                mouseClicked = false;
                circles.set(v, mouseX);
                circles.set(v + 1, mouseY);
                circles.remove(v + 2);
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
                if (!mouseExited && mouseX + xOffset <= 735 && mouseY + yOffset <= 735 &&
                        mouseX + xOffset >= 0 && mouseY + yOffset >= 0 && !select &&
                        !currentlySelected.contains("Selected")) {  //Checks if mouse is in the screen & in field image
                    motorNames.add(currentlySelected);
                    if(!currentlySelected.contains("DCWheel")){
                        motorExecutedLocation.add(circles.size() / 3);
                    }
                    circles.add(mouseX);
                    circles.add(mouseY);
                    draw.setDimension(15, 15);
                    draw.backgroundTransparent(true);
                    draw.visibility(true);
                    draw.setColor("red");
                    if (line) {
                        draw.setLineSetting("straight");
                    } else if (curve) {
                        draw.setLineSetting("curve");
                    }
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


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static class draw extends JPanel {  //Draw a dot
        static int wid = 0;
        static int hei = 0;
        static boolean opaque = true;
        public static JPanel paintPanel;
        public static JPanel linePanel;
        public static JPanel curvePanel;
        static boolean redrawCircle = false;
        static boolean redrawLine = false;
        static boolean redrawCurve = false;
        static int loopStopper = 0;
        static int componentChecker = 0;
        static boolean isVisible = false;
        static boolean clear = false;
        static int numOfIndexesRun = 0;
        static int numTimes2Run = 0;
        static int loopflag = 0;
        static int lineInitVar = 0;
        static int midX = 0;
        static int midY = 0;
        static int lineSetting = -100;
        static int numTimes2Run2 = 0;
        static int loopflag2 = 0;
        static int lineInitVar2 = 0;
        static ArrayList<Integer> xPoints = new ArrayList<>();
        static ArrayList<Integer> yPoints = new ArrayList<>();
        static ArrayList<Integer> xTemp = new ArrayList<>();
        static ArrayList<Integer> yTemp = new ArrayList<>();
        static ArrayList<Integer> getIndexes = new ArrayList<>();

        public static void setDimension(int width, int height) {        //Set width and height of circle
            wid = width;
            hei = height;
        }

        public static void clearOldCircle() {       //If circle is moved manually with mouse, old circle is cleared
            clear = true;
        }

        public static void setLineSetting(String setting) {
            if (setting.equalsIgnoreCase("straight")) {
                lineSetting = 0;
            } else if (setting.equalsIgnoreCase("curve")) {
                lineSetting = 1;
            } else {
                lineSetting = -100;
                System.out.println("Error: Setting '" + setting + "' does not exist.");
            }
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
            redrawCircle = true;
            if (lineSetting == 0) {
                redrawLine = true;
            } else if (lineSetting == 1) {
                redrawCurve = true;
            } else if (lineSetting == -100) {
                System.out.println("Error: Invalid Line Setting! (lineSetting index = " + lineSetting + ")");
            }
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

        public static void showAllCirclesAndLines(JLayeredPane lp) {
            loopStopper = 0;
            componentChecker = 0;
            numOfIndexesRun = 0;
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
                if (redrawCircle) {
                    paintPanel.repaint();
                    redrawCircle = false;
                }
                paintPanel.setOpaque(opaque);
                paintPanel.setBounds(circles.get(componentChecker) + xOffset, circles.get(componentChecker + 1)
                        + yOffset, wid, hei);           //Inits/draws all circles
                lp.add(paintPanel, index, 0);      //Sets object constraints, a value that determines layering
                lp.moveToFront(paintPanel);
                loopStopper = loopStopper + 1;
                componentChecker = componentChecker + 3;
                index = index + 1;
            }
            if (lineSetting == 0) {
                initStraightLines(lp);
            } else if (lineSetting == 1) {
                initCurvedLines(lp);
            } else if (lineSetting == -100) {
                System.out.println("Error: Invalid Line Setting! (lineSetting index = " + lineSetting + ")");
            }
        }

        public static void initStraightLines(JLayeredPane lp) {
            numTimes2Run = 0;
            loopflag = 0;
            lineInitVar = 0;
            if (circles.size() / 3 == 0 || circles.size() / 3 == 1) {
                numTimes2Run = -100;
            } else {
                numTimes2Run = circles.size() / 3 - 1;
            }
            while (loopflag < numTimes2Run) {
                linePanel = new JPanel() {  //Sets paintComponent as JPanel -> JPanel then set on layout
                    @Override
                    public void paintComponent(Graphics g) {  //Draws circle over JPanel
                        super.paintComponent(g);
                        linePanel.setOpaque(opaque);
                        linePanel.setVisible(isVisible);
                        Graphics2D g2ds = (Graphics2D) g;
                        g2ds.setColor(Color.BLACK);
                        try {
                            Line2D.Double line = new Line2D.Double(circles.get(lineInitVar) + xOffset,
                                    circles.get(lineInitVar + 1) + yOffset, circles.get(lineInitVar + 3) + xOffset,
                                    circles.get(lineInitVar + 4) + yOffset);
                            g2ds.fill(line);
                            g2ds.draw(line);
                        } catch (Exception e) {
                        }
                    }
                };
                linePanel.setOpaque(opaque);
                if (redrawLine) {
                    linePanel.repaint();
                    redrawLine = false;
                }
                //System.out.println(loopflag + " < " + numTimes2Run + ", circles.get(" + lineInitVar + ", " + (lineInitVar + 1) +
                //        ", " + (lineInitVar + 3) + ", " + (lineInitVar + 4) + ")");
                try {
                    if (lineSettingsAndParameters.get(0).get(loopflag).equals("straight")) {
                        //cmdLine.debugger.dispVar("lineSettingsAndParamters.get(0)", lineSettingsAndParameters.get(0), -1, "straight");
                        xTemp.clear();
                        xTemp.add(circles.get(lineInitVar) + xOffset);
                        xTemp.add(circles.get(lineInitVar + 3) + xOffset);
                        int panelXCord = Collections.max(xTemp);
                        yTemp.clear();
                        yTemp.add(circles.get(lineInitVar + 1) + yOffset);
                        yTemp.add(circles.get(lineInitVar + 4) + yOffset);
                        int panelYCord = Collections.max(yTemp);
                        cmdLine.debugger.dispVar("panelXCords", panelXCord);
                        cmdLine.debugger.dispVar("panelYCords", panelYCord);
                        curvePanel.setBounds(0, 0, panelXCord, panelYCord);
                        lp.add(curvePanel, index, 0);
                        index = index + 1;
                    }
                } catch (Exception e) {
                }
                //System.out.println((circles.size() / 3 - 2) + " == " + loopflag2 + ", " + settings.size() + " < " + (circles.size() / 3 - 1));
                if (circles.size() / 3 - 2 == loopflag && settings.size() < circles.size() / 3 - 1) {
                    settings.add("straight");
                    params1.add(String.valueOf(circles.get(circles.size() - 6)));
                    params1.add(String.valueOf(circles.get(circles.size() - 5)));
                    params2.add("N/A");
                    params2.add("N/A");
                    params3.add(String.valueOf(circles.get(circles.size() - 3)));
                    params3.add(String.valueOf(circles.get(circles.size() - 2)));
                }
                //cmdLine.debugger.viewAllComponents(lp);
                loopflag = loopflag + 1;
                lineInitVar = lineInitVar + 3;
            }
            lineSettingsAndParameters.clear();
            lineSettingsAndParameters.add(settings);
            lineSettingsAndParameters.add(params1);
            lineSettingsAndParameters.add(params2);
            lineSettingsAndParameters.add(params3);
            if (lineSettingsAndParameters.get(0).contains("curve")) {
                if (lineTypesLooped > 0) {
                    lineTypesLooped = 0;
                } else if (lineTypesLooped == 0) {
                    lineTypesLooped = lineTypesLooped + 1;
                    initCurvedLines(lp);
                }
            }
        }

        public static void initCurvedLines(JLayeredPane lp) {
            numTimes2Run2 = 0;
            loopflag2 = 0;
            lineInitVar2 = 0;
            midX = 0;
            midY = 0;
            lineSettingsAndParameters.clear();
            if (circles.size() / 3 == 0 || circles.size() / 3 == 1) {
                numTimes2Run2 = -100;
            } else {
                numTimes2Run2 = circles.size() / 3 - 1;
            }
            while (loopflag2 < numTimes2Run2) {
                try {
                    if (circles.get(lineInitVar2) > circles.get(lineInitVar2 + 3)) {
                        midX = (circles.get(lineInitVar2) - circles.get(lineInitVar2 + 3)) / 2 + circles.get(lineInitVar2 + 3);
                    } else if (circles.get(lineInitVar2 + 3) > circles.get(lineInitVar2)) {
                        midX = (circles.get(lineInitVar2 + 3) - circles.get(lineInitVar2)) / 2 + circles.get(lineInitVar2);
                    } else if (circles.get(lineInitVar2).equals(circles.get(lineInitVar2 + 3))) {
                        midX = circles.get(lineInitVar2);
                    }
                    if (circles.get(lineInitVar2 + 4) > circles.get(lineInitVar2 + 1)) {
                        midY = (circles.get(lineInitVar2 + 4) - circles.get(lineInitVar2 + 1)) / 2 + circles.get(lineInitVar2 + 1);
                    } else if (circles.get(lineInitVar2 + 1) > circles.get(lineInitVar2 + 4)) {
                        midY = (circles.get(lineInitVar2 + 1) - circles.get(lineInitVar2 + 4)) / 2 + circles.get(lineInitVar2 + 4);
                    } else if (circles.get(lineInitVar2 + 1).equals(circles.get(lineInitVar2 + 4))) {
                        midY = circles.get(lineInitVar2 + 1);
                    }
                    midX = midX + xOffset;
                    midY = midY + yOffset;
                    if (circles.size() / 3 - 2 == loopflag2 && xPoints.size() / 3 < circles.size() - 1) {
                        xPoints.add(circles.get(lineInitVar2) + xOffset);
                        xPoints.add(midX-50);
                        xPoints.add(circles.get(lineInitVar2 + 3) + xOffset);
                    }
                    if (circles.size() / 3 - 2 == loopflag2 && yPoints.size() / 3 < circles.size() - 1) {
                        yPoints.add(circles.get(lineInitVar2 + 1) + yOffset);
                        yPoints.add(midY-50);
                        yPoints.add(circles.get(lineInitVar2 + 4) + yOffset);
                    }
                } catch (Exception e) {
                }
                curvePanel = new JPanel() {  //Sets paintComponent as JPanel -> JPanel then set on layout
                    @Override
                    public void paint(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(Color.BLACK);
                        GeneralPath curvedLine = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.size());
                        curvePanel.setOpaque(opaque);
                        curvePanel.setVisible(isVisible);
                        try {
                            curvedLine.moveTo(xPoints.get(lineInitVar2), yPoints.get(lineInitVar2));
                            curvedLine.curveTo(xPoints.get(lineInitVar2), yPoints.get(lineInitVar2), xPoints.get(lineInitVar2 + 1),
                                    yPoints.get(lineInitVar2 + 1), xPoints.get(lineInitVar2 + 2), yPoints.get(lineInitVar2 + 2));
                            curvedLine.closePath();
                            g2d.draw(curvedLine);
                        } catch (Exception e) {
                        }
                    }
                };
                curvePanel.setOpaque(opaque);
                if (redrawCurve) {
                    curvePanel.repaint();
                    redrawCurve = false;
                }
                //System.out.println((circles.get(lineInitVar2) + xOffset) + ", " + (circles.get(lineInitVar2 + 1) + yOffset) + ", " +
                //        midX + ", " + midY + ", " + (circles.get(lineInitVar2 + 3) + xOffset) + ", " + (circles.get(lineInitVar2 + 4) + yOffset));
                getIndexes.clear();
                getIndexes.add(0);
                getIndexes.add(1);
                getIndexes.add(2);
                getIndexes.add(3);
                cmdLine.debugger.dispVar("settings",settings,getIndexes,"N/A");
                if (settings.size() < circles.size() / 3 && circles.size() / 3 >= 2 && loopflag2 == numTimes2Run2 - 1) {
                    //System.out.println(loopflag2 + " < " + numTimes2Run2 + ", " + settings.size() + " < " + (circles.size() / 3 - 1));
                    settings.add("curve");
                    params1.add(String.valueOf(xPoints.get(lineInitVar2)));
                    params1.add(String.valueOf(yPoints.get(lineInitVar2)));
                    params2.add(String.valueOf(xPoints.get(lineInitVar2 + 1)));
                    params2.add(String.valueOf(yPoints.get(lineInitVar2 + 1)));
                    params3.add(String.valueOf(xPoints.get(lineInitVar2 + 2)));
                    params3.add(String.valueOf(yPoints.get(lineInitVar2 + 2)));

                    try {
                        int zz = 0;
                        cmdLine.debugger.dispVar("params1",params1,0,"N/A");
                        while (params1.size() > zz) {
                            String loopCondition = lineSettingsAndParameters.get(1).contains(params1.get(zz)) + "&&" +
                                    lineSettingsAndParameters.get(2).contains(params2.get(zz)) + "&&" +
                                    lineSettingsAndParameters.get(3).contains(params3.get(zz));
                            cmdLine.debugger.conditionChecker(loopCondition,true);
                            if (lineSettingsAndParameters.get(1).contains(params1.get(zz)) &&
                                    lineSettingsAndParameters.get(2).contains(params2.get(zz)) &&
                                    lineSettingsAndParameters.get(3).contains(params3.get(zz))) {
                                lineSettingsAndParameters.get(1).remove(lineSettingsAndParameters.get(1).indexOf(params1.get(zz)));
                                lineSettingsAndParameters.get(2).remove(lineSettingsAndParameters.get(2).indexOf(params2.get(zz)));
                                lineSettingsAndParameters.get(3).remove(lineSettingsAndParameters.get(3).indexOf(params3.get(zz)));
                            }
                            zz = zz + 1;
                        }
                    } catch (Exception e){
                    }

                    lineSettingsAndParameters.add(settings);
                    lineSettingsAndParameters.add(params1);
                    lineSettingsAndParameters.add(params2);
                    lineSettingsAndParameters.add(params3);
                }
                try {
                    //System.out.println((circles.size() / 3 - 2) + " == " + loopflag2 + ", " + settings.size() + " < " + (circles.size() / 3 - 1));
                    if (lineSettingsAndParameters.get(0).get(loopflag2).equals("curve")) {
                        xTemp.clear();
                        xTemp.add(xPoints.get(lineInitVar2));
                        xTemp.add(xPoints.get(lineInitVar2 + 1));
                        xTemp.add(xPoints.get(lineInitVar2 + 2));
                        int panelXCord = Collections.max(xTemp);
                        yTemp.clear();
                        yTemp.add(yPoints.get(lineInitVar2));
                        yTemp.add(yPoints.get(lineInitVar2 + 1));
                        yTemp.add(yPoints.get(lineInitVar2 + 2));
                        int panelYCord = Collections.max(yTemp);
                        curvePanel.setBounds(0, 0, panelXCord, panelYCord);
                        lp.add(curvePanel, index, 0);
                        index = index + 1;
                    }
                } catch (Exception e) {
                }
                //cmdLine.debugger.viewAllComponents(lp);
                loopflag2 = loopflag2 + 1;
                lineInitVar2 = lineInitVar2 + 3;
            }
            try {
                if (lineSettingsAndParameters.get(0).contains("straight")) {
                    if (lineTypesLooped > 0) {
                        lineTypesLooped = 0;
                    } else if (lineTypesLooped == 0) {
                        lineTypesLooped = lineTypesLooped + 1;
                        initStraightLines(lp);
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
