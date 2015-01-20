package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/19/15.
 */
public class ResolvingSpell {
	private Spell spell;
	private SpellcastClient caster;
	private Target target;
	private String hand;

	public ResolvingSpell(final Spell spell, final SpellcastClient caster, final Target target, final String hand) {
		this.spell = spell;
		this.caster = caster;
		this.target = target;
		this.hand = hand;
	}

	public Spell getSpell() {
		return spell;
	}

	public void setSpell(final Spell spell) {
		this.spell = spell;
	}

	public SpellcastClient getCaster() {
		return caster;
	}

	public void setCaster(final SpellcastClient caster) {
		this.caster = caster;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(final Target target) {
		this.target = target;
	}

	public String getHand() {
		return hand;
	}

	public void setHand(final String hand) {
		this.hand = hand;
	}

	public void fire(SpellcastMatchState matchState) {
		spell.fireSpell(matchState, caster, target);
	}

	public String get351() {
		StringBuilder sb = new StringBuilder("351 ");
		sb.append(caster.getNickname());
		sb.append(" CASTS ");
		sb.append(spell.getSlug());
		sb.append(" AT ");
		sb.append(target.getNickname());
		sb.append(" WITH ");
		sb.append(hand);
		return sb.toString();
	}
}

