package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * Any spell cast at the subject of this spell is reflected back at the caster of that spell for that turn only.
 * This includes spells like 'missile' and 'lightning bolt' but does not include attacks by monsters already in
 * existence, or stabs from wizards. Note that certain spells are cast by wizards usually upon themselves,
 * e.g. spells from this section and the "Summons" section, in which case the mirror would have no effect.
 *
 * It is countered totally, with no effect whatsoever, if the subject is the simultaneous subject of either a
 * 'counter-spell' or 'dispel magic'. It has no effect on spells which affect more than one person, such as
 * 'fire storm' and 2 mirrors cast at someone simultaneously combine to form a single mirror.

 */
public class MagicMirrorSpell extends Spell {
	public static final String Slug = "magicmirror";

	public MagicMirrorSpell(final String name, final String gestures) {
		super(name, Slug, gestures, SpellType.Protection);
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		for (ResolvingSpell rs : matchState.getResolvingSpells()) {
			if (!rs.getSpell().getSlug().equals(Slug) && rs.getTarget().getNickname().equals(target.getNickname())) {
				rs.setTarget(rs.getCaster());
				broadcastFizzle(matchState, target, rs.getCaster(), "Magic mirror reflects the spell");
			}
		}

	}
}
