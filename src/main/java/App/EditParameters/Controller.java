package App.EditParameters;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

public class Controller {
    private EditParametersActivity view;
    private ParametersTable model;


    public Controller(EditParametersActivity view, ParametersTable model) {
        this.view = view;
        this.model = model;

        initialize();
        model.addTestData();
    }

    private void initialize() {
        model.getTable().getSelectionModel().addListSelectionListener(this::selectionChanged);
        view.getRefresh().addActionListener(this::refresh);
    }

    public void refresh(ActionEvent event) {
        List<Parameter> parameterList = ParameterScannerLocal.scan();

    }

    public void selectionChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            // Selection changed

            System.out.println("Selection changed");

            evt.getFirstIndex();
        }
    }



}
