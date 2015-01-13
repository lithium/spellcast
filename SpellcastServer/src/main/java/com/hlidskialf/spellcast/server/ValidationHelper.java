package com.hlidskialf.spellcast.server;

import java.util.regex.Pattern;

/**
 * Created by wiggins on 1/12/15.
 */
public class ValidationHelper {
    public final static Pattern validNicknamePattern = Pattern.compile("^\\w+$");
    public final static String validGestureCharacters = "FPSWDCK_";

    public static boolean isNicknameValid(String nickname) {
        return (nickname.length() > 0 &&
                nickname.length() <= 32 &&
                validNicknamePattern.matcher(nickname).matches());
    }

    public static boolean isGestureValid(String gesture) {
        if (gesture.length() != 1) {
            return false;
        }
        char c = gesture.toUpperCase().charAt(0);
        return validGestureCharacters.indexOf(c) != -1;
    }

    public static boolean isHandValid(String hand) {
        if (hand == null || hand.isEmpty()) {
            return false;
        }
        hand = hand.toLowerCase();
        return (hand.equals("left") || hand.equals("right"));
    }
    public static boolean isSpellValid(String slug) {
        Spell s = SpellList.lookupSpellBySlug(slug);
        return s != null;
    }


}
