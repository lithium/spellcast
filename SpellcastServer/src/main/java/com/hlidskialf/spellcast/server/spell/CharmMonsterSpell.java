package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;
import com.hlidskialf.spellcast.server.effect.ControlEffect;
import com.hlidskialf.spellcast.server.question.MonsterQuestion;
import com.hlidskialf.spellcast.server.question.Question;
import com.hlidskialf.spellcast.server.question.TargetQuestion;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Spell-
 *
 * Except for cancellation with other enchantments, this spell only affects monsters (excluding elementals).
 * Control of the monster is transferred to the caster of the spell (or retained by him) as of this turn,
 * i.e. the monster will attack whosoever its new controller dictates from that turn onwards including that turn.
 * Further charms are, of course, possible, transferring as before.
 *
 * If the subject of the charm is also the subject of any of: 'amnesia', 'confusion', 'charm person', 'fear' or
 * 'paralysis', none of the spells work.
 */
public class CharmMonsterSpell extends ControlSpell {

    public static final String Slug = "charmmonster";

    public CharmMonsterSpell(String name, String gestures) {
        super(name, Slug, gestures, ControlEffect.CharmMonster, 0);
    }

    @Override
    public ArrayList<Question> questions(SpellcastMatchState matchState, SpellcastClient caster) {
        ArrayList<Question> ret = new ArrayList<Question>();
        TargetQuestion q = new TargetQuestion(matchState);
        q.setTargetOptions(matchState);
        ret.add(q);
        return ret;
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {

        if (!isResolvingMultipleControlSpells(matchState, target)) {

            if (target instanceof Monster && !(target instanceof Elemental)) {
                Monster monster = (Monster) target;
                caster.takeControlOfMonster(monster);

                ArrayList<ResolvingAttack> resolvingAttacks = matchState.getResolvingAttacks();
                Iterator<ResolvingAttack> attackIterator = resolvingAttacks.iterator();
                while (attackIterator.hasNext()) {
                    ResolvingAttack ra = attackIterator.next();
                    if (ra.getAttacker() != null && ra.getAttacker().getNickname().equals(monster.getNickname())) {
                        //remove any attacks from the older controller
                        attackIterator.remove();
                    }
                }
                Target attackTarget = matchState.getTargetByNickname(answers.get("target"));
                resolvingAttacks.add(new ResolvingAttack(monster, attackTarget, monster.getDamage()));

            }

        }

    }
}

