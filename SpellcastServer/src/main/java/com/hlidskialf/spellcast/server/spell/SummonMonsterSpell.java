package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;

/**
 * Created by wiggins on 1/19/15.
 *
 *
 *
 * Waving Hands Rules-
 *
 * This spell creates a goblin under the control of the subject upon whom the spell is cast
 * (or if cast on a monster, *its* controller, even if the monster later dies or changes loyalty).
 * The goblin can attack immediately and its victim can be any any wizard or other monster the controller desires,
 * stating which at the time he writes his gestures.
 * It does 1 point of damage to its victim per turn and is destroyed after 1 point of damage is inflicted upon it.
 * The summoning spell cannot be cast at an elemental, and if cast at something which doesn't exist, the spell has no effect.
 */



public class SummonMonsterSpell extends Spell {

    private int damage;
    private String monsterRef;

    public SummonMonsterSpell(String name, String gestures, int damage) {
        super(name, gestures, SpellType.Summon);
        this.damage = damage;
    }

    public String generateMonsterName() {
        StringBuilder sb = new StringBuilder("Joe the ");
        switch(damage) {
            case 1: sb.append("Goblin"); break;
            case 2: sb.append("Ogre"); break;
            case 3: sb.append("Troll"); break;
            case 4: sb.append("Giant"); break;
        }
        return sb.toString();
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        Monster monster = new Monster(generateMonsterName(), damage, damage);
        monster.setRef(monsterRef);
        caster.takeControlOfMonster(monster);
        matchState.broadcast(monster.get311());

        if (monsterRef != null) {
            for (MonsterQuestion mq : caster.getMonsterQuestions()) {
                if (mq.getNickname().equals(monsterRef)) {
                    mq.setMonster(monster);
                }
            }
        }
    }

    public int getDamage() {
        return damage;
    }

    public String getMonsterRef() {
        return monsterRef;
    }

    public void setMonsterRef(String monsterRef) {
        this.monsterRef = monsterRef;
    }
}
