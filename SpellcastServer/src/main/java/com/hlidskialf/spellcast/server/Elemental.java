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

}
