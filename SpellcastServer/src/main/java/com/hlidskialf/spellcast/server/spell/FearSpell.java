package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.effect.ControlEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 *
 * In the turn following the casting of this spell, the subject cannot perform a C, D, F or S gesture.
 * This obviously has no effect on monsters.
 * If the subject is also the subject of 'amnesia', 'confusion', 'charm person', 'charm monster' or 'paralysis', then
 * none of the spells work.

 */
public class FearSpell extends ControlSpell {

    public FearSpell(String name, String gestures) {
        super(name, gestures, ControlEffect.Fear, 2);
    }
}
