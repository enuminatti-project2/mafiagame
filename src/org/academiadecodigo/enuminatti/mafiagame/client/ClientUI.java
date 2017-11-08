package org.academiadecodigo.enuminatti.mafiagame.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.academiadecodigo.enuminatti.mafiagame.client.control.ChatController;

import java.io.IOException;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class ClientUI extends Application {
    private FXMLLoader loader;
    private ChatController chatController;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        loader = new FXMLLoader(getClass().getResource("view/ClientView.fxml"));
        loader.load();

        chatController = loader.getController();

    }

    @Override
    public void start(Stage primaryStage) throws IOException{

        primaryStage.setScene(new Scene(loader.getRoot()));
        primaryStage.setTitle("Mafia: The Game");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> chatController.shutdown());

    }
}
