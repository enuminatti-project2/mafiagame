package org.academiadecodigo.enuminatti.mafiagame.client.control;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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

import java.text.SimpleDateFormat;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChatController implements Controller {

    private Client client;
    private String nightCSS;
    private String dayCSS;
    private boolean night;
    private Sound gunShotSound;
    private SimpleDateFormat dateFormat;
    private ScheduledExecutorService timerExecutor = null;

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
        timerExecutor = null;
        dateFormat = new SimpleDateFormat("[HH:mm:ss] ");
        gunShotSound = new Sound(Constants.GUN_SHOT_SOUND_PATH);
    }

    @FXML
    void sendMessageToClient(ActionEvent event) {

        if (sendButton.getText().equals("Back")) {
            Platform.runLater(() -> {
                SceneNavigator.getInstance().loadScreen("Lobby");
                SceneNavigator.getInstance().<LobbyController>getController("Lobby").setClient(client);
            });
        }

        // don't send anything if there's not at least one non-whitespace character
        if (clientPrompt.getText().matches(".*\\S.*")) {
            // sanitize too many whitespaces to just a single space
            String message = clientPrompt.getText().replaceAll("\\s+", " ");

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
        client.encodeAndSend(EncodeDecode.NICKLIST, "asking for my nick");
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
                    newText.setText(dateFormat.format(new java.util.Date()) + finalMessage + "\n");
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

    private ImageView getEndImage() {
        return endImage;
    }

    void killed(){
        if (!isNight()) {
            getEndImage().setImage(new Image(Constants.ROPE_IMAGE_PATH));
            getEndImage().setFitWidth(240.0);
            getEndImage().setFitHeight(400.0);
            getEndImage().setY(-100.0);
        }
        getEndImage().setVisible(true);
        getGunShotSound().play(true);
        getSendButton().setDisable(false);
        ScheduledFuture<?> schedule = timerExecutor.schedule(this::bye,
                Constants.SECONDS_ENDGAME, TimeUnit.SECONDS);
    }

    void bye(){
        Platform.runLater(() -> SceneNavigator.getInstance().back());
    }

    private Sound getGunShotSound() {
        return gunShotSound;
    }

    private boolean isNight() {
        return night;
    }

    public Client getClient() {
        return client;
    }
}