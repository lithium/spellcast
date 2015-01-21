package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/20/15.
 */
public class ResistHeatEffect extends SpellEffect {
    public final static String Name = "resistheat";

    public ResistHeatEffect(SpellcastMatchState matchState, Target target) {
        super(Name, Spell.SpellType.Enchantment, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, -1);
    }

    @Override
    public String expire() {
        return null;
    }
}
