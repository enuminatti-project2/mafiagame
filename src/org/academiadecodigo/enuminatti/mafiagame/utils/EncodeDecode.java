package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.util.*;

/**
 * Created by Samuel Laço on 07/11/17.
 */
public enum EncodeDecode {
    MESSAGE("<MSG>", "</MSG>"),
    NICK("<NICK>", "</NICK>"),
    NICKOK("<NICKOK>", "</NICKOK>"),
    TIMER("<TIMER>", "<TIMER>"),
    NICKMESSAGE("<_NICKMESSAGE>", "</_NICKMESSAGE>"), //Tags that begin with _ are group tags
    VOTE("<VOTE>", "<VOTE>"),
    START("<START>", "</START>"); //Tags that begin with _ are group tags

    //private static ArrayList<String> listEnum = new ArrayList<>(values().length);
    private static Map<String, EncodeDecode> mapEnum = new HashMap<>();
    private String startTag;
    private String endTag;

    static {
        for (int i = 0; i < values().length; i++) {
            //listEnum.add(values()[i].getStart());
            mapEnum.put(values()[i].getStart(), values()[i]);
        }
    }

    EncodeDecode(String s, String s1) {
        this.startTag = s;
        this.endTag = s1;
    }

    public String getStartTag() {
        return startTag;
    }

    /**
     * Encode the string with the ENUM tags
     *
     * @param message string to encode
     * @return the encoded string
     */
    public String encode(String message) {
        return startTag + message + endTag;
    }

    /**
     * Decode one string with with the ENUM tags
     *
     * @param message to decode
     * @return the decoded string or null if cant be decoded
     */
    public String decode(String message) {
        if (canDecode(message)) {
            return message.substring(message.indexOf(startTag) + startTag.length(), message.lastIndexOf(endTag));
        }
        return null;
    }

    private boolean canDecode(String message) {
        return (message.startsWith(startTag) && message.endsWith(endTag));
    }

    private String getStart() {
        return startTag;
    }

    /**
     * Check if a given starting tag is supported by this enum (EncodeDecode.isInEnum("<TAG>"))
     *
     * @param tag to check
     * @return true or false
     */
    public static boolean isInEnum(String tag) {
        //return listEnum.contains(tag);
        return mapEnum.get(tag) != null;
    }

    /**
     * Get the start Tag of the given string
     *
     * @param message the string to analise
     * @return a valid tag <TAG> or null
     */
    public static String getStartTag(String message) {
        if (!message.startsWith("<")) {
            return null;
        }
        String tempTag = message.replaceAll("(?<=>)(.*)", "");

        if (tempTag.matches("(<\\w+>)*")) {
            return tempTag;
        }
        return null;
    }

    /**
     * Wrap the values of a given Map inside a tag
     * @param mapToSend the map with the values to send
     * @return the Encoded String
     */
    public String encode(Map<EncodeDecode, String> mapToSend) {
        String encodedString = "";

        for (Map.Entry<EncodeDecode, String> entry : mapToSend.entrySet()) {
            EncodeDecode key = entry.getKey();
            String value = entry.getValue();

            encodedString += key.encode(value);
        }
        encodedString = encode(encodedString);

        return encodedString;
    }

    /**
     * Decode a multiple tags string to a Map<EncodeDecode, String>
     * @param message the message to decode
     * @return the map with the multiple values
     */
    public Map<EncodeDecode, String> decodeStringMap(String message) {
        if(!isGroupString(message)){
            return null;
        }

        String decodedString = decode(message);
        if (decodedString == null){
            return null;
        }

        String[] splitted = decodedString.split("(?=<)(?<=>)");

        Map<EncodeDecode, String> mapToReturn = new HashMap<>();

        for (String stringToDecode: splitted) {
            EncodeDecode enumType = getEnum(getStartTag(stringToDecode));
            mapToReturn.put(enumType,enumType.decode(stringToDecode));
        }

        return mapToReturn;
    }

    /**
     * check if a given string is a group of strings
     * @param message the message to analyze
     * @return true or false
     */
    private boolean isGroupString(String message){
        String tag = getStartTag(message);
        return (tag != null && tag.startsWith("<_"));
    }

    /**
     * Return the enum value that have the given tag
     * @param tag the tag to check in the enum
     * @return  the enum value
     */
    public static EncodeDecode getEnum(String tag) {
        if (!isInEnum(tag)){
            return null;
        }
        return mapEnum.get(tag);
    }
}
