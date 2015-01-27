package com.hlidskialf.spellcast.server;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/18/15.
 */
public interface SpellcastMatchState {
    public String getCurrentMatchId();
    public int getCurrentRoundNumber();

	public ArrayList<ResolvingSpell> getResolvingSpells();
    public ArrayList<ResolvingAttack> getResolvingAttacks();
    public Iterable<Target> getAllTargets();

    public Target getTargetByNickname(String nick);

    public void broadcast(String monster311);

    public Elemental getElemental();
}
