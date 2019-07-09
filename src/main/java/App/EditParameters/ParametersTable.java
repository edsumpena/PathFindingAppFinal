package App.EditParameters;

import javax.script.Bindings;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ParametersTable {
    DefaultTableModel tableModel;
    JTable table;

    Object[] header = {
        "Type",
            "Name",
            "Description",
            "Value"
    };

    public ParametersTable(JTable table) {
        this.table = table;
        tableModel = new DefaultTableModel(header ,0);
        table.setModel(tableModel);
    }

    public void addTestData() {
        tableModel.addRow(
                new Parameter(ParameterType.DOUBLE, "Test", "Description", 3).getTableEntries()
        );
        table.invalidate();
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
