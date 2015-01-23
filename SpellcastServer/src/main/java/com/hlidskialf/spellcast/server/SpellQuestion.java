package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/12/15.
 */
public class SpellQuestion extends Question {
    private Spell spell;

    public SpellQuestion(Spell spell) {
        this.spell = spell;
    }

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    @Override
    public boolean hasTarget() {
        if (!spell.hasTarget()) {
            return true;
        }
        return super.hasTarget();
    }
}
