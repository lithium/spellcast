package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/11/15.
 */
public class Spell {
    private final String name;
    private final String gestures;
    private final String reverse;

    public Spell(String name, String gestures) {
        this.name = name;
        this.gestures = gestures;
        this.reverse = new StringBuilder(gestures).reverse().toString();
    }

    public String getName() {
        return name;
    }

    public String getGestures() {
        return gestures;
    }

    public String getReverseGestures() {
        return reverse;
    }
}
