package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/18/15.
 */
public class ShieldEffect extends SpellEffect {

	public final static String Name = "shield";

	public ShieldEffect(SpellcastMatchState matchState, Target target, int duration) {
        super(Name, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, duration);
    }

    @Override
    public String expire() {
        return "The shield dissapates from around "+target.getNickname()+".";
    }
}
