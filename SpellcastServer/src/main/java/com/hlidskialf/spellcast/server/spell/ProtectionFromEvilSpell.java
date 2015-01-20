package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ProtectionFromEvilEffect;

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
		target.addEffect(new ProtectionFromEvilEffect(matchState, target, 4));
    }
}
