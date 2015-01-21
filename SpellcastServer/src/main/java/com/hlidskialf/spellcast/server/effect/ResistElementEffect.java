package com.hlidskialf.spellcast.server.effect;

import com.hlidskialf.spellcast.server.Element;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/21/15.
 */
public class ResistElementEffect extends SpellEffect {
    public static final String NamePrefix = "resist";
    private Element element;

    public static final String Heat = "resistfire";
    public static final String Cold = "resistice";

    public static String resistanceFor(Element element) {
        switch (element) {
            case fire: return Heat;
            case ice:  return Cold;
        }
        return null;
    }

    public ResistElementEffect(SpellcastMatchState matchState, Target target, Element element) {
        super(NamePrefix+element, Spell.SpellType.Enchantment, matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, -1);
        this.element = element;
    }

    @Override
    public String expire() {
                         return null;
        }


    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
