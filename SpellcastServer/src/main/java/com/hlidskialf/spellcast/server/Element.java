package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/21/15.
 */
public enum Element {
    fire, ice;

    public static Element opposing(Element e) {
        switch (e) {
            case fire: return ice;
            case ice: return fire;
        }
        return null;
    }
}
