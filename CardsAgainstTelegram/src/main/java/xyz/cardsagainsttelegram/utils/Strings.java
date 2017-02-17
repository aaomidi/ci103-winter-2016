package xyz.cardsagainsttelegram.utils;


import java.security.SecureRandom;

public class Strings {
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static SecureRandom rnd = new SecureRandom();

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
