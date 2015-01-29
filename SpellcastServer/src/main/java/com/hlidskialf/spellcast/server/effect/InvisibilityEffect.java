package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/21/15.
 */
public class InvisibilityEffect extends SpellEffect {

    public static final String Name = "invisibility";

    public InvisibilityEffect(String matchId, int roundCast, Target target, int duration) {
        super(Name, Spell.SpellType.Enchantment, matchId, roundCast, target, duration);
    }

    @Override
    public String expire() {
        return target.getVisibleName()+" is visible again";
    }
}
