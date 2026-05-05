package com.techpark;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Tech-Park UQ - Cargando...");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("Tech-Park UQ");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}