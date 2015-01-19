package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/18/15.
 *
 *
 * Waving Hands Rules-
 *
 * This spell protects the subject from all attacks from monsters (that is, creatures created by a summons class spell),
 * from missile spells, and from stabs by wizards.
 * The shield lasts for that turn only, but one shield will cover all such attacks made against the subject that turn.
 */
public class ShieldSpell extends Spell {

    public final static String ShieldEffect = "shield";

    public ShieldSpell(String name, String gestures) {
        super(name, gestures, SpellType.Protection);
    }


    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, SpellcastClient target) {

        SpellEffect effect = new ShieldEffect(matchState, target, 1);
        target.addEffect(effect);
    }
}
