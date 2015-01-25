package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.spell.SpellList;
import com.hlidskialf.spellcast.server.spell.SummonMonsterSpell;

/**
 * Created by wiggins on 1/20/15.
 */
public class MonsterQuestion extends Question {
    private Monster monster;
    private String nickname;

    public MonsterQuestion(String nickname) {
        setNickname(nickname);
    }

    public MonsterQuestion(Monster monster) {
        this.monster = monster;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname.trim();
    }

    public String getMonsterNickname() {
        if (nickname != null && !nickname.isEmpty()) {
            return nickname;
        }
        if (monster != null) {
            return monster.getNickname();
        }
        return null;
    }

    public int getDamage() {
        int idx = nickname == null ? -1 : nickname.indexOf("$");
        if (idx != -1) {
            try {
                SummonMonsterSpell summon = (SummonMonsterSpell) SpellList.lookupSpellBySlug(nickname.substring(idx + 1));
                return summon.getDamage();
            } catch (ClassCastException e) {
            }

        }
        if (monster != null) {
            return monster.getDamage();
        }
        return -1;
    }
}
