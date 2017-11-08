package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.util.*;

/**
 * Created by Samuel Laço on 07/11/17.
 */
public enum EncodeDecode {
    MESSAGE("<MSG>", "</MSG>"),
    NICK("<NICK>", "</NICK>"),
    NICKOK("<NICKOK>", "</NICKOK>"),
    NICKMESSAGE("<_NICKMESSAGE>", "</_NICKMESSAGE>");

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

    /*
      TODO: multi string encode / decode
     */

    public void encode(Map<EncodeDecode, String> mapToSend) {

    }

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
            EncodeDecode enumtype = getEnum(getStartTag(stringToDecode));
            mapToReturn.put(enumtype,enumtype.decode(stringToDecode));
        }

        return mapToReturn;
    }

    private boolean isGroupString(String message){
        String tag = getStartTag(message);
        return (tag != null && tag.startsWith("<_"));
    }

    public static EncodeDecode getEnum(String tag) {
        if (!isInEnum(tag)){
            return null;
        }
        return mapEnum.get(tag);
    }
}
