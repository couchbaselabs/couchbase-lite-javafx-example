package com.couchbase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    CouchbaseSingleton couchbase;

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("STARTING THE APPLICATION...");
        Parent root = FXMLLoader.load(getClass().getResource("/TodoFX.fxml"));
        primaryStage.setTitle("Couchbase JavaFX Example");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
        this.couchbase = CouchbaseSingleton.getInstance();
        this.couchbase.startReplication(new URL("http://localhost:4984/fx-example/"), true);
    }

    @Override
    public void stop() {
        System.out.println("STOPING THE APPLICATION...");
        this.couchbase.stopReplication();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
