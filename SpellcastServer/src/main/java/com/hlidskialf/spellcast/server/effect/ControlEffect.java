package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/21/15.
 */
public class ControlEffect extends SpellEffect {
    public static final String Amnesia = "amnesia";
    public static final String Confusion = "confusion";
    public static final String CharmPerson = "charmperson";
    public static final String CharmMonster = "charmmonster";
    public static final String Paralysis = "paralysis";
    public static final String Fear = "fear";

    public ControlEffect(String name, String matchId, int roundCast, Target target, int duration) {
        super(name, Spell.SpellType.Enchantment, matchId, roundCast, target, duration);
    }

    @Override
    public String expire() {
        return null;
    }
}
