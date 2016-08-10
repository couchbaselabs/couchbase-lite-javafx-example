package com.couchbase;

import com.couchbase.lite.*;
import com.couchbase.lite.Database.ChangeListener;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

public class TodoFXController implements Initializable {

    private CouchbaseSingleton couchbase;

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
        try {
            this.couchbase = CouchbaseSingleton.getInstance();
            fxListView.getItems().addAll(this.couchbase.query());
            this.couchbase.getDatabase().addChangeListener(new ChangeListener() {
                @Override
                public void changed(Database.ChangeEvent event) {
                    for(int i = 0; i < event.getChanges().size(); i++) {
                        final Document retrievedDocument = couchbase.getDatabase().getDocument(event.getChanges().get(i).getDocumentId());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                int documentIndex = indexOfByDocumentId(retrievedDocument.getId(), fxListView.getItems());
                                for(int j = 0; j < fxListView.getItems().size(); j++) {
                                    if(((Todo)fxListView.getItems().get(j)).getDocumentId().equals(retrievedDocument.getId())) {
                                        documentIndex = j;
                                        break;
                                    }
                                }
                                if (retrievedDocument.isDeleted()) {
                                    if (documentIndex > -1) {
                                        fxListView.getItems().remove(documentIndex);
                                    }
                                } else {
                                    if (documentIndex == -1) {
                                        fxListView.getItems().add(new Todo(retrievedDocument.getId(), (String) retrievedDocument.getProperty("title"), (String) retrievedDocument.getProperty("description")));
                                    } else {
                                        fxListView.getItems().remove(documentIndex);
                                        fxListView.getItems().add(new Todo(retrievedDocument.getId(), (String) retrievedDocument.getProperty("title"), (String) retrievedDocument.getProperty("description")));
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    fxListView.getItems().add(couchbase.save(new Todo(fxTitle.getText(), fxDescription.getText())));
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

    private int indexOfByDocumentId(String needle, ObservableList<Todo> haystack) {
        int result = -1;
        for(int i = 0; i < haystack.size(); i++) {
            if(haystack.get(i).getDocumentId().equals(needle)) {
                result = i;
                break;
            }
        }
        return result;
    }

}
