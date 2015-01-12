package com.hlidskialf.spellcast.server.base;


/**
 * Created by wiggins on 1/11/15.
 */
public abstract class SpellcastClient {

    public enum ClientState {
        WaitingForName,
        Identified,
        Playing
    };

    private ClientState state;
    private String nickname;
    private String visibleName;
    private String gender;

    public SpellcastClient() {
        this.state = ClientState.WaitingForName;
    }

    abstract public void send(String message);



    /*
     * Getters/setters
     */
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
        return visibleName;
    }
    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }



}
