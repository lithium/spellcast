package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/22/15.
 */
public class Tombstone {
    private final String nickname;
    private final String matchId;
    private final int deathRound;
    private SpellcastClient client;

    public Tombstone(SpellcastClient client, String matchId, int deathRound) {
        this.client = client;
        this.nickname = client.getNickname();
        this.deathRound = deathRound;
        this.matchId = matchId;
    }

    public SpellcastClient getClient() {
        return client;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMatchId() {
        return matchId;
    }

    public int getDeathRound() {
        return deathRound;
    }
}
