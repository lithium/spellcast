package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Element;
import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.DeathEffect;
import com.hlidskialf.spellcast.server.effect.ResistElementEffect;

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

	public static final String Slug = "fireball";

	public FireballSpell(final String name, final String gestures) {
		super(name, Slug, gestures, SpellType.Damage);
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {


		if (!ResolvingSpell.isSpellResolving(matchState.getResolvingSpells(), ElementStormSpell.IceStormSlug)) {

			if (matchState.getElemental() != null &&
				target.getNickname().equals(matchState.getElemental().getNickname()) &&
				matchState.getElemental().getElement().equals(Element.ice)
				) {
				target.addEffect( new DeathEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target) );
			}
			else
            if (!target.hasEffect(ResistElementEffect.Heat)) {
                target.takeDamage(5);
            } else {
				broadcastFizzle(matchState, caster, target, "resists the fireball's heat");
			}

		}




	}
}
