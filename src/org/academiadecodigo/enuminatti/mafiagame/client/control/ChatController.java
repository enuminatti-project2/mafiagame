package org.academiadecodigo.enuminatti.mafiagame.client.control;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.Sound;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

public class ChatController implements Controller {

    private Client client;
    private String nightCSS;
    private String dayCSS;
    private boolean night;
    private Sound gunShotSound = new Sound(Constants.GUN_SHOT_SOUND_PATH);

    @FXML
    private TextFlow flowChat;

    @FXML
    private TextField clientPrompt;

    @FXML
    private Button sendButton;

    @FXML
    private Button voteButton;

    @FXML
    private Pane pane;

    @FXML
    private ListView<String> usersList;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ImageView endImage;

    @FXML
    void initialize() {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert clientPrompt != null : "fx:id=\"clientPrompt\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert flowChat != null : "fx:id=\"flowChat\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert usersList != null : "fx:id=\"usersList\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert sendButton != null : "fx:id=\"sendButton\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert voteButton != null : "fx:id=\"voteButton\" was not injected: check your FXML file 'ClientView.fxml'.";

        scrollPane.vvalueProperty().bind(flowChat.heightProperty());
        System.out.println("Disabling vote button");
        voteButton.setDisable(true);
        //sendButton.setDisable(true);
    }

    @FXML
    void sendMessageToClient(ActionEvent event) {

        if (sendButton.getText().equals("Back")){
            Platform.runLater(() -> SceneNavigator.getInstance().loadScreen("Lobby"));
        }

        if (clientPrompt.getText().matches(".*\\S.*")) {
            //chatWindow.appendText(clientPrompt.getText().replaceAll("\\s+", " ") + "\n");
            String message = clientPrompt.getText();
            client.encodeAndSend(EncodeDecode.MESSAGE, message);
            clientPrompt.setText("");
            clientPrompt.requestFocus();
        }
    }

    @FXML
    void vote(ActionEvent event) {

        String votedUser = usersList.getSelectionModel().getSelectedItem();

        if (votedUser != null) {
            client.encodeAndSend(EncodeDecode.VOTE, votedUser);
            usersList.getSelectionModel().clearSelection();
            voteButton.setDisable(true);
        }
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

    void setClient(Client client) {
        this.client = client;
        this.client.setController(this);
        client.encodeAndSend(EncodeDecode.NICKLIST, "que sa foda este encode");
        client.encodeAndSend(EncodeDecode.ROLE, "asking for my role");
        setNight("false");
    }

    void updateNickList(String message) {
        String allnick[] = message.split(" ");
        ObservableList<String> names = FXCollections.observableArrayList(allnick);
        Platform.runLater(() -> usersList.setItems(names));
    }


    void setNight(String message) {

        night = Boolean.parseBoolean(message);

        System.out.println("Night: " + night);

        if (dayCSS == null) {
            nightCSS = getClass().getResource("css/night.css").toExternalForm();
            dayCSS = getClass().getResource("css/day.css").toExternalForm();
        }

        if (night) {
            StyleManager.getInstance().removeUserAgentStylesheet(dayCSS);
            StyleManager.getInstance().addUserAgentStylesheet(nightCSS);
            return;
        }
        StyleManager.getInstance().removeUserAgentStylesheet(nightCSS);
        StyleManager.getInstance().addUserAgentStylesheet(dayCSS);

    }

    void writeNewLine(String message, Color color) {

        final String finalMessage = (message == null ? "" : message);
        final Color textColor;

        if (night && color == Color.BLACK) {
            textColor = Color.WHITE;
        } else {
            textColor = color;
        }

        Platform.runLater(
                () -> {

                    Text newText = new Text();
                    newText.setFill(textColor);
                    newText.setText(finalMessage + "\n");
                    flowChat.getChildren().add(newText);
                }
        );


    }

    Button getSendButton() {
        return sendButton;
    }

    Button getVoteButton() {
        return voteButton;
    }

    ImageView getEndImage() {
        return endImage;
    }

    Sound getGunShotSound() {
        return gunShotSound;
    }

    boolean isNight() {
        return night;
    }
}