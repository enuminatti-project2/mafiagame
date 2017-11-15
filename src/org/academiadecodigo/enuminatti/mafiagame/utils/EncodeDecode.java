package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel Laço on 07/11/17.
 */
public enum EncodeDecode {
    MESSAGE("MSG"),
    LOBBYMESSAGE("LOBBYMESSAGE"),
    NICK("NICK"),
    GUESTLOGIN("GUEST"),
    LOGIN("LOGIN"),
    NICKOK("NICKOK"),
    LOBBY ("LOBBY"),
    TIMER("TIMER"),
    NIGHT("NIGHT"),
    VOTE("VOTE"),
    NICKLIST("NICKLIST"),
    LOBBYNICKLIST("LOBBYNICKLIST"),
    START("START"),
    KILL("KILL"),
    ROLE("ROLE"),
    OVER("OVER"),
    ALLOW_TALK("ALLOW_TALK"),
    ALLOW_VOTE("ALLOW_VOTE"),
    ALLOW_VISIT("ALLOW_VISIT"),
    HOSTSLIST("HOSTS"),
    PWDERROR("PWDERROR");

    private static Map<String, EncodeDecode> mapEnum = new HashMap<>();
    private String tag;

    static {
        for (int i = 0; i < values().length; i++) {
            mapEnum.put(values()[i].getStart(), values()[i]);
        }
    }

    EncodeDecode(String tag) {
        this.tag = tag;
    }

    /**
     * Encode the string with the ENUM tags
     *
     * @param message string to encode
     * @return the encoded string
     */
    public String encode(String message) {
        return String.format("<%s>%s</%s>", tag, message, tag);
    }

    /**
     * Decode one string with with the ENUM tags
     *
     * @param message to decode
     * @return the decoded string or null if cant be decoded
     */
    public String decode(String message) {
        if (canDecode(message)) {
            return message.substring(message.indexOf(getStart()) + getStart().length(),
                    message.lastIndexOf("</" + tag + ">"));
        }
        return null;
    }

    private boolean canDecode(String message) {
        return (message.startsWith("<" + tag + ">") && message.endsWith("</" + tag + ">"));
    }

    private String getStart() {
        return "<" + tag + ">";
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
