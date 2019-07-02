package App.EditParameters;

import javax.swing.*;

public class EditParametersActivity {
    private JPanel panel;
    private JTable parametersTable;
    private JTextField name;
    private JTextArea textArea1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("EditParametersActivity");
        frame.setContentPane(new EditParametersActivity().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
