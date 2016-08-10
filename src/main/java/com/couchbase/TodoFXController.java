package com.couchbase;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class TodoFXController implements Initializable {

    @FXML
    private TextField fxTitle;

    @FXML
    private TextArea fxDescription;

    @FXML
    private ListView fxListView;

    @FXML
    private Button fxSave;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        fxListView.setCellFactory(new Callback<ListView<Todo>, ListCell<Todo>>(){
            @Override
            public ListCell<Todo> call(ListView<Todo> p) {
                ListCell<Todo> cell = new ListCell<Todo>(){
                    @Override
                    protected void updateItem(Todo t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getTitle());
                        }
                    }
                };
                return cell;
            }
        });
        fxSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(!fxTitle.getText().equals("") && !fxDescription.getText().equals("")) {
                    fxListView.getItems().add(new Todo(fxTitle.getText(), fxDescription.getText()));
                    fxTitle.setText("");
                    fxDescription.setText("");
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Missing Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Both a title and description are required for this example.");
                    alert.showAndWait();
                }
            }
        });
    }

}
