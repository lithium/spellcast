package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Hand;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/26/15.
 */
public class GestureEffect extends ControlEffect {

    private String gesture;
    private Hand hand;

    public GestureEffect(Hand hand, String matchId, int roundCast, Target target, int duration) {
        super(ControlEffect.CharmPerson, matchId, roundCast, target, duration);
        this.hand = hand;
        this.gesture = null;
    }
    public GestureEffect(Hand hand, String gesture, String matchId, int roundCast, Target target, int duration) {
        super(ControlEffect.CharmPerson, matchId, roundCast, target, duration);
        this.hand = hand;
        this.gesture = gesture;
    }

    @Override
    public String expire() {
        return null;
    }

    public String getGesture() {
        return gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }
}
