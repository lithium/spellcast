package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/19/15.
 *
 * Targets with a Death effect are killed after spells resolve but before attacks occur.
 */
public class DeathEffect extends SpellEffect {

	public final static String Name = "death";

	public DeathEffect(String matchId, int roundCast, Target target) {
		super(Name, Spell.SpellType.Damage, matchId, roundCast, target, 0);
	}

	@Override
	public String expire() {
		return null;
	}
}
