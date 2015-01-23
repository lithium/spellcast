package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/19/15.
 */
public class ResolvingSpell {
	private Spell spell;
	private SpellcastClient caster;
	private Target target;
	private Hand hand;
	private boolean fired;
	private boolean countered;

	public ResolvingSpell(final Spell spell, final SpellcastClient caster, final Target target, final Hand hand) {
		this.spell = spell;
		this.caster = caster;
		this.target = target;
		this.hand = hand;
		this.fired = false;
		this.fired = countered;
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

	public Hand getHand() {
		return hand;
	}

	public void setHand(final Hand hand) {
		this.hand = hand;
	}

	public boolean isFired() {
		return fired;
	}

	public boolean isCountered() {
		return countered;
	}

	public void setCountered(final boolean countered) {
		this.countered = countered;
	}

	public void setFired(final boolean fired) {
		this.fired = fired;
	}


	public void effects(SpellcastMatchState matchState) {
		spell.effects(matchState, caster, target);
	}
	public void fire(SpellcastMatchState matchState) {
		spell.fireSpell(matchState, caster, target);
		this.fired = true;
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

	public static boolean isSpellResolving(Iterable<ResolvingSpell> resolvingSpells, String spellSlug) {
		for (ResolvingSpell rs : resolvingSpells) {
			if (rs.getSpell().getSlug().equals(spellSlug)) {
				return true;
			}
		}
		return false;
	}
}

