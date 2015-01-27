package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;
import com.hlidskialf.spellcast.server.effect.ControlEffect;
import com.hlidskialf.spellcast.server.effect.GestureEffect;
import com.hlidskialf.spellcast.server.question.GestureQuestion;
import com.hlidskialf.spellcast.server.question.HandQuestion;
import com.hlidskialf.spellcast.server.question.Question;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 * Except for cancellation with other enchantments, this spell only affects humans. The subject is told which of his
 * hands will be controlled at the time the spell hits, and in the following turn, the caster of the spell writes down
 * the gesture he wants the subject's named hand to perform. This could be a stab or nothing.
 * (Some people, myself included do not allow the gesture to be nothing. Makes the game more exciting, as there is no
 * reason to choose any non-nothing gesture when you have the choice.
 * -- B) If the subject is only so because of a reflection from a 'magic mirror' the subject of the mirror assumes the
 * role of caster and writes down his opponent's gesture.
 *
 * If the subject is also the subject of any of 'amnesia', 'confusion', 'charm monster', 'paralysis' or 'fear',
 * none of the spells work.

 */
public class CharmPersonSpell extends ControlSpell {
    public CharmPersonSpell(String name, String gestures) {
        super(name, gestures, ControlEffect.CharmPerson, 2);
    }


    @Override
    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        if (!isResolvingMultipleControlSpells(matchState, target) && target instanceof SpellcastClient) {
            String gesture = answers.get("gesture");
            Hand hand = Hand.valueOf(answers.get("hand"));
            target.addEffect(new GestureEffect(hand, gesture, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, 2));
        }
    }

    @Override
    public ArrayList<Question> questions(SpellcastMatchState matchState, SpellcastClient caster) {
        ArrayList<Question> ret = new ArrayList<Question>();
        ret.add(new HandQuestion("hand"));
        ret.add(new GestureQuestion("gesture"));
        return ret;
    }
}
