package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/18/15.
 */
public class ShieldSpell extends Spell {

    public final static String ShieldEffect = "shield";

    public ShieldSpell(String name, String gestures) {
        super(name, gestures);
    }


    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, SpellcastClient target) {

        SpellEffect effect = new ShieldEffect(matchState, target, 1);
        target.addEffect(effect);
    }
}
