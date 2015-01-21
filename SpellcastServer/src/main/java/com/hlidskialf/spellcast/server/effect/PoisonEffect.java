package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/21/15.
 */
public class PoisonEffect extends SpellEffect {

    private static final String Name = "poison";

    public PoisonEffect(SpellcastMatchState matchState, Target target, int duration) {
        super(Name, Spell.SpellType.Enchantment, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, duration);
    }

    @Override
    public String expire() {
        target.addEffect(new DeathEffect(matchId, roundCast+duration, target));
        return target.getVisibleName()+" clutches their throat and gasping dies from poison";
    }
}
