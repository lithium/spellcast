package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.DiseaseEffect;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * Cure Light-
 * If the subject has received damage then he is cured by 1 point as if that point had not been inflicted.
 * Thus, for example, if a wizard was at 10 points of damage and was hit simultaneously by a 'cure light wounds'
 * and a 'lightning bolt' he would finish that turn on 14 points rather than 15 (or 9 if there had been no 'lightning bolt').
 * The effect is not removed by a 'dispel magic' or 'remove enchantment'.
 *
 * Cure Heavy-
 * This spell is the same as 'cure light wounds' for its effect, but 2 points of damage are cured instead of 1, or only 1 if only 1 had been sustained.
 * A side effect is that the spell will also cure a disease (note 'raise dead' on a live individual won't).
 */
public class CureWoundsSpell extends Spell {
	private final int healAmount;

	public CureWoundsSpell(final String name, final String gestures, int healAmount) {
		super(name, gestures, SpellType.Protection);
		this.healAmount = healAmount;
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		target.healDamage(healAmount);

		if (healAmount == 2) {
			// Cure Heavy Wounds
			if (target.hasEffect(DiseaseEffect.Name)) {
				target.removeEffect(DiseaseEffect.Name);
			}
		}
	}
}
