package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ControlEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * control enchantment spells cancel each other out if they resolve on the same turn
 */
public class ControlSpell extends Spell {
    protected String effectName;
    protected int duration;

    public ControlSpell(String name, String gestures, String effectName, int duration) {
        this(name, null, gestures, effectName, duration);
    }
    public ControlSpell(String name, String slug, String gestures, String effectName, int duration) {
        super(name, slug, gestures, SpellType.Enchantment);
        this.effectName = effectName;
        this.duration = duration;
    }

    @Override
    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {

        if (!isResolvingMultipleControlSpells(matchState, target)) {
            target.addEffect(new ControlEffect(effectName, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, duration));
        }
    }

    protected boolean isResolvingMultipleControlSpells(SpellcastMatchState matchState, Target target) {
        int controlSpellCount = 0;
        for (ResolvingSpell rs : matchState.getResolvingSpells()) {
            if (rs.getTarget().getNickname().equals(target.getNickname()) && rs.getSpell() instanceof ControlSpell) {
                controlSpellCount += 1;
                if (controlSpellCount > 1) {
                    return true;
                }
            }
        }
        return false;

    }
}
