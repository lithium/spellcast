package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/19/15.
 */
public class DeathEffect extends SpellEffect {

	public final static String Name = "death";

	public DeathEffect(SpellcastMatchState matchState, Target target) {
		super(Name, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, 0);
	}

	@Override
	public String expire() {
		return null;
	}
}
