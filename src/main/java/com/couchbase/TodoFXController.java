package com.couchbase;

import com.couchbase.lite.*;
import com.couchbase.lite.Database.ChangeListener;
import com.couchbase.lite.replicator.Replication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

public class TodoFXController implements Initializable {

    private Manager manager;
    private Database database;
    private Replication pushReplication;
    private Replication pullReplication;

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
            this.manager = new Manager(new JavaContext("data"), Manager.DEFAULT_OPTIONS);
            this.database = this.manager.getDatabase("fx-project");
            this.pushReplication = this.database.createPushReplication(new URL("http://localhost:4984/fx-example/"));
            this.pullReplication = this.database.createPullReplication(new URL("http://localhost:4984/fx-example/"));
            this.pushReplication.setContinuous(true);
            this.pullReplication.setContinuous(true);
            this.pushReplication.start();
            this.pullReplication.start();
            View todoView = database.getView("todos");
            todoView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(document.get("_id"), document);
                }
            }, "1");
            Query query = todoView.createQuery();
            QueryEnumerator result = query.run();
            Document document = null;
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                document = row.getDocument();
                fxListView.getItems().add(new Todo(document.getId(), (String)document.getProperty("title"), (String)document.getProperty("description")));
            }
            this.database.addChangeListener(new ChangeListener() {
                @Override
                public void changed(Database.ChangeEvent event) {
                    for(int i = 0; i < event.getChanges().size(); i++) {
                        final Document retrievedDocument = database.getDocument(event.getChanges().get(i).getDocumentId());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //int documentIndex = fxListView.getItems().indexOf(new Todo(retrievedDocument.getId(), (String) retrievedDocument.getProperty("title"), (String) retrievedDocument.getProperty("description")));
                                int documentIndex = -1;
                                for(int j = 0; j < fxListView.getItems().size(); j++) {
                                    //System.out.println(((Todo)fxListView.getItems().get(j)).getDocumentId() + " to " + retrievedDocument.getId());
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
                    fxListView.getItems().add(save(new Todo(fxTitle.getText(), fxDescription.getText())));
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

    private Todo save(Todo todo) {
        String docId = "";
        Map<String, Object> properties = new HashMap<String, Object>();
        Document document = this.database.createDocument();
        properties.put("type", "todo");
        properties.put("title", todo.getTitle());
        properties.put("description", todo.getDescription());
        try {
            docId = document.putProperties(properties).getDocument().getId();
            todo.setDocumentId(docId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todo;
    }

}
