package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Monster;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.DeathEffect;
import com.hlidskialf.spellcast.server.effect.InvisibilityEffect;

import javax.management.monitor.Monitor;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 * This spell is similar to 'blindness' only the subject of the spell becomes invisible to his opponent and his
 * monsters. All spells he creates, though not gestures, can be seen by his opponent and identified.
 * The subject cannot be attacked by any monsters although they can be directed at him in case he becomes visible
 * prematurely. Wizards can still stab and direct spells at him, with the same hope.
 * Any monster made invisible is destroyed due to the unstable nature of such magically created creatures.

 */
public class InvisibilitySpell extends Spell {

    private static final String Slug = "invisibility";

    public InvisibilitySpell(String name, String gestures) {
        super(name, Slug, gestures, SpellType.Enchantment);
    }

    @Override
    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        if (target instanceof Monster) {
            target.addEffect(new DeathEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target));
            broadcastFizzle(matchState, caster, target, "Blindness destroys monsters");
        } else {
            target.addEffect(new InvisibilityEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, 4));
            broadcastSuccess(matchState, caster, target, "is now invisible");
        }
    }
}
