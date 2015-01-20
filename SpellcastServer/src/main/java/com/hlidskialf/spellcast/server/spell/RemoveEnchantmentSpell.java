package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Monster;
import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * If the subject of this spell is currently being effected by any of the spells in the spells in the "enchantments"
 * section, and/or if spells from that section are cast at him at the same time as the remove enchantment, then any
 * such spells terminate immediately although their effect for that turn might already have passed. For example, the
 * victim of a 'blindness' spell would not be able to see what his opponent's gestures were on the turn his sight is
 * restored by a 'remove enchantment'.
 * Note that the 'remove enchantment' affects all spells from the "Enchantment" section whether the caster wants them
 * to all go or not.
 * A second effect of the spell is to destroy any monster upon which it is cast, although the monster can attack in
 * that turn.
 * It does not affect wizards unless cast on a wizard as he creates a monster when the monster is destroyed,
 * and the effects described above apply.

 */
public class RemoveEnchantmentSpell extends Spell {
	public static final String Slug = "removeenchantment";

	public RemoveEnchantmentSpell(final String name, final String gestures) {
		super(name, Slug, gestures, SpellType.Protection);
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {

		target.removeEnchantments();
		for (ResolvingSpell rs : matchState.getResolvingSpells()) {
			if (SpellType.Enchantment.equals(rs.getSpell().getType())) {
				rs.setCountered(true);
			}
		}

		try {
			Monster monster = (Monster)target;
			//target is a monster, dispell it after it attacks
			monster.setDispelled(true);
		} catch (ClassCastException e) {
			// not a monster
		}

	}
}
