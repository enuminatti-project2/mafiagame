package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.util.*;

/**
 * Created by Samuel Laço on 07/11/17.
 */
public enum EncodeDecode {
    MESSAGE("<MSG>", "</MSG>"),
    NICK("<NICK>", "</NICK>"),
    NICKOK("<NICKOK>", "</NICKOK>");

    private static ArrayList<String> listEnum = new ArrayList<>(values().length);
    private String startTag;
    private String endTag;

    static{
        for (int i = 0; i < values().length; i++) {
            listEnum.add(values()[i].getStart());
        }
    }

    EncodeDecode(String s, String s1) {
        this.startTag = s;
        this.endTag = s1;
    }

    /**
     * Encode the string with the ENUM tags
     * @param message string to encode
     * @return the encoded string
     */
    public String encode(String message) {
        return startTag + message + endTag;
    }

    /**
     * Decode one string with with the ENUM tags
     * @param message to decode
     * @return the decoded string
     */
    public String decode(String message) {
        if (canDecode(message)){
            return message.substring(message.indexOf(startTag) + startTag.length(), message.lastIndexOf(endTag));
        }
        return null;
    }

    private boolean canDecode(String message) {
        return (message.startsWith(startTag) && message.endsWith(endTag));
    }

    private String getStart(){
        return startTag;
    }

    /**
     * check if a given starting tag is supported by this enum (EncodeDecode.isInEnum("<TAG>"))
     * @param tag to check
     * @return true or false
     */
    public static boolean isInEnum(String tag){
        return listEnum.contains(tag);
    }
}
