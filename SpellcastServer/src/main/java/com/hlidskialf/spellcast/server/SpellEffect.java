package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/18/15.
 */
abstract public class SpellEffect {
    protected String name;
    protected String matchId;
    protected int roundCast;
    protected SpellcastClient target;
    protected int duration;

    public SpellEffect(String name, String matchId, int roundCast, SpellcastClient target, int duration) {
        this.name = name;
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

    public SpellcastClient getTarget() {
        return target;
    }

    public void setTarget(SpellcastClient target) {
        this.target = target;
    }

    abstract public String expire();
}
