package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.ResolvingSpell;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ResistColdEffect;

/**
 * Created by wiggins on 1/20/15.
 *
 * Waving Hands Rules-
 * Everything not resistant to cold sustains 5 points of damage that turn. The spell cancels wholly, causing no damage,
 * with either a 'fire storm' or a fire elemental, and will cancel locally with a 'fireball'.
 * It will destroy but not be destroyed by an ice elemental. Two 'ice storms' act as one.


 */
public class IceStormSpell extends Spell {
    public static final String Slug = "icestorm";

    public IceStormSpell(String name, String gestures) {
        super(name, Slug, gestures, SpellType.Damage);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {

        if (ResolvingSpell.isSpellResolving(matchState.getResolvingSpells(), FireStormSpell.Slug)) {
            // TODO: or there is an fire elemental
            return;
        }

        //TODO: if there is a ice elemental destroy it

        for (Target t : matchState.getAllTargets()) {
            if (!t.hasEffect(ResistColdEffect.Name)) {
                t.takeDamage(5);
            }
        }

    }
}
