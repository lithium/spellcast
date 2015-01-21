package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ResistColdEffect;

/**
 * Created by wiggins on 1/20/15.
 */
public class ResistColdSpell extends Spell {
    public ResistColdSpell(String name, String gestures) {
        super(name, gestures, SpellType.Enchantment);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        target.addEffect(new ResistColdEffect(matchState, target));
    }
}
