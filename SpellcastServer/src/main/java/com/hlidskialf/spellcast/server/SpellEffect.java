package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/18/15.
 */
public class SpellEffect {
    private String name;
    private String matchId;
    private int roundCast;
    private int duration;

    public SpellEffect(String name, String matchId, int roundCast, int duration) {
        this.name = name;
        this.matchId = matchId;
        this.roundCast = roundCast;
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
}
