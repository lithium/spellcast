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
        else if (NicknameInUseError.equals(parts[0])) {
            listener.errorNicknameInUse(channel, parts);
        }
    }



    public interface MessageListener {

        public void onWelcome(SpellcastChannel channel, String[] message);
        public void onHello(SpellcastChannel channel, String[] message);

        public void onWizardStatus(SpellcastChannel channel, String[] message);
        public void onMonsterStatus(SpellcastChannel channel, String[] message);

        public void errorNicknameInUse(SpellcastChannel channel, String[] message);
    }




}
