package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Spell;
import com.hlidskialf.spellcast.server.SpellEffect;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/18/15.
 */
public class ProtectionFromEvilSpell extends Spell {

    public ProtectionFromEvilSpell(String name, String gestures) {
        super(name, gestures);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, SpellcastClient target) {
        SpellEffect effect = new ShieldEffect(matchState, target, 3);
        target.addEffect(effect);
    }
}
