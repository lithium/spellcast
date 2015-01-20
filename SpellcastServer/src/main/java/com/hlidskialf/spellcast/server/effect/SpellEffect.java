package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/18/15.
 */
abstract public class SpellEffect {
    protected String name;
	private final Spell.SpellType spellType;
	protected String matchId;
    protected int roundCast;
    protected Target target;
    protected int duration;

    public SpellEffect(String name, Spell.SpellType spellType, String matchId, int roundCast, Target target, int duration) {
        this.name = name;
	    this.spellType = spellType;
	    this.matchId = matchId;
        this.roundCast = roundCast;
        this.target = target;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getRoundCast() {
        return roundCast;
    }

    public void setRoundCast(int roundCast) {
        this.roundCast = roundCast;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

	public Spell.SpellType getSpellType() {
		return spellType;
	}

	abstract public String expire();


}
