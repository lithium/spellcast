package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * The subject of this spell is hit by a bolt of lightning and sustains 5 points of damage. Resistance to heat or
 * cold is irrelevant. There are 2 gesture combinations for the spell, but the shorter one may be used only once per day
 * (i.e. per battle) by any wizard. The longer one may be used without restriction. A 'shield' spell offers no defence.

 */
public class LightningBoltSpell extends Spell {

	public LightningBoltSpell(final String name, final String gestures) {
		super(name, gestures, SpellType.Damage);
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		target.takeDamage(5);
	}
}
