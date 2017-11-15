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

public class LobbyController implements Controller {

    private Client client;

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
        SceneNavigator.getInstance().back();
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
        client.encodeAndSend(EncodeDecode.LOBBYNICKLIST, "que sa foda este encode");

    }

    public void getMessage(String message) {
        ControllerDecoder.lobbyControllerDecoder(this, message);
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    void writeNewLine(String message) {

        Platform.runLater(
                () -> {
                    Text newText = new Text();
                    newText.setText(message + "\n");
                    flowChat.getChildren().add(newText);
                }
        );

    }

    void updateStats(String nickname) {

        Platform.runLater(
                () -> {
                    System.out.println(nameStats);
                    nameStats.setText(nickname);
                }
        );

    }

    public void updateNickList(String message) {
        String allnick[] = message.split(" ");
        ObservableList<String> names = FXCollections.observableArrayList(allnick);
        Platform.runLater(() -> usersList.setItems(names));
    }


}



