package com.hlidskialf.spellcast.server.question;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/26/15.
 */
public class TargetQuestion extends Question {

    public TargetQuestion(SpellcastMatchState matchState) {
        this("target");
        setTargetOptions(matchState);
    }
    public TargetQuestion(String identifier) {
        super(identifier);
    }

    public void setTargetOptions(SpellcastMatchState matchState) {
        options = new ArrayList<String>();
        for (Target t : matchState.getAllTargets()) {
            options.add(t.getNickname());
        }
    }
}
