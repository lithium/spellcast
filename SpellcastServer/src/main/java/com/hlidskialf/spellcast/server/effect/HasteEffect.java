package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/21/15.
 */
public class HasteEffect extends SpellEffect {
    private static final String Name = "haste";

    public HasteEffect(String matchId, int roundCast, Target target, int duration) {
        super(Name, Spell.SpellType.Enchantment, matchId, roundCast, target, duration);
    }

    @Override
    public String expire() {
        return null;
    }
}
