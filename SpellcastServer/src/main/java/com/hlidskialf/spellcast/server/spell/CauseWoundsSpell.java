package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * The subject of this spell is inflicted with 2 points of damage. Resistance to heat or cold offers no defence.
 * A simultaneous 'cure light wounds' will serve only to reduce the damage to 1 point. A 'shield' has no effect.
 *
 */
public class CauseWoundsSpell extends Spell {
	private final int damage;

	public CauseWoundsSpell(final String name, final String gestures, int damage) {
		super(name, gestures, SpellType.Damage);
		this.damage = damage;
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		target.takeDamage(damage);
	}
}
