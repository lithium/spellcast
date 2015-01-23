package com.hlidskialf.spellcast.server;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by wiggins on 1/18/15.
 */
public interface SpellcastMatchState {
    public String getCurrentMatchId();
    public int getCurrentRoundNumber();

	public ArrayList<ResolvingSpell> getResolvingSpells();
    public Iterable<Target> getAllTargets();

    void broadcast(String monster311);
}
