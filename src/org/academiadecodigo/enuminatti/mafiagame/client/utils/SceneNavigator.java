package org.academiadecodigo.enuminatti.mafiagame.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.academiadecodigo.enuminatti.mafiagame.client.control.Controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class SceneNavigator {
    private static final String VIEW_PATH = "/org/academiadecodigo/enuminatti/mafiagame/client/view";
    private final int MIN_WIDTH = 1024; // window width
    private final int MIN_HEIGHT = 768; // window height

    private LinkedList<Scene> scenes;
    private Map<String, Controller> controllers;


    private Stage stage; // reference to the application window

    private static SceneNavigator sceneNavigator;

    public static SceneNavigator getInstance() {
        if (sceneNavigator == null) {
            synchronized (SceneNavigator.class) {
                if (sceneNavigator == null) {
                    sceneNavigator = new SceneNavigator();
                }
            }
        }

        return sceneNavigator;
    }

    private SceneNavigator() {
        scenes = new LinkedList<>();
        controllers = new LinkedHashMap<>();
    }

    public void loadScreen(String view) {
        try {

            // Instantiate the view and the controller
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource(VIEW_PATH + "/" + view + ".fxml"));
            Parent root = fxmlLoader.load();

            //Store the controller
            controllers.put(view, fxmlLoader.getController());

            // Create a new scene and add it to the stack
            Scene scene = new Scene(root, MIN_WIDTH, MIN_HEIGHT);
            scenes.push(scene);

            // Put the scene on the stage
            setScene(scene);

        } catch (IOException e) {
            System.out.println("Failure to load view " + view + " : " + e.getMessage());
        }
    }

    public void back() {

        if (scenes.isEmpty()) {
            return;
        }

        // remove the current scene from the stack
        scenes.pop();

        // load the scene at the top of the stack
        setScene(scenes.peek());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setScene(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }


    @SuppressWarnings("unchecked")
    public <T> T getController(String view) {
        return (T) controllers.get(view);
    }
}
