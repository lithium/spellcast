package com.hlidskialf.spellcast.swing;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wiggins on 1/16/15.
 */
public class SpellcastMessage {

    public static final String Welcome = "222";
    public static final String Hello = "200";
    public static final String WizardStatusBegin = "300";
    public static final String WizardStatus = "301";
    public static final String WizardStatusEnd = "302";
    public static final String MonsterStatusBegin = "310";
    public static final String MonsterStatus = "311";
    public static final String MonsterStatusEnd = "312";

    public static final String MatchStart = "250";
    public static final String RoundStart = "251";
    public static final String MatchWaitingForPlayers = "252";
    public static final String MatchInProgress = "253";

    public static final String AskForGestures = "320";
    public static final String GesturesReady = "321";
    public static final String GesturesForRoundBegin = "330";
    public static final String GesturesForRound = "331";
    public static final String GesturesForRoundEnd = "332";

    public static final String QuestionsBegin = "340";
    public static final String AskForSpell = "341";
    public static final String SpellAnswerOption = "342";
    public static final String SpellOptionsEnd = "343";
    public static final String AskForTarget = "345";
    public static final String TargetAnswerOption = "346";
    public static final String TargetOptionsEnd = "347";
    public static final String QuestionsEnd = "349";


    public static final String NicknameInUseError = "400";

    public static String[] splitMessage(String message) {
        int idx = message.indexOf(':');
        if (idx == -1) {
            return message.split(" ");
        } else {
            String last = message.substring(idx+1);
            String[] parts = message.substring(0,idx).split(" ");
            ArrayList<String> ret = new ArrayList<String>(Arrays.asList(parts));
            ret.add(last);
            return ret.toArray(new String[ret.size()]);
        }
    }

    public static void dispatchMessage(MessageListener listener, SpellcastChannel channel, String message) {
        String[] parts = splitMessage(message);
        if (parts.length < 1) {
            return;
        }

        if (Welcome.equals(parts[0])) {
            listener.onWelcome(channel, parts);
        }
        else if (Hello.equals(parts[0])) {
            listener.onHello(channel, parts);
        }
        else if (WizardStatus.equals(parts[0])) {
            listener.onWizardStatus(channel, parts);
        }
        else if (MonsterStatus.equals(parts[0])) {
            listener.onMonsterStatus(channel, parts);
        }
        else if (MatchStart.equals(parts[0])) {
            listener.onMatchStart(channel, parts);
        }
        else if (MatchInProgress.equals(parts[0])) {
            listener.onMatchInProgress(channel, parts);
        }
        else if (RoundStart.equals(parts[0])) {
            listener.onRoundStart(channel, parts);
        }
        else if (AskForGestures.equals(parts[0])) {
            listener.onAskForGestures(channel, parts);
        }
        else if (GesturesReady.equals(parts[0])) {
            listener.onGesturesReady(channel, parts);
        }
        else if (GesturesForRound.equals(parts[0])) {
            listener.onGesturesForRound(channel, parts);
        }

        else if (AskForSpell.equals(parts[0])) {
            listener.onAskForSpell(channel, parts);
        }
        else if (SpellAnswerOption.equals(parts[0])) {
            listener.onSpellOption(channel, parts);
        }
        else if (SpellOptionsEnd.equals(parts[0])) {
            listener.onSpellOptionsEnd(channel, parts);
        }
        else if (AskForTarget.equals(parts[0])) {
            listener.onAskForTarget(channel, parts);
        }
        else if (TargetAnswerOption.equals(parts[0])) {
            listener.onTargetOption(channel, parts);
        }
        else if (TargetOptionsEnd.equals(parts[0])) {
            listener.onTargetOptionsEnd(channel, parts);
        }

        else if (NicknameInUseError.equals(parts[0])) {
            listener.errorNicknameInUse(channel, parts);
        }
    }



    public interface MessageListener {

        public void onWelcome(SpellcastChannel channel, String[] message);
        public void onHello(SpellcastChannel channel, String[] message);

        public void onWizardStatus(SpellcastChannel channel, String[] message);
        public void onMonsterStatus(SpellcastChannel channel, String[] message);


        public void onMatchStart(SpellcastChannel channel, String[] message);
        public void onMatchInProgress(SpellcastChannel channel, String[] message);
        public void onRoundStart(SpellcastChannel channel, String[] message);

        public void onAskForGestures(SpellcastChannel channel, String[] message);
        public void onGesturesReady(SpellcastChannel channel, String[] message);
        public void onGesturesForRound(SpellcastChannel channel, String[] message);

        public void onAskForSpell(SpellcastChannel channel, String[] message);
        public void onSpellOption(SpellcastChannel channel, String[] message);
        public void onSpellOptionsEnd(SpellcastChannel channel, String[] message);
        public void onAskForTarget(SpellcastChannel channel, String[] message);
        public void onTargetOption(SpellcastChannel channel, String[] message);
        public void onTargetOptionsEnd(SpellcastChannel channel, String[] message);

        public void errorNicknameInUse(SpellcastChannel channel, String[] message);
    }




}
