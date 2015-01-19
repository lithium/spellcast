package com.hlidskialf.spellcast.server;


import java.util.ArrayList;
import java.util.Iterator;

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
    private int maxHitpoints;
    private boolean ready;
    private String leftGesture, rightGesture;
    private ArrayList<String> leftGestures, rightGestures;
    private ArrayList<SpellQuestion> leftSpellQuestions, rightSpellQuestions;

    private ArrayList<SpellEffect> effects;


    public SpellcastClient(Object channel) {
        this.channel = channel;
        this.state = ClientState.WaitingForName;
        this.ready = false;
        this.leftGestures = new ArrayList<String>();
        this.rightGestures = new ArrayList<String>();
        leftSpellQuestions = new ArrayList<SpellQuestion>();
        rightSpellQuestions = new ArrayList<SpellQuestion>();
        effects = new ArrayList<SpellEffect>();
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

    public int getMaxHitpoints() {
        return maxHitpoints;
    }

    public void setMaxHitpoints(int maxHitpoints) {
        this.maxHitpoints = maxHitpoints;
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
            return hitpoints+"/"+maxHitpoints;
        }
        if (state == ClientState.Identified && ready) {
            return "+";
        }
        return "-";
    }


    public boolean hasEffect(String effectName) {
        return getEffect(effectName) != null;
    }
    public SpellEffect getEffect(String effectName) {
        for (SpellEffect se : effects) {
            if (se.getName().equals(effectName)) {
                return se;
            }
        }
        return null;
    }
    public boolean addEffect(SpellEffect effect) {
        if (!hasEffect(effect.getName())) {
            effects.add(effect);
            return true;
        }
        return false;
    }
    public ArrayList<String> expireEffects(SpellcastMatchState matchState) {
        ArrayList<String> expired = new ArrayList<String>();

        String matchId = matchState.getCurrentMatchId();
        int roundNumber = matchState.getCurrentRoundNumber();
        Iterator<SpellEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            SpellEffect se = iterator.next();
            if (!matchId.equals(se.getMatchId()) || roundNumber >= se.getRoundCast()+se.getDuration()) {
                expired.add(se.expire());
                iterator.remove();
            }
        }
        return expired;
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

    public ArrayList<SpellQuestion> getLeftSpellQuestions() {
        return leftSpellQuestions;
    }

    public ArrayList<SpellQuestion> getRightSpellQuestions() {
        return rightSpellQuestions;
    }

    public void resetHistory() {
        leftGestures.clear();
        rightGestures.clear();
    }
    public void resetQuestions() {
        leftSpellQuestions.clear();
        rightSpellQuestions.clear();
    }

    public void performGestures() {
        if (leftGesture == null || rightGesture == null) {
            return;
        }

        leftGestures.add(leftGesture);
        rightGestures.add(rightGesture);
        leftGesture = null;
        rightGesture = null;

        for(Spell s : findMatchingSpells(gestureHistory(leftGestures))) {
            leftSpellQuestions.add(new SpellQuestion(s));
        }
        for(Spell s : findMatchingSpells(gestureHistory(rightGestures))) {
            rightSpellQuestions.add(new SpellQuestion(s));
        }

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
            sb.insert(0,g.charAt(0));
            i += 1;
            if (i > howMany) {
                break;
            }
        }
        return sb.toString();
    }


    private SpellQuestion chooseSpell(ArrayList<SpellQuestion> questions, Spell spell) {
        for (SpellQuestion q : questions) {
            if (q.getSpell().equals(spell)) {
                return q;
            }
        }
        return null;
    }

    public void answerQuestion(String hand, String answer) {
        ArrayList<SpellQuestion> questions = hand.toLowerCase().equals("left") ? leftSpellQuestions : rightSpellQuestions;

        if (questions.size() > 1) { // should be specifying which spell
            Spell s = SpellList.lookupSpellBySlug(answer);
            if (s != null) {
                SpellQuestion chosen = chooseSpell(questions, s);
                if (chosen != null) { // chose a valid option
                    questions.clear();
                    questions.add(chosen);
                }
            }
        } else if (questions.size() == 1) { // should be specifying a target
            SpellQuestion q = questions.get(0);
            q.setTarget(answer);
        }
    }

    public boolean hasUnansweredQuestions() {
        int nLeft = leftSpellQuestions.size();
        int nRight = rightSpellQuestions.size();

        return (nLeft > 1 ||  nRight > 1 ||
               (nLeft == 1 && !leftSpellQuestions.get(0).hasTarget()) ||
               (nRight == 1 && !rightSpellQuestions.get(0).hasTarget()));

    }
}
