package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

public class LobbyController implements Controller {

    private Client client;

    @FXML
    private Pane pane;

    @FXML
    private TextField clientPrompt;

    @FXML
    private ListView<?> usersList;

    @FXML
    private Button sendButton;

    @FXML
    private Button statsButton;

    @FXML
    private TextFlow flowChat;

    @FXML
    private GridPane statsWindow;

    @FXML
    private Label nameStats;

    @FXML
    private Label gamesWon;

    @FXML
    private Label gamesLost;

    @FXML
    private Label turnsSurvived;

    @FXML
    private Label totalPoints;

    @FXML
    private Button logoutButton;

    @FXML
    void logout(ActionEvent event) {
        SceneNavigator.getInstance().back();
    }

    @FXML
    void sendMessageToClient(ActionEvent event) {

    }

    @FXML
    void showStats(ActionEvent event) {

        if (statsWindow.isVisible()) {
            statsWindow.setVisible(false);
            return;
        }

        statsWindow.setVisible(true);

    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        this.client.setController(this);
        client.encodeAndSend(EncodeDecode.NICK, "que sa foda este encode");
    }

    public void getMessage(String message) {

        ControllerDecoder.chatControllerDecoder(this, message);
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    void writeNewLine(String message) {

    }

    void  updateStats(String nickname){

        Platform.runLater(
                () -> {
                    System.out.println(nameStats);
                    nameStats.setText(nickname);
                }
        );

    }

}



