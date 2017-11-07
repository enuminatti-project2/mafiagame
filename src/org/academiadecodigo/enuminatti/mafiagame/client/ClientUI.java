package org.academiadecodigo.enuminatti.mafiagame.client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class ClientUI extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException{

        Parent root = FXMLLoader.load(getClass().getResource("view/ClientView.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Mafia: The Game");
        primaryStage.show();
    }
}
