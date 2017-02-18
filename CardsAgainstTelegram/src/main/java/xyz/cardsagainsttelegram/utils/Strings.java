package xyz.cardsagainsttelegram.utils;


import xyz.cardsagainsttelegram.bean.game.LobbyJoinResult;

import java.security.SecureRandom;

public class Strings {
    public static final String INVITE_OTHERS = "\uD83D\uDCAC"; // 💬
    public static final String GO_BACK = "\uD83D\uDD19"; // 🔙
    public static final String CREATE_LOBBY = "\uD83D\uDD28"; // 🔨
    public static final String NAME = "\uD83D\uDCDB"; // 📛
    public static final String LOBBY_SETTINGS = "⚙️"; // ⚙️
    public static final String JOIN_LOBBY = "\uD83D\uDD11️"; // 🔑
    public static final String LEAVE_LOBBY = "\uD83D\uDEAA"; // 🚪
    public static final String PERSON_TALKING = "\uD83D\uDDE3️"; // 🗣️
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom rnd = new SecureRandom();

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

    public static String getString(LobbyJoinResult result) {
        switch (result) {
            case LOBBY_NOT_FOUND:
                return "Lobby not found";
            case LOBBY_FULL:
                return "Lobby is full";
            case PLAYER_HAS_LOBBY:
                return "You are already in a lobby. Use /leave to leave your lobby.";
            case UNKNOWN:
                return "An unknown error occured, please contact @aaomidi";
            case SUCCESS:
                return "Joined lobby! Don't forget to say hi!";
        }
        return "";
    }

}
