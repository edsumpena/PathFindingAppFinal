package App.EditParameters;

import javax.swing.event.ListSelectionEvent;
import java.util.Vector;

public class Controller {
    private EditParametersActivity view;



    public Controller(EditParametersActivity view) {
        this.view = view;
        initialize();
    }

    private void initialize() {
        view.getParametersTable().getSelectionModel().addListSelectionListener(this::selectionChanged);
    }

    public void selectionChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            // Selection changed



            view.getDescription().setText("");

        }
    }

}
