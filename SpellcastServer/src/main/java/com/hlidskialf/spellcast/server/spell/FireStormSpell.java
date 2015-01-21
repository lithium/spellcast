package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ResistElementEffect;

/**
 * Created by wiggins on 1/20/15.
 *
 * Waving Hands Rules-
 * Everything not resistant to heat sustains 5 points of damage that turn. The spell cancels wholly, causing no damage,
 * with either an 'ice storm' or an ice elemental. It will destroy but not be destroyed by a fire elemental.
 * Two 'fire storms' act as one.
 *
 */
public class FireStormSpell extends Spell {
    public static final String Slug = "firestorm";

    public FireStormSpell(String name, String gestures) {
        super(name, Slug, gestures, SpellType.Damage);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {

        if (ResolvingSpell.isSpellResolving(matchState.getResolvingSpells(), IceStormSpell.Slug)) {
            // TODO: or there is an ice elemental
            return;
        }

        //TODO: if there is a fire elemental destroy it

        for (Target t : matchState.getAllTargets()) {
            if (!t.hasEffect(ResistElementEffect.Heat)) {
                t.takeDamage(5);
            }
        }

    }
}
