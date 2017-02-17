package xyz.cardsagainsttelegram.utils;


import java.security.SecureRandom;

public class Strings {
    public static final String INVITE_OTHERS = "\uD83D\uDCAC"; // 💬
    public static final String GO_BACK = "\uD83D\uDD19"; // 🔙
    public static final String CREATE_LOBBY = "\uD83D\uDD28"; // 🔨
    public static final String NAME = "\uD83D\uDCDB"; // 📛
    public static final String LOBBY_SETTINGS = "⚙️"; // ⚙️
    public static final String JOIN_LOBBY = "\uD83D\uDD11️"; // 🔑
    public static final String LEAVE_LOBBY = "\uD83D\uDEAA"; // 🚪

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
}
