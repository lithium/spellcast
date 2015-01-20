package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * This spell creates a material object of hard substance which is hurled towards the subject of the spell and causes him 1 point of damage.
 * The spell is thwarted by a 'shield' in addition to the usual 'counter-spell', 'dispel magic' and 'magic mirror'
 * (the latter causing it to hit whoever cast it instead).

 */
public class MissileSpell extends Spell {

	public MissileSpell(final String name, final String gestures) {
		super(name, gestures, SpellType.Damage);
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		if (!target.hasEffect(ShieldEffect.Name)) {
			target.takeDamage(1);
		}
	}
}
