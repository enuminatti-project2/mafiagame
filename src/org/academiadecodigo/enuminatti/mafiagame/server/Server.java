package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.game.Role;
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

        private String nickname;
        private Role role;
        private BufferedReader in;
        private PrintWriter out;

        public ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        private void init() throws IOException {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }

        private void receiveMessage(String message) {
            gameMaster.receiveMessage(message, nickname);
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void setRole(Role role) {
            this.role = role;
        }

        @Override
        public void run() {
            try {
                String message = "";
                while ((message = in.readLine()) != null) {
                    if (nickname == null) {
                        if (!tryRegister(message)) {
                            sendMessage(EncodeDecode.MESSAGE.encode("The nickname you chose is already in use."));
                            continue;
                        }
                        this.nickname = EncodeDecode.NICK.decode(message);
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
            System.out.println("disconnected player");
            try {
                System.out.println(nickname);
                if (gameMaster.getListOfPlayers().containsKey(nickname)) {
                    gameMaster.kickPlayer(nickname);
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

            return gameMaster.addNick(EncodeDecode.NICK.decode(message), this);
        }

        public Role getRole() {
            return role;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

}
