package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Element;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.ResistElementEffect;

/**
 * Created by wiggins on 1/20/15.
 *
 * Waving Hands Rules-
 *
 * Resist Heat-
 * The subject of this spell becomes totally resistant to all forms of heat attack ('fireball', 'fire storm' and
 * fire elementals). Only 'dispel magic' or 'remove enchantment' will terminate this resistance once started (although
 * a 'counter-spell' will prevent it from working if cast at the subject at the same time as this spell). A
 * 'resist heat' cast directly on a fire elemental will destroy it before it can attack that turn, but there is no
 * effect on ice elementals.

 * Resist Cold-
 * The effects of this spell are identical to 'resist heat' but resistance is to cold ('ice storm' and ice elementals)
 * and it destroys ice elementals if they are the subject of the spell but doesn't affect fire elementals.

 */
public class ResistElementSpell extends Spell {
    private Element element;

    public ResistElementSpell(String name, String gestures, Element element) {
        super(name, gestures, SpellType.Enchantment);
        this.element = element;
    }

    @Override
    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        target.addEffect(new ResistElementEffect(matchState, target, element));
    }
}
