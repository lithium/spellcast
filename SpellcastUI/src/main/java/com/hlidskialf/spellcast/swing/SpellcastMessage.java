package com.hlidskialf.spellcast.swing;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wiggins on 1/16/15.
 */
public class SpellcastMessage {

    public static final String Welcome = "222";

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
    }



    public interface MessageListener {

        public void onWelcome(SpellcastChannel channel, String[] message);

    }




}
