package com.hlidskialf.spellcast.swing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wiggins on 1/15/15.
 */
public class Player {
    private String nickname;
    private String visibleName;
    private String gender;
    private int maxHP;
    private int currentHP;
    private Map<String,Player> monsters;

    public Player(String nickname) {
        this(nickname,0);
    }
    public Player(String nickname, int maxHP) {
        this.nickname = nickname;
        this.visibleName = nickname;
        this.maxHP = maxHP;
        this.currentHP = this.maxHP;
        monsters = new HashMap<String, Player>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return visibleName;
    }

    public void setName(String name) {
        this.visibleName = name;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<String, Player> getMonsters() {
        return monsters;
    }

    public void addMonster(Player monster) {
        monsters.put(monster.getNickname(), monster);
    }
    public void removeMonster(Player monster) {
        monsters.remove(monster.getNickname());
    }
}
