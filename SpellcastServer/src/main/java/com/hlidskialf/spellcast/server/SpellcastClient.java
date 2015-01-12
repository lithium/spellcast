package com.hlidskialf.spellcast.server;


/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastClient {

    public enum ClientState {
        WaitingForName,
        Identified,
        Playing
    };

    private static final String GenderFemale = "female";
    private static final String GenderMale = "male";
    private static final String GenderNone = "none";

    private Object channel;
    private ClientState state;
    private String nickname;
    private String visibleName;
    private String gender;
    private int hitpoints;

    public SpellcastClient(Object channel) {
        this.channel = channel;
        this.state = ClientState.WaitingForName;
    }


    /*
     * Getters/setters
     */
    public Object getChannel() {
        return channel;
    }
    public void setChannel(Object channel) {
        this.channel = channel;
    }
    public ClientState getState() {
        return state;
    }
    public void setState(ClientState state) {
        this.state = state;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getVisibleName() {
        return visibleName == null ? nickname : visibleName;
    }
    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        if (gender.toLowerCase().equals(GenderFemale)) {
            this.gender = GenderFemale;
        } else
        if (gender.toLowerCase().equals(GenderMale)) {
            this.gender = GenderMale;
        } else {
            this.gender = GenderNone;
        }

    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public int takeDamage(int damage) {
        hitpoints -= damage;
        return hitpoints;
    }
    public int healDamage(int damage) {
        hitpoints += damage;
        return hitpoints;
    }
    public String getHitpointString() {
        if (state == ClientState.Playing) {
            return String.valueOf(hitpoints);
        }
        return "-";
    }

    public String get301() {
        return "301 "+nickname+" "+getHitpointString()+" "+gender+" :"+getVisibleName();
    }
}
