package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/21/15.
 */
public class TimeStopEffect extends SpellEffect {

    private static final String Name = "timestop";

    public TimeStopEffect(String matchId, int roundCast, Target target, int duration) {
        super(Name, Spell.SpellType.Enchantment, matchId, roundCast, target, duration);
    }

    @Override
    public String expire() {
        return null;
    }
}
