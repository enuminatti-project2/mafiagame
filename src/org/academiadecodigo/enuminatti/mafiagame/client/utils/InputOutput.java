package org.academiadecodigo.enuminatti.mafiagame.client.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InputOutput {

    private static final String hostPath = "savedfiles/Hosts.txt";
    private static final String nicksPath = "savedfiles/Nicks.txt";

    /**
     * Read all the nicks from the file
     * @return a Set with the nicks
     */
    public static Set<String> readNicks() {

        File file = new File(nicksPath);
        if (!file.exists()){
            return null;
        }

        Set<String> listOfNicks = new HashSet<>();


        FileReader fReader = null;
        BufferedReader bReader = null;

        String line = null;

        try {
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            line = bReader.readLine();
            while (line != null) {
                listOfNicks.add(line);
                line = bReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fReader.close();
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listOfNicks;
    }

    /**
     * Read all the hosts from the file
     * @return the LinkedHashMap with the hosts
     */
    public static LinkedHashMap<String, String> readHosts() {

        File file = new File(hostPath);
        if (!file.exists()){
            return null;
        }

        LinkedHashMap<String, String> listOfSN = new LinkedHashMap<>();

        FileReader fReader = null;
        BufferedReader bReader = null;

        String line = null;

        try {
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            line = bReader.readLine();

            while (line != null) {
                String[] s = line.split("\\|");
                listOfSN.put(s[0], s[1]);
                line = bReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fReader.close();
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listOfSN;
    }

    /**
     * Reverse the order of an LinkedHashMap
     * @param map LinkedHashMap to reverse
     * @return the reversed LinkedHashMap
     */
    public static LinkedHashMap<String, String> reverseOrderMap(Map<String, String> map) {
        ArrayList<String> arrayList = new ArrayList<>(map.keySet());
        LinkedHashMap<String, String> newMap = new LinkedHashMap<>();
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            String value = map.get(arrayList.get(i));
            String key = arrayList.get(i);
            newMap.put(key, value);
        }
        return newMap;
    }

    /**
     * Edit the name of the host referenced by the ip and port
     * @param ip of the host
     * @param name new name to the host
     */
    public static void editHost(String ip, String name) {
        createIfNotExists(hostPath);

        LinkedHashMap<String, String> hosts = readHosts();
        if (hosts == null) {
            return;
        }

        hosts.put(ip, name);

        writeToFile(mapToString(hosts), hostPath);

    }

    /**
     * Delete a host referenced by the ip and port
     * @param ip of the host
     */
    public static void deleteHost(String ip){
        createIfNotExists(hostPath);

        LinkedHashMap<String, String> hosts = readHosts();

        if (hosts == null) {
            return;
        }

        hosts.remove(ip);

        writeToFile(mapToString(hosts), hostPath);

    }

    /**
     * Delete the nick
     * @param nick delete
     */
    public static void deleteNick (String nick) {

        Set<String> nicks = readNicks();
        if (nicks == null) {
            return;
        }

        nicks.remove(nick);

        writeToFile(setToString(nicks), nicksPath);
    }

    /**
     * Add a new host
     * @param ip of the host do add
     * @param name of the host to add
     */
    public static void addHost(String ip, String name) {
        final String lineSeparator = System.getProperty("line.separator");

        createIfNotExists(hostPath);

        String newHost = ip + "|" + name + lineSeparator;

        try {
            Files.write(Paths.get(hostPath), newHost.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addHost(String host) {
        final String lineSeparator = System.getProperty("line.separator");


        Pattern pattern = Pattern.compile("(?<name>\\w+)?[\\s(]*(?<ip>" +
                ClientConstants.REGEXIP + ")");

        Matcher matcher = pattern.matcher(host);

        if (!matcher.matches()) {
            return;
        }

        String ip = matcher.group("ip");
        System.out.println(ip);
        String name = matcher.group("name");
        System.out.println(name);

        if (ip == null) {
            return;
        }

        createIfNotExists(hostPath);

        Map<String, String> currentHosts = readHosts();

        if (currentHosts != null && currentHosts.containsKey(ip)
                && currentHosts.get(ip).equals(name)) {
            return;
        } else if (currentHosts != null && currentHosts.containsKey(ip)) {
            editHost(ip, name);
            return;
        }

        String newHost = ip + "|" + (name != null ? name : "unnamed") + lineSeparator;

        try {
            Files.write(Paths.get(hostPath), newHost.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new nick
     * @param nick to add
     */
    public static void addNick(String nick) {
        final String lineSeparator = System.getProperty("line.separator");

        Set<String> nicks = readNicks();

        if (!nicks.add(nick) || nick == null){
            return;
        }

        createIfNotExists(nicksPath);

        Set<String> currentNicks = readNicks();

        if (currentNicks != null && currentNicks.contains(nick)) {
            return;
        }

        try {
            Files.write(Paths.get(nicksPath), (nick + lineSeparator).getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static StringBuilder mapToString(Map<String, String> map) {
        final String lineSeparator = System.getProperty("line.separator");

        StringBuilder bigString = new StringBuilder();

        for (Map.Entry<String, String> newLine : map.entrySet()) {

            bigString.append(newLine.getKey()).append("|").append(newLine.getValue()).append(lineSeparator);
        }
        return bigString;
    }


    private static StringBuilder setToString (Set<String> set) {
        final String lineSeparator = System.getProperty("line.separator");

        StringBuilder bigString = new StringBuilder();

        for(String s : set){
            bigString.append(s).append(lineSeparator);
        }
        return bigString;
    }

    private static void writeToFile(StringBuilder bigString, String path) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(path);
            writer.append(bigString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createIfNotExists(String pathToFile) {
        File file = new File(pathToFile);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}