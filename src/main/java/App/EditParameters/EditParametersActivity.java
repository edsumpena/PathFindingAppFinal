package App.EditParameters;

import javax.swing.*;

public class EditParametersActivity {
    private JPanel panel;
    private JTable parametersTable;
    private JTextField name;
    private JTextArea description;

    // region accessors
    public JPanel getPanel() {
        return panel;
    }

    public JTable getParametersTable() {
        return parametersTable;
    }

    public JTextField getName() {
        return name;
    }

    public JTextArea getDescription() {
        return description;
    }
    // endregion

    public EditParametersActivity() {
        parametersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("EditParametersActivity");
        frame.setContentPane(new EditParametersActivity().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
