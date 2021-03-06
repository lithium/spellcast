package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/19/15.
 */
public class Monster extends Target {
    private final int damage;
    private SpellcastClient controller;
	private boolean dispelled;
    private String ref;

    public Monster(String name, int damage, int maxHitpoints) {
        this.nickname = generateMonsterId();
        this.visibleName = name;
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

    public int getDamage() {
        return damage;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String get311() {
        StringBuilder sb = new StringBuilder("311 ");
        sb.append(nickname);
        sb.append(" ");
        sb.append(getController().getNickname());
        sb.append(" ");
        sb.append(getHitpoints());
        sb.append("/");
        sb.append(getMaxHitpoints());
        sb.append(" :");
        sb.append(getVisibleName());
        return sb.toString();
    }

    private static int monsterIdSeed = 1000;
    private static String generateMonsterId() {
        String id = "mon"+monsterIdSeed;
        monsterIdSeed += 1;
        return id;

    }

	public boolean isDispelled() {
		return dispelled;
	}

	public void setDispelled(final boolean dispelled) {
		this.dispelled = dispelled;
	}

    public String get360() {
        StringBuilder sb = new StringBuilder("360 ");
        sb.append(nickname);
        sb.append(" :");
        sb.append(visibleName);
        sb.append(" is dispelled");
        return sb.toString();
    }
}
