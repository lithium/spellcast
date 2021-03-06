package com.hlidskialf.spellcast.server;


import com.hlidskialf.spellcast.server.effect.ControlEffect;
import com.hlidskialf.spellcast.server.effect.GestureEffect;
import com.hlidskialf.spellcast.server.effect.SpellEffect;
import com.hlidskialf.spellcast.server.question.MonsterQuestion;
import com.hlidskialf.spellcast.server.question.Question;
import com.hlidskialf.spellcast.server.question.SpellQuestion;
import com.hlidskialf.spellcast.server.spell.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastClient extends Target {


    public enum ClientState {
        WaitingForName,
        Watching,
        Playing
    };

    private static final String GenderFemale = "female";
    private static final String GenderMale = "male";
    private static final String GenderNone = "none";

    private Object channel;
    private ClientState state;
    private String gender;
    private boolean ready;
    private String leftGesture, rightGesture;
    private ArrayList<String> leftGestures, rightGestures;
    private HashMap<Hand, ArrayList<SpellQuestion>> spellQuestions;
    private ArrayList<Monster> monsters;
    private ArrayList<MonsterQuestion> monsterQuestions;

    private HashMap<String, Object> properties;


    public SpellcastClient(Object channel) {
        this.channel = channel;
        this.state = ClientState.WaitingForName;
        this.ready = false;
        this.leftGestures = new ArrayList<String>();
        this.rightGestures = new ArrayList<String>();
        properties = new HashMap<String, Object>();
        monsters = new ArrayList<Monster>();
        monsterQuestions = new ArrayList<MonsterQuestion>();
        spellQuestions = new HashMap<Hand, ArrayList<SpellQuestion>>();
        spellQuestions.put(Hand.left, new ArrayList<SpellQuestion>());
        spellQuestions.put(Hand.right, new ArrayList<SpellQuestion>());
        spellQuestions.put(Hand.both, new ArrayList<SpellQuestion>());
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

    public String getHitpointString() {
        if (state == ClientState.Playing) {
            return hitpoints+"/"+maxHitpoints;
        }
        if (state == ClientState.Watching && ready) {
            return "+";
        }
        return "-";
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public Monster getMonsterById(String nick) {
        for (Monster m : monsters) {
            if (m.getNickname().equals(nick)) {
                return m;
            }
        }
        return null;
    }

    public void takeControlOfMonster(Monster monster) {
        SpellcastClient previousController = monster.getController();
        if (previousController != null) {
            previousController.loseControlOfMonster(monster);
        }
        monster.setController(this);
        monsters.add(monster);
    }
    public void loseControlOfMonster(Monster monster) {
        monsters.remove(monster);
    }




    public String get301() {
        return "301 "+nickname+" "+getHitpointString()+" "+getGender()+" :"+getVisibleName();
    }

    public boolean canGestureThisRound(int roundNumber) {
        if (hasEffect(ControlEffect.Amnesia)) {
            readyGestures(leftGestures.get(leftGestures.size()-1),
                          rightGestures.get(rightGestures.size()-1));
            return false;
        }
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

    public String getLastLeftGesture() {
        return leftGestures.get(leftGestures.size()-1);
    }
    public String getLastRightGesture() {
        return rightGestures.get(rightGestures.size()-1);
    }

    public HashMap<Hand, ArrayList<SpellQuestion>> getSpellQuestions() {
        return spellQuestions;
    }

    public ArrayList<SpellQuestion> getSpellQuestions(Hand hand) {
        return spellQuestions.get(hand);
    }

    public ArrayList<MonsterQuestion> getMonsterQuestions() {
        return monsterQuestions;
    }

    public Elemental getElemental() {
        for (Monster monster : monsters) {
            if (monster instanceof Elemental) {
                return (Elemental)monster;
            }
        }
        return null;
    }


    public void setProperty(String property, Object value) {
        properties.put(property, value);
    }
    public Object getProperty(String property) {
        return properties.get(property);
    }
    public boolean hasProperty(String property) {
        return properties.containsKey(property);
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public void reset() {
        resetHistory();
        resetQuestions();
        properties.clear();
    }
    public void resetHistory() {
        leftGestures.clear();
        rightGestures.clear();
    }
    public void resetQuestions() {
        for (ArrayList<SpellQuestion> qs : spellQuestions.values()) {
            qs.clear();
        }
        monsterQuestions.clear();
    }


    public void askForMonsterAttacks() {
        for (Monster monster : monsters) {
            if (!(monster instanceof Elemental) && !monster.hasEffect(ControlEffect.Paralysis)) {
                monsterQuestions.add(new MonsterQuestion(monster));
            }
        }
   }

    public void addGestures(String left, String right) {
        leftGestures.add(left);
        rightGestures.add(right);
    }

    public void performGestures() {
        if (leftGesture == null || rightGesture == null) {
            return;
        }


        if (hasEffect(ControlEffect.Confusion)) {
            if (new Random().nextBoolean()) {
                leftGesture = ValidationHelper.randomGesture();
            } else {
                rightGesture = ValidationHelper.randomGesture();
            }
            //TODO broadcast spell effect
        }
        if (hasEffect(ControlEffect.Fear)) {
            final String forbidden = "CDFS";
            if (forbidden.contains(leftGesture)) {
                leftGesture = "_";
            }
            if (forbidden.contains(rightGesture)) {
                rightGesture = "_";
            }
            //TODO broadcast spell effect
        }

        for (SpellEffect effect : effects) {
            if (effect instanceof GestureEffect) {
                GestureEffect gestureEffect = (GestureEffect)effect;
                // if gesture is null repeat last (paralysis)
                String gest = gestureEffect.getGesture();
                switch (gestureEffect.getHand()) {
                    case left: leftGesture = gest != null ? gest : leftGestures.get(leftGestures.size()-1); break;
                    case right: rightGesture = gest != null ? gest : rightGestures.get(rightGestures.size()-1); break;
                }
            }
        }

        leftGesture = leftGesture.toUpperCase();
        rightGesture = rightGesture.toUpperCase();
        if (leftGesture.equals(rightGesture)) {
            leftGesture = leftGesture.toLowerCase();
            rightGesture = leftGesture;
        }

        leftGestures.add(leftGesture);
        rightGestures.add(rightGesture);

        if (Character.isLowerCase(leftGesture.charAt(0)) && Character.isLowerCase(rightGesture.charAt(0))) {

            ArrayList<Spell> casting = new ArrayList<Spell>();
            for(Spell s : findMatchingSpells(gestureHistory(leftGestures))) {
                spellQuestions.get(Hand.both).add(new SpellQuestion(s));
                casting.add(s);
            }
            for(Spell s : findMatchingSpells(gestureHistory(rightGestures))) {
                if (!casting.contains(s)) {
                    spellQuestions.get(Hand.both).add(new SpellQuestion(s));
                    casting.add(s);
                }

            }

        } else {

            for(Spell s : findMatchingSpells(gestureHistory(leftGestures))) {
                spellQuestions.get(Hand.left).add(new SpellQuestion(s));
            }
            for(Spell s : findMatchingSpells(gestureHistory(rightGestures))) {
                spellQuestions.get(Hand.right).add(new SpellQuestion(s));
            }

        }


        leftGesture = null;
        rightGesture = null;

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

        // hack for short lightning bolt
        if (spell == SpellList.LightningBolt2 && hasProperty(LightningBoltSpell.usedShortProp)) {
            return false;
        }

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
        int l = gestures.size();
        howMany = Math.min(l, howMany);
        StringBuffer sb = new StringBuffer(howMany);
        int i;
        for (i=l-1; i >= 0; i--) {
            sb.append(gestures.get(i).charAt(0));
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

    public void answerQuestion(String handString, String answer) {

        if (handString.contains("$")) {
            for (MonsterQuestion mq : monsterQuestions) {
                if (mq.getNickname().equals(handString)) {
                    mq.setAnswer(answer);
                    return;
                }
            }
            return;
        }

        Hand hand = Hand.valueOf(handString.toLowerCase());
        ArrayList<SpellQuestion> questions = spellQuestions.get(hand);

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
            q.setAnswer(answer);
        }
    }

    public void answerMonsterQuestion(String monsterId, String answer) {
        for (MonsterQuestion mq : monsterQuestions) {
            if (mq.getMonsterNickname().equals(monsterId)) {
                mq.setAnswer(answer);
                return;
            }
        }
    }

    public boolean hasUnansweredQuestions() {

        for (MonsterQuestion mq : monsterQuestions) {
            if (!mq.hasAnswer()) {
                return true;
            }
        }

        for (ArrayList<SpellQuestion> qs : spellQuestions.values()) {
            if (qs.size() > 1 || (qs.size() == 1 && (!qs.get(0).hasAnswer() || qs.get(0).hasUnansweredSubQuestions()))) {
                return true;
            }
        }
        return false;

    }
}
