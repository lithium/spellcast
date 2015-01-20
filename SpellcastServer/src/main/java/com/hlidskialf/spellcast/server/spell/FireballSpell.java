package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * The subject of this spell is hit by a ball of fire and sustains 5 points of damage unless he is resistant to fire.
 * If at the same time an 'ice storm' prevails, the subject of the 'fireball' is instead not harmed by either spell,
 * although the storm will affect others as normal.
 * If directed at an ice elemental, the fireball will destroy it before it can attack, but has no other effect on the
 * creatures.
 *
 */
public class FireballSpell extends Spell {
	public FireballSpell(final String name, final String gestures) {
		super(name, gestures, SpellType.Damage);
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {

	}
}
