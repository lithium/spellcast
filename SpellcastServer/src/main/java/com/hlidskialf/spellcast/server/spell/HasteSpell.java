package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.HasteEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 *
 * For the next 3 turns, the subject (but not his monsters if a wizard) makes an extra set of gestures due to being
 * sped up. This takes effect in the following turn so that instead of giving one pair of gestures, 2 are given, the
 * effect of both being taken simultaneously at the end of the turn.
 * Thus a single 'counter-spell' from his adversary could cancel 2 spells cast by the hastened wizard on 2 half-turns
 * if the phasing is right. Non-hastened wizards and monsters can see everything the hastened individual is doing.
 * Hastened monsters can change target in the extra turns if desired.

 */
public class HasteSpell extends Spell {

    private static final String Slug = "haste";


    public HasteSpell(String name, String gestures) {
        super(name, Slug, gestures, SpellType.Enchantment);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        target.addEffect(new HasteEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, 4));
    }
}
