package com.hlidskialf.spellcast.server.test.helpers;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/22/15.
 */
public class TestSpellcastChannel {
    public ArrayList<String> messages;

    public TestSpellcastChannel() {
        messages = new ArrayList<String>();
    }

    public void sendToClient(String message) {
        messages.add(message);
    }

    public String lastMessage() {
        return this.lastMessage(0);
    }
    public String lastMessage(int ofs) {
        return messages.get(messages.size()-1-Math.abs(ofs));
    }
}
