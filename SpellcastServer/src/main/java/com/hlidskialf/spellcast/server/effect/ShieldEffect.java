package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellEffect;
import com.hlidskialf.spellcast.server.SpellcastMatchState;

/**
 * Created by wiggins on 1/18/15.
 */
public class ShieldEffect extends SpellEffect {

    public static final String name = "shield";

    public ShieldEffect(SpellcastMatchState matchState, int duration) {
        super(name, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), duration);
    }
}
