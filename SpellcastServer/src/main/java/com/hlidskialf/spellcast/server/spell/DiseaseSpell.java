package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.DiseaseEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hand Rules-
 *
 * The subject of this spell immediately contracts a deadly (non-contagious) disease which will kill him at the end of
 * 6 turns counting from the one upon which the spell is cast.
 * The malady is cured by 'remove enchantment' or 'cure heavy wounds' or 'dispel magic' in the meantime.
 *

 */
public class DiseaseSpell extends Spell {

    public DiseaseSpell(String name, String gestures) {
        super(name, gestures, SpellType.Enchantment);
    }

    @Override
    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        target.addEffect(new DiseaseEffect(matchState, target, 6));
    }
}
