package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.CounterspellEffect;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/19/15.
 *
 * Waving Hands Rules-
 *
 * Any other spell cast upon the subject in the same turn has no effect whatever. In the case of blanket-type spells,
 * which affect more than 1 person, the subject of the 'counter-spell' alone is protected. For example, a 'fire storm'
 * spell could kill off a monster but not if it was simultaneously the subject of a 'counter-spell' although everyone
 * else would be affected as usual unless they had their own protection. The 'counter-spell' will cancel all the spells
 * cast at the subject for that turn including 'remove enchantment' and 'magic mirror' but not 'dispel magic' or
 * 'finger of death'. It will combine with another spell of its own type for the same effect as if it were alone.
 *
 * The 'counter-spell' will also act as a 'shield' on the final gesture in addition to its other properties, but the
 * shield effect is on the same subject as its other effect. The spell has 2 alternative gesture sequences,
 * either of which may, be used at any time.

 */
public class CounterspellSpell extends Spell {

	public static final String Slug = "counterspell";

	public CounterspellSpell(final String name, final String gestures) {
		super(name, Slug, gestures, SpellType.Protection);
	}

	@Override
	public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
		target.addEffect(new CounterspellEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target));
		target.addEffect(new ShieldEffect(matchState, target, 1));
	}

	@Override
	public void fireSpell(final SpellcastMatchState matchState, final SpellcastClient caster, final Target target) {
		for (ResolvingSpell rs : matchState.getResolvingSpells()) {
			String slug = rs.getSpell().getSlug();
			if (!slug.equals(Slug) &&
				!slug.equals(FingerOfDeathSpell.Slug) &&
				!slug.equals(DispelMagicSpell.Slug) &&
				rs.getTarget().getNickname().equals(target.getNickname()) &&
//				!next.getSpell().getSlug().equals(Surrender.Slug) &&
//				!next.getSpell().getSlug().equals(FireStorm.Slug) &&
//				!next.getSpell().getSlug().equals(IceStorm.Slug) &&
				!rs.isCountered()  &&
				!rs.isFired()) {

				broadcastFizzle(matchState, caster, slug, rs.getTarget(), "Counters the spell");
				rs.setCountered(true);
			}
		}
	}
}
