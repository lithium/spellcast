package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 * On the turn following the casting of this spell, the subject cannot include any gestures made on or before this
 * turn in a spell sequence and must restart a new spell from the beginning of that spell sequence.
 * The spell does not affect spells which are cast on the same turn nor does it affect monsters.
 */
public class AntispellSpell extends Spell {
    private static final String Slug = "antispell";

    public AntispellSpell(String name, String gestures) {
        super(name, Slug, gestures, SpellType.Enchantment);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        if (target instanceof SpellcastClient) {
            SpellcastClient wizard = (SpellcastClient)target;
            wizard.addGestures("*","*");
        }
    }
}
