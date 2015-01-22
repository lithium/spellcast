package com.hlidskialf.spellcast.server.test.helpers;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastServer;

/**
 * Created by wiggins on 1/22/15.
 */
public class TestSpellcastServer extends SpellcastServer<TestSpellcastChannel> {

    public TestSpellcastServer(String serverName, String serverVersion) {
        super(serverName, serverVersion);
    }

    @Override
    public void sendToClient(SpellcastClient client, String message) {
        TestSpellcastChannel channel = (TestSpellcastChannel)client.getChannel();
        channel.sendToClient(message);
    }

    @Override
    public void closeClient(SpellcastClient client) {
        removeChannel((TestSpellcastChannel)client.getChannel());
    }
}
