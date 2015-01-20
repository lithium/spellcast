package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.DeathEffect;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * Kills the subject stone dead. This spell is so powerful that it is unaffected by a 'counter-spell' although a
 * 'dispel magic' spell cast upon the final gesture will stop it. The usual way to prevent being harmed by this spell
 * is to disrupt it during casting using, for example, an 'anti-spell'.
 *
 */
public class FingerOfDeathSpell extends Spell {
	public FingerOfDeathSpell(final String name, final String gestures) {
		super(name, gestures, SpellType.Damage);
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		target.addEffect(new DeathEffect(matchState, target));
	}
}
