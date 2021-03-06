package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.text.SimpleDateFormat;

public class LobbyController implements Controller {

    private Client client;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss] ");


    @FXML
    private Pane pane;

    @FXML
    private TextField clientPrompt;

    @FXML
    private ListView<String> usersList;

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
        shutdown();
        SceneNavigator.getInstance().loadScreen("LoginScreen");
    }

    @FXML
    void sendMessageToClient(ActionEvent event) {
        if (clientPrompt.getText().matches(".*\\S.*")) {
            String message = clientPrompt.getText();
            this.client.encodeAndSend(EncodeDecode.LOBBYMESSAGE, message);
            clientPrompt.setText("");
            clientPrompt.requestFocus();
        }
    }

    @FXML
    void showStats(ActionEvent event) {

        client.encodeAndSend(EncodeDecode.SCORE,null);

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
        client.encodeAndSend(EncodeDecode.LOBBYNICKLIST, "");

    }

    public void getMessage(String message) {
        ControllerDecoder.lobbyControllerDecoder(this, message);
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    void writeNewLine(String message) {

        Platform.runLater(
                () -> {
                    Text newText = new Text();
                    newText.setText(dateFormat.format(new java.util.Date()) + message + "\n");
                    flowChat.getChildren().add(newText);
                }
        );

    }

    void getStats(String stats) {

        String[] statsplit = stats.split("\\s+");

        if(statsplit.length != 5){
            return;
        }
        updateStats(statsplit);

    }


    private void updateStats(String[] mystats) {
        Platform.runLater(
                () -> {
                    nameStats.setText(mystats[0]);
                    gamesWon.setText(mystats[1]);
                    gamesLost.setText(mystats[2]);
                    turnsSurvived.setText(mystats[3]);
                    totalPoints.setText(mystats[4]);
                }
        );
    }

    void updateNickList(String message) {
        String allnick[] = message.split(" ");
        ObservableList<String> names = FXCollections.observableArrayList(allnick);
        Platform.runLater(() -> usersList.setItems(names));
    }

    void clearChat() {
        flowChat.getChildren().clear();
    }
}



