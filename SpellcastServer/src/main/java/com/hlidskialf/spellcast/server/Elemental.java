package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/21/15.
 */
public class Elemental extends Monster {


    private final Element element;

    public Elemental(String name, Element element) {
        super(name, 3, 3);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public static boolean isElemental(Monster monster) {
        return Elemental.nullCast(monster) != null;
    }
    public static Elemental nullCast(Monster monster) {
        try {
            Elemental e = (Elemental)monster;
            return e;
        } catch (ClassCastException e) {

        }
        return null;
    }
}
