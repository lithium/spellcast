package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.PoisonEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 *
 * This is the same as the disease spell except that 'cure heavy wounds' does not stop its effects.
 */
public class PoisonSpell extends Spell {
    public PoisonSpell(String name, String gestures) {
        super(name, gestures, SpellType.Enchantment);
    }

    @Override
    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
       target.addEffect(new PoisonEffect(matchState, target, 6));
    }
}
