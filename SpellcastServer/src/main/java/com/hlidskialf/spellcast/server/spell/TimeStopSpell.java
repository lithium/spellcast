package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.TimeStopEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 * The subject of this spell immediately takes an extra turn, on which no-one can see or know about unless they are
 * harmed. All non-affected beings have no resistance to any form of attack, e.g. a wizard halfway through the
 * duration of a 'protection from evil' spell can be harmed by a monster which has had its time stopped.
 * Time-stopped monsters attack whoever their controller instructs, and time-stopped elementals affect everyone,
 * resistance to heat or cold being immaterial in that turn.

 */
public class TimeStopSpell extends Spell {
    private static final String Slug = "timestop";

    public TimeStopSpell(String name, String gestures) {
        super(name, Slug, gestures, SpellType.Enchantment);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        target.addEffect(new TimeStopEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, 1));
    }
}
