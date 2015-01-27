package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ControlEffect;
import com.hlidskialf.spellcast.server.question.HandQuestion;
import com.hlidskialf.spellcast.server.question.Question;
import com.hlidskialf.spellcast.server.question.SpellQuestion;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving hands Rules-
 *
 * If the subject of the spell is a wizard, then on the turn the spell is cast, after gestures have been revealed,
 * the caster selects one of the wizard's hands and on the next turn that hand is paralyzed into the position it is in
 * this turn.
 * If the wizard already had a paralyzed hand, it must be the same hand which is paralyzed again.
 * Certain gestures remain the same but if the hand being paralyzed is performing a C, S or W it is instead paralyzed
 * into F, D or P respectively, otherwise it will remain in the position written down (this allows repeated stabs).
 *
 * A favourite ploy is to continually paralyze a hand (F-F-F-F-F-F etc.) into a non-P gesture and then set a monster on
 * the subject so that he has to use his other hand to protect himself, but then has no defence against other magical
 * attacks.
 *
 * If the subject of the spell is a monster (excluding elementals which are unaffected) it simply does not attack in the
 * turn following the one in which the spell was cast.
 *
 * If the subject of the spell is also the subject of any of 'amnesia', 'confusion', 'charm person', 'charm monster' or
 * 'fear', none of the spells work.

 */
public class ParalysisSpell extends ControlSpell {

    public ParalysisSpell(String name, String gestures) {
        super(name, gestures, ControlEffect.Paralysis, 2);
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {

        String targetHand = answers.get("hand");

        return;

    }

    @Override
    public ArrayList<Question> questions(SpellcastMatchState matchState, SpellcastClient caster) {
        ArrayList<Question> ret = new ArrayList<Question>();

        ret.add(new HandQuestion("hand", this));
        return ret;
    }
}
