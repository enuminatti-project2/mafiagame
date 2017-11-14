package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.util.*;

/**
 * Created by Samuel Laço on 07/11/17.
 */
public enum EncodeDecode {
    MESSAGE("<MSG>", "</MSG>"),
    NICK("<NICK>", "</NICK>"),
    GUESTLOGIN("<GUEST>", "</GUEST>"),
    LOGIN("<LOGIN>", "</LOGIN>"),
    NICKOK("<NICKOK>", "</NICKOK>"),
    TIMER("<TIMER>", "<TIMER>"),
    NIGHT("<NIGHT>", "</NIGHT>"),
    VOTE("<VOTE>", "<VOTE>"),
    NICKLIST("<NICKLIST>", "</NICKLIST>"),
    START("<START>", "</START>"),
    KILL("<KILL>", "</KILL>"),
    ROLE("<ROLE>", "</ROLE>"),
    OVER("<OVER>", "</OVER>"),
    ALLOW_TALK("<ALLOW_TALK>", "</ALLOW_TALK>"),
    ALLOW_VOTE("<ALLOW_VOTE>", "</ALLOW_VOTE>"),
    HOSTSLIST("<HOSTS>", "</HOST>"),
    PWDERROR("<PWDERROR>","</PWDERROR>");

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
     * Return the enum value that have the given tag
     *
     * @param tag the tag to check in the enum
     * @return the enum value
     */
    public static EncodeDecode getEnum(String tag) {
        if (!isInEnum(tag)) {
            return null;
        }
        return mapEnum.get(tag);
    }
}
