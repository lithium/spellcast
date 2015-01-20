package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;
import com.hlidskialf.spellcast.server.effect.SpellEffect;

/*
 * Created by wiggins on 1/18/15.
 *
 * Waving Hands Rules-
 *
 * For this turn and the following 3 turns the subject of this spell is protected as if using a 'shield' spell,
 * thus leaving both hands free. Concurrent 'shield' spells offer no further protection and
 * compound 'protection from evil' spells merely overlap offering no extra cover.
 */
public class ProtectionFromEvilSpell extends Spell {

    public ProtectionFromEvilSpell(String name, String gestures) {
        super(name, gestures, SpellType.Enchantment);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        SpellEffect effect = new ShieldEffect(matchState, target, 4);
        target.addEffect(effect);
    }
}
