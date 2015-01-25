package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.spell.Spell;
import com.hlidskialf.spellcast.server.spell.SpellList;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by wiggins on 1/12/15.
 */
public class ValidationHelper {
    public final static Pattern validNicknamePattern = Pattern.compile("^\\w+$");
    public final static String validGestureCharacters = "CDFPSWK_";  // this order matters for Confusion spell

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

    public static String randomGesture() {
        int idx = new Random().nextInt(6);
        return validGestureCharacters.substring(idx,idx+1);
    }


    public static boolean isHandValid(String hand) {
        if (hand == null || hand.isEmpty()) {
            return false;
        }
        hand = hand.toLowerCase();

        int idx = hand.indexOf("$");
        if (idx != -1) {
            String spell = hand.substring(idx+1);
            if (!isSpellValid(spell)) {
                return false;
            }
            hand = hand.substring(0,idx);
        }
        Hand h = Hand.valueOf(hand);
        if (h == null) {
            int foo = 42;
        }
        return h != null;
//        return (hand.equals("left") || hand.equals("right"));
    }
    public static boolean isSpellValid(String slug) {
        Spell s = SpellList.lookupSpellBySlug(slug);
        return s != null;
    }


}
