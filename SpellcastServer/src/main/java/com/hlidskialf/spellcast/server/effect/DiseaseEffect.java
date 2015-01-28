package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/21/15.
 */
public class DiseaseEffect extends SpellEffect {

    public static final String Name = "disease";

    public DiseaseEffect(SpellcastMatchState matchState, Target target, int duration) {
        super(Name, Spell.SpellType.Enchantment, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, duration);

    }

    @Override
    public String expire() {
        target.addEffect(new DeathEffect(matchId, roundCast+duration, target));
        return target.getVisibleName()+" keels over and dies from a disease";
    }
}
