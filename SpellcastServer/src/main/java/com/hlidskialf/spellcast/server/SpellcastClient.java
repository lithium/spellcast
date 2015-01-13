package com.hlidskialf.spellcast.server;


import java.util.ArrayList;

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
    private boolean ready;
    private String leftGesture, rightGesture;
    private ArrayList<String> leftGestures, rightGestures;


    public SpellcastClient(Object channel) {
        this.channel = channel;
        this.state = ClientState.WaitingForName;
        this.ready = false;
        this.leftGestures = new ArrayList<String>();
        this.rightGestures = new ArrayList<String>();
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
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
        if (state == ClientState.Identified && ready) {
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

    public void readyGestures(String left, String right) {
        //lowercase the gesture if its done by both hands simultaneously
        left = left.toUpperCase();
        right = right.toUpperCase();
        if (left.equals(right)) {
            left = left.toLowerCase();
            right = left;
        }
        leftGesture = left;
        rightGesture = right;
        ready = true;
    }

    public String getRightGesture() {
        return rightGesture;
    }
    public String getLeftGesture() {
        return leftGesture;
    }

    public void performGestures() {
        if (leftGesture == null || rightGesture == null) {
            return;
        }


        leftGestures.add(leftGesture);
        rightGestures.add(rightGesture);
        leftGesture = null;
        rightGesture = null;


        String leftHistory = gestureHistory(leftGestures);
        String rightHistory = gestureHistory(rightGestures);

        ArrayList<Spell> leftSpells = findMatchingSpells(leftHistory);
        ArrayList<Spell> rightSpells = findMatchingSpells(rightHistory);

    }

    private ArrayList<Spell> findMatchingSpells(String history) {
        ArrayList<Spell> ret = new ArrayList<Spell>();

        for (Spell spell : SpellList.AllSpells) {
            if (spellMatches(history, spell)) {
                ret.add(spell);
            }
        }

        return ret;
    }

    private boolean spellMatches(String history, Spell spell) {
        String reverse = spell.getReverseGestures();
        int l = reverse.length();
        int i;
        if (history.length() < l)
            return false;
        for (i = 0; i < l; i++) {
            char h = history.charAt(i);
            char s = reverse.charAt(i);

            // if spell has a lowercase character it must match a lowercase character in history
            // an uppercase character in a spell matches upper or lower in history

            boolean matches = false;
            if (Character.isLowerCase(s)) {
                matches = (h == s);
            } else {
                matches = (Character.toUpperCase(h) == s);
            }
            if (!matches) {
                return false;
            }
        }
        return true;
    }

    private String gestureHistory(ArrayList<String> gestures ) {
        // max spell length is 8
        return gestureHistory(gestures, 8);
    }
    private String gestureHistory(ArrayList<String> gestures, int howMany) {
        howMany = Math.min(gestures.size(), howMany);
        StringBuffer sb = new StringBuffer(howMany);
        int i=0;
        for (String g : gestures) {
            sb.append(g.charAt(0));
            i += 1;
            if (i > howMany) {
                break;
            }
        }
        return sb.toString();
    }
}
