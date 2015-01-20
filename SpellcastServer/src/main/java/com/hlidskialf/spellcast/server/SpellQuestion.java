package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/12/15.
 */
public class SpellQuestion {

    private Spell spell;
    private String target;

    public SpellQuestion(Spell spell) {
        this.spell = spell;
    }

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean hasTarget() {
        return (this.target != null && this.target.length() > 0);
    }
}
