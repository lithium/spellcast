package com.hlidskialf.spellcast.server.question;

import com.hlidskialf.spellcast.server.Hand;
import com.hlidskialf.spellcast.server.spell.Spell;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/25/15.
 */
public class HandQuestion extends Question {

    public HandQuestion(String identifier) {
        super(identifier);

        this.options = new ArrayList<String>(2);
        this.options.add(Hand.left.toString());
        this.options.add(Hand.right.toString());
    }

}
