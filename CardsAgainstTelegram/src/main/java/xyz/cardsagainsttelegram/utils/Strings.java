package xyz.cardsagainsttelegram.utils;


import xyz.cardsagainsttelegram.bean.game.enums.GenericResult;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;

import java.security.SecureRandom;

public class Strings {
    public static final String INVITE_OTHERS = "\uD83D\uDCAC"; // 💬
    public static final String GO_BACK = "\uD83D\uDD19"; // 🔙
    public static final String CREATE_LOBBY = "\uD83D\uDD28"; // 🔨
    public static final String NAME = "\uD83D\uDCDB"; // 📛
    public static final String LOBBY_SETTINGS = "⚙"; // ⚙
    public static final String JOIN_LOBBY = "\uD83D\uDD11️"; // 🔑
    public static final String LEAVE_LOBBY = "\uD83D\uDEAA"; // 🚪
    public static final String PERSON_TALKING = "\uD83D\uDDE3️"; // 🗣️
    public static final String CZAR = "\uD83D\uDC51"; // 👑
    public static final String ALERT = "⚠"; // ⚠

    public static final String[] NUMBERS = new String[11];
    public static final String BLUE_CIRCLE = "\uD83D\uDD35"; // 🔵
    public static final String RED_CIRCLE = "\uD83D\uDD34"; // 🔴
    public static final String SUBMIT_BUTTON = "\uD83D\uDD2E"; // 🔮
    public static final String RIGHT_ARROW = "➡"; // ➡

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom rnd = new SecureRandom();

    //
    static {
        NUMBERS[0] = "\u0030\u20E3";
        NUMBERS[1] = "\u0031\u20E3";
        NUMBERS[2] = "\u0032\u20E3";
        NUMBERS[3] = "\u0033\u20E3";
        NUMBERS[4] = "\u0034\u20E3";
        NUMBERS[5] = "\u0035\u20E3";
        NUMBERS[6] = "\u0036\u20E3";
        NUMBERS[7] = "\u0037\u20E3";
        NUMBERS[8] = "\u0038\u20E3";
        NUMBERS[9] = "\u0039\u20E3";
        NUMBERS[10] = "\uD83D\uDD1F";
    }

    /**
     * Generates a random string.
     *
     * @param len The length of the string to generate
     * @return The generated string.
     */
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static String getString(LobbyResult result) {
        switch (result) {
            case LOBBY_NOT_FOUND:
                return "Lobby not found.";
            case LOBBY_FULL:
                return "Lobby is full.";
            case LOBBY_STARTED:
                return "You can not join an active game.";
            case PLAYER_HAS_LOBBY:
                return "You are already in a lobby. Use /leave to leave your lobby.";
            case PLAYER_NO_LOBBY:
                return "You do not have a lobby. Use /createlobby to make a lobby.";
            case PLAYER_NOT_IN_LOBBY:
                return "You are not in the lobby.";
            case PLAYER_IN_LOBBY:
                return "You are already in that lobby.";
            case PLAYER_NOT_OWN_LOBBY:
                return "You do not have permissions to modify this lobby.";
            case UNKNOWN:
                return "An unknown error occured, please contact @.";
            case SUCCESS:
                return "Successful!";
        }
        return "";
    }

    public static String getString(GenericResult result) {
        switch (result) {
            case NOT_ENOUGH_ARGS:
                return "Not enough arguments.";
        }
        return "";
    }

    public static String escape(String text, boolean brackets) {
        String res = (text == null) ? "null" : (brackets ? text.replace("[", "\\[") : text);
        return res.replace("*", "\\*").replace("_", "\\_").replace("`", "\\`");
    }

    public static String getNumber(int number) {
        if (number <= 10) {
            return NUMBERS[number];
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int i = number % 10;
            sb.insert(0, NUMBERS[i]);
            number /= 10;

        }
        return sb.toString();
    }
}
