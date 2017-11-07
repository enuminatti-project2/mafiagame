package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.net.Socket;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class Server {

    private GameMaster gameMaster;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private void start() {
        // open socket
        // while to acceptConnections
    }

    public Server() {
        this.gameMaster = new GameMaster();
    }

    private void acceptConnection() {
        PlayerHandler newPlayer = new PlayerHandler();
    }


    public class PlayerHandler {
        private Socket clientSocket;

        private String nickname;
        private boolean alive;
        private Role role;

        private void receiveMessage() {
            // while to BufferedReader.readLine()
            // when new message gameMaster.receiveAndDecode(message)
        }

        public void sendMessage(String message) {
            // send message to client
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }
    }
}
