package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.game.RoleFactory;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class Server {

    private GameMaster gameMaster;
    private ServerSocket server;
    private ExecutorService executorService;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public Server() {
        this.gameMaster = new GameMaster();
        executorService = Executors.newFixedThreadPool(Constants.MAX_PLAYERS);
    }

    private void closeServer() {
        executorService.shutdown();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        // open socket
        // while to acceptConnections

        try {
            server = new ServerSocket(Constants.PORT);
            System.out.println("listening to new connections");
            while (true) {
                Socket client = server.accept();
                System.out.println("New Connection was accept.Socket number: " + client.getPort());
                acceptConnection(client);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeServer();
        }
    }

    private void acceptConnection(Socket client) {
        ServerWorker newPlayer = null;
        try {
            newPlayer = new ServerWorker(client);
            newPlayer.init();
            executorService.submit(newPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class ServerWorker implements Runnable {
        private Socket clientSocket;

        private BufferedReader in;
        private PrintWriter out;
        private Player player;

        public ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        private void init() throws IOException {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }

        public void receiveMessage(String message) {
            System.out.println("Sending to player the message: " + message);
            player.getFromPlayer(message);
            System.out.println("Server sent.");
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        @Override
        public void run() {
            try {
                String message = "";
                while ((message = in.readLine()) != null) {
                    if (EncodeDecode.getStartTag(message).equals(EncodeDecode.NICK.getStartTag())
                            && gameMaster.getListOfPlayers().get(EncodeDecode.NICK.decode(message)) == null) {
                        if (!tryRegister(message)) {
                            sendMessage(EncodeDecode.MESSAGE.encode("The nickname you chose is already in use, you bitch."));
                        }
                    } else {
                        receiveMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnectPlayer();
            }
        }

        public void disconnectPlayer() {
            try {
                if (gameMaster.getListOfPlayers().containsKey(player.getName())) {
                    System.out.println("disconnected player: " + player.getName());

                    gameMaster.kickPlayer(player.getName());
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean tryRegister(String message) {
            if (!Objects.equals(EncodeDecode.getStartTag(message), EncodeDecode.NICK.getStartTag())) {
                return false;
            }

            String nickname = EncodeDecode.NICK.decode(message);
            if (gameMaster.addNick(nickname, this)) {
                player = gameMaster.getListOfPlayers().get(nickname);
                return true;
            }
            return false;

        }

        public boolean checkWinCondition() {
            return false;
        }

    }
}
