package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/28/15.
 */
public class DelayedEffect extends SpellEffect {

    public final static String Name = "delayedeffect";

    public DelayedEffect(String matchId, int roundCast, Target target) {
        super(Name, Spell.SpellType.Enchantment, matchId, roundCast, target, 4);
    }

    @Override
    public String expire() {
        return null;
    }
}
