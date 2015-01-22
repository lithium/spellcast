package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.effect.ControlEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 *
 * If the subject of this spell is a wizard, next turn he must repeat identically the gestures he made in the current
 * turn, including stabs. If the subject is a monster it will attack whoever it attacked this turn. If the subject is
 * simultaneously the subject of any of 'confusion', 'charm person', 'charm monster', 'paralysis' or 'fear' then none
 * of the spells work.
 */
public class AmnesiaSpell extends ControlSpell {
    public AmnesiaSpell(String name, String gestures) {
        super(name, gestures, ControlEffect.Amnesia, 2);
    }
}
