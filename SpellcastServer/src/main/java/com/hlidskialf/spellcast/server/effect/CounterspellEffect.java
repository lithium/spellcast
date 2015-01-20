package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/19/15.
 */
public class CounterspellEffect extends SpellEffect {

	public static final String Name = "counterspell";

	public CounterspellEffect(final String matchId, final int roundCast, final Target target) {
		super(Name, Spell.SpellType.Protection, matchId, roundCast, target, 1);
	}

	@Override
	public String expire() {
		return null;
	}
}
