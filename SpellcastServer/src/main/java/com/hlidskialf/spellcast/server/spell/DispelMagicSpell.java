package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Monster;
import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * This spell acts as a combination of 'counter-spell' and 'remove enchantment', but its effects are universal rather
 * than limited to the subject of the spell. It will stop any spell cast in the same turn from working (apart from
 * another dispel magic spell which combines with it for the same result), and will remove all enchantments from all
 * beings before they have effect. In addition, all monsters are destroyed although they can attack that turn.
 * 'Counter-spells' and 'magic mirrors' have no effect. The spell will not work on stabs or surrenders.
 * As with a 'counter-spell' it also acts as a 'shield' for its subject.

 */
public class DispelMagicSpell extends Spell {

	public static final String Slug = "dispelmagic";

	public DispelMagicSpell(final String name, final String gestures) {
		super(name, gestures, SpellType.Protection);
	}


	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		for (ResolvingSpell rs : matchState.getResolvingSpells()) {
			if (!rs.getSpell().getSlug().equals(Slug)
//				&& !next.getSpell().getSlug().equals(SurrenderSpell.Slug)
				) {
				rs.setCountered(true);
			}
		}

		//remove enchantments from all beings
		for (Target t: matchState.getAllClients()) {
			t.removeEnchantments();

			//mark all monsters to be destroyed after attacking this round
			try {
				Monster monster = (Monster)t;
				monster.setDispelled(true);
			} catch (ClassCastException e) {  }
		}

		// target gets a shield effect for this round
		target.addEffect(new ShieldEffect(matchState, target, 1));
	}
}
