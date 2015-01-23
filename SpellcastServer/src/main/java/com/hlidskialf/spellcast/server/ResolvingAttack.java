package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.effect.ShieldEffect;
import com.hlidskialf.spellcast.server.spell.Spell;

/**
 * Created by wiggins on 1/20/15.
 */
public class ResolvingAttack {


    private String monsterRef;
    private Target attacker;
    private Target target;
    private int damage;
    private SpellcastClient caster;

    public ResolvingAttack(Target target, int damage) {
        this.target = target;
        this.damage = damage;
    }
    public ResolvingAttack(Target attacker, Target target, int damage) {
        this(target,damage);
        this.attacker = attacker;
    }
    public ResolvingAttack(SpellcastClient caster, String monsterRef, Target target, int damage) {
        this(target, damage);
        this.caster = caster;
        this.monsterRef = monsterRef;
    }

    private void resolveMonsterRef() {
        if (attacker == null && monsterRef != null) {
            for (Monster m : caster.getMonsters()) {
                if (m.getRef().equals(monsterRef)) {
                    attacker = m;
                    return;
                }
            }

        }
    }

    public boolean resolveAttack(SpellcastMatchState matchState) {
        resolveMonsterRef();

        if (target.hasEffect(ShieldEffect.Name)) {
            return false;
        }
        target.takeDamage(damage);
        return true;
    }

    public String get352() {
        StringBuilder sb = new StringBuilder("352 ");
        sb.append(attacker.getNickname());
        sb.append(" ATTACKS ");
        sb.append(target.getNickname());
        return sb.toString();
    }
    public String get353() {
        StringBuilder sb = new StringBuilder("353 ");
        sb.append(target.getNickname());
        sb.append(" BLOCKS ");
        sb.append(attacker.getNickname());
        sb.append(" :the attack is blocked by a shield");
        return sb.toString();
    }

    /*
     * Getters/Setters
     */

    public Target getAttacker() {
        return attacker;
    }

    public void setAttacker(Target attacker) {
        this.attacker = attacker;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
