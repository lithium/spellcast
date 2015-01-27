package com.hlidskialf.spellcast.server.question;

import com.hlidskialf.spellcast.server.ValidationHelper;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/26/15.
 */
public class GestureQuestion extends Question {

    public GestureQuestion(String identifier) {
        super(identifier);

        this.options = new ArrayList<String>(2);
        this.options.add("C");
        this.options.add("D");
        this.options.add("F");
        this.options.add("P");
        this.options.add("S");
        this.options.add("W");
        this.options.add("K");
    }
}
