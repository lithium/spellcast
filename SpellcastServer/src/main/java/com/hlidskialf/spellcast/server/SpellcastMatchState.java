package com.hlidskialf.spellcast.server;

import com.sun.mirror.apt.RoundState;

import java.util.Collection;

/**
 * Created by wiggins on 1/18/15.
 */
public interface SpellcastMatchState {
    public String getCurrentMatchId();
    public int getCurrentRoundNumber();

    public Collection<SpellcastClient> getAllClients();
    public SpellcastClient getClientByNickname(String nickname);

}
