package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/19/15.
 */
public class Monster extends Target {
    private final String id;
    private final String name;
    private final int damage;
    private SpellcastClient controller;

    public Monster(String name, int damage, int maxHitpoints) {
        this.id = generateMonsterId();
        this.name = name;
        this.damage = damage;
        this.maxHitpoints = maxHitpoints;
        this.hitpoints = maxHitpoints;
        this.controller = null;
    }


    public void setController(SpellcastClient controller) {
        this.controller = controller;
    }

    public SpellcastClient getController() {
        return controller;
    }

    public String getVisibleId() {
        return controller.getNickname()+"+"+id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public String get311() {
        StringBuilder sb = new StringBuilder("311 ");
        sb.append(getVisibleId());
        sb.append(" ");
        sb.append(getHitpoints());
        sb.append("/");
        sb.append(getMaxHitpoints());
        sb.append(" :");
        sb.append(getName());
        return sb.toString();
    }

    private static int monsterIdSeed = 1000;
    private static String generateMonsterId() {
        String id = "mon"+monsterIdSeed;
        monsterIdSeed += 1;
        return id;

    }

}
