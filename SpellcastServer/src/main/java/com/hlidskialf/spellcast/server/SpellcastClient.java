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
    private boolean readyToPlay;

    public SpellcastClient(Object channel) {
        this.channel = channel;
        this.state = ClientState.WaitingForName;
        this.readyToPlay = false;
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
        return gender != null ? gender : GenderNone;
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

    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
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
        if (state == ClientState.Identified && readyToPlay) {
            return "+";
        }
        return "-";
    }

    public String get301() {
        return "301 "+nickname+" "+getHitpointString()+" "+getGender()+" :"+getVisibleName();
    }

    public boolean canGestureThisRound(int roundNumber) {
        // TODO: return false if under some effect, or if roundNumber is even and this user is not hasted
        return true;
    }
}
