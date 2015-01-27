package com.hlidskialf.spellcast.server.question;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.spell.Spell;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wiggins on 1/12/15.
 */
public class SpellQuestion extends Question {
    private Spell spell;
    private ArrayList<Question> subQuestions;
    private boolean doneSpellQuestions;

    public SpellQuestion(Spell spell) {
        this(null, spell);
    }
    public SpellQuestion(String identifier, Spell spell) {
        super(identifier != null ? identifier : spell.getSlug());
        this.spell = spell;
    }

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
        setIdentifier(spell.getSlug());
    }

    @Override
    public boolean hasAnswer() {
        if (!spell.hasTarget()) {
            return true;
        }
        return super.hasAnswer();
    }

    public void doSpellQuestions(SpellcastMatchState matchState, SpellcastClient caster) {
        if (!doneSpellQuestions) {
            subQuestions = spell.questions(matchState, caster);
            doneSpellQuestions = true;
        }
    }

    public boolean hasUnansweredSubQuestions() {
        if (subQuestions != null) {
            for (Question q : subQuestions) {
                if (!q.hasAnswer())
                    return true;
            }
        }
        return false;
    }

    public ArrayList<Question> getSubQuestions() {
        return subQuestions;
    }
}
