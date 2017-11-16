package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.persistence.ConnectionManager;
import org.academiadecodigo.enuminatti.mafiagame.server.persistence.JdbcLogin;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
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
    private Map<String, String> hostsMap;
    private JdbcLogin jdbc;
    private List<ServerWorker> serverWorkers;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public Server() {
        this.gameMaster = new GameMaster();
        executorService = Executors.newFixedThreadPool(Constants.MAX_PLAYERS);
        hostsMap = new LinkedHashMap<>();
        jdbc = new JdbcLogin(ConnectionManager.getConnection());
        serverWorkers = new LinkedList<>();
    }

    private void closeServer() {
        if (server.isClosed()) {
            return;
        }

        shutdownAllWorkers();
        executorService.shutdown();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdownAllWorkers() {
        System.out.println(serverWorkers.size());
        for (ServerWorker worker : serverWorkers) {
            worker.shutdown();
        }
    }

    private void start() {
        // open socket
        // while to acceptConnections

        try {
            server = new ServerSocket(Constants.PORT);

            // type anything in server to cleanly exit
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                closeServer();
            }).start();

            System.out.println("listening to new connections");
            while (true) {
                Socket client = server.accept();
                System.out.println("New Connection was accepted. Socket number: " + client.getPort());
                acceptConnection(client);
            }

        } catch (IOException e) {
            System.out.println("Closed server socket");
        } finally {
            closeServer();
        }
    }

    private void acceptConnection(Socket client) {
        ServerWorker newPlayer;
        try {
            newPlayer = new ServerWorker(client);
            newPlayer.init();
            serverWorkers.add(newPlayer);
            executorService.submit(newPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void updateHostList(String message) {
        Map<String, String> tempMap = new LinkedHashMap<>();

        String decodedMessage = EncodeDecode.HOSTSLIST.decode(message);

        if (decodedMessage == null || decodedMessage.equals("")) {
            return;
        }

        String[] hostslist = decodedMessage.split(",");

        for (String host : hostslist) {
            String[] s = host.split("\\|");
            tempMap.put(s[0], s[1]);
        }

        hostsMap.putAll(tempMap);
    }


    public class ServerWorker implements Runnable {
        private Socket clientSocket;

        private BufferedReader in;
        private PrintWriter out;
        private Player player;

        ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        private void init() throws IOException {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }

        void receiveMessage(String message) {
            player.getFromPlayer(message);
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {

                    EncodeDecode parsedEncoding = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

                    if (parsedEncoding == null) {
                        // ignore unencoded message
                        continue;
                    }

                    switch (parsedEncoding) {
                        case HOSTSLIST:
                            updateHostList(message);
                            break;
                        case LOGIN:
                            doLogin(message);
                            break;
                        case GUESTLOGIN:
                            guestLogin();
                            break;
                        default:
                            receiveMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.err.println("Player disconnected.");
                disconnectPlayer();
            }
        }

        private void guestLogin() {
            String nick = "Guest" + (int) (Math.floor(Math.random() * (1000)));

            while (!tryRegister(nick)) {
                nick = "Guest" + (int) (Math.floor(Math.random() * (1000)));
            }
        }

        private void doLogin(String message) {
            String nickAndPass = EncodeDecode.LOGIN.decode(message);

            if (nickAndPass == null || nickAndPass.split(",").length != 2) {
                return;
            }

            String[] splitUserPass = nickAndPass.split(",");

            if (player == null) {

                if (ConnectionManager.getConnection() == null) {
                    System.out.println("Database is down, using Local Storage");
                    if (!tryRegister(splitUserPass[0])) {
                        sendMessage(EncodeDecode.NICKOK.encode("false"));
                    }
                    return;
                }

                if (jdbc.authenticate(splitUserPass[0], splitUserPass[1])) {
                    if (!tryRegister(splitUserPass[0])) {
                        sendMessage(EncodeDecode.NICKOK.encode("false"));
                    }
                    return;
                }

                if (jdbc.userExists(splitUserPass[0])) {
                    sendMessage(EncodeDecode.PWDERROR.encode("true"));
                    return;
                }

                if (jdbc.addUser(splitUserPass[0], splitUserPass[1])) {
                    tryRegister(splitUserPass[0]);
                }
            }
        }

        public void disconnectPlayer() {
            if (clientSocket.isClosed()) {
                return;
            }
            gameMaster.removePlayerFromLists(player);
            shutdown();
        }

        private boolean tryRegister(String nick) {
            if (gameMaster.addNick(nick, this)) {

                player = gameMaster.getListOfLobby().get(nick);
                return true;
            }
            return false;
        }


        public void shutdown() {
            if (clientSocket == null) {
                return;
            }
            try {
                clientSocket.shutdownInput();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Client socket closed");
            }
        }
    }
}

