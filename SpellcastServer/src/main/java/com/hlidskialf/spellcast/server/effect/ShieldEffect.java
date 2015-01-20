package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellEffect;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/18/15.
 */
public class ShieldEffect extends SpellEffect {

    public static final String name = "shield";

    public ShieldEffect(SpellcastMatchState matchState, Target target, int duration) {
        super(name, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, duration);
    }

    @Override
    public String expire() {
        return "The shield dissapates from around "+target.getNickname()+".";
    }
}
