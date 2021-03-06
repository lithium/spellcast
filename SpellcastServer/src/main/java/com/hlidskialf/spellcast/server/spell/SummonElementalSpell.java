package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;
import com.hlidskialf.spellcast.server.question.Question;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 *
 * This spell creates either a fire elemental or an ice elemental at the discretion of the person upon whom the spell is
 * cast after he has seen all the gestures made that turn.

 * Elementals must be cast at someone and cannot be "shot off" harmlessly at some inanimate object. The elemental will,
 * for that turn and until destroyed, attack everyone who is not resistant to its type (heat or cold), causing 3 points
 * of damage per turn. The elemental takes 3 points of damage to be killed but may be destroyed by spells of the
 * opposite type (e.g. 'fire storm', 'resist cold' or 'fireball' will kill an ice elemental), and will also neutralize
 * the canceling spell.
 *
 * Elementals will not attack on the turn they are destroyed by such a spell. An elemental will also be engulfed and
 * destroyed by a storm of its own type but, in such an event, the storm is not neutralized although the elemental still
 * does not attack in that turn.
 *
 * 2 elementals of the opposite type will also destroy each other before attacking, and 2 of the same type will join
 * together to form a single elemental of normal strength.
 *
 * Note that only wizards or monsters resistant to the type of elemental, or who are casting a spell which has the
 * effect of a 'shield' do not get attacked by the elemental.
 * Casting a 'fireball' upon yourself when being attacked by an ice elemental is no defence! (Cast it at the elemental...)


 */
public class SummonElementalSpell extends SummonMonsterSpell {

    public static final String SummonFireElemental = "summonfireelemental";
    public static final String SummonIceElemental = "summoniceelemental";

    private Element element;

    public SummonElementalSpell(String name, String gestures, Element element) {
        super(name, gestures, 3);
        this.element = element;
    }

    @Override
    public ArrayList<Question> questions(SpellcastMatchState matchState, SpellcastClient caster) {
        return null;
    }

    @Override
    public String generateMonsterName() {
        return Character.toUpperCase(element.toString().charAt(0)) + element.toString().substring(1) + " Elemental";
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        Elemental mob = matchState.getElemental();
        Elemental elemental = null;
        if (mob != null) {
            mob.getController().loseControlOfMonster(mob);
            if (mob.getElement().equals(element)) {
                broadcastFizzle(matchState, caster, mob.getController(), "Elementals of the same type fuse");
                elemental = new Elemental(generateMonsterName(), element);
            } else {
                broadcastFizzle(matchState, caster, mob, "Elementals of opposing types destroy each other");
                broadcastFizzle(matchState, caster, caster, "Elementals of opposing types destroy each other");
            }
        } else {
            elemental = new Elemental(generateMonsterName(), element);
        }

        if (elemental != null) {
            caster.takeControlOfMonster(elemental);
            matchState.getResolvingAttacks().add(new ResolvingAttack(elemental, null, elemental.getDamage()));
        }
    }
}
