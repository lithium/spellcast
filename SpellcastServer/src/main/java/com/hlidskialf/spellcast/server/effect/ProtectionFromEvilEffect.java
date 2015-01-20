package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/19/15.
 */
public class ProtectionFromEvilEffect extends SpellEffect {

	public ProtectionFromEvilEffect(SpellcastMatchState matchState, Target target, int duration) {
		super(ShieldEffect.Name, Spell.SpellType.Enchantment, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, duration);
	}

	@Override
	public String expire() {
		return "The shield dissipates from around "+target.getNickname()+".";
	}

}
