package org.academiadecodigo.enuminatti.mafiagame.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.academiadecodigo.enuminatti.mafiagame.client.control.LoginController;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;

import java.io.IOException;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class ClientUI extends Application {

    private LoginController loginController;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage primaryStage) throws IOException{

        SceneNavigator.getInstance().setStage(primaryStage);
        primaryStage.setTitle("Mafia: The Game");

        SceneNavigator.getInstance().loadScreen("LoginScreen");
        loginController = SceneNavigator.getInstance().getController("LoginScreen");

    }

    @Override
    public void stop() throws Exception {
        loginController.shutdown();
    }
}
