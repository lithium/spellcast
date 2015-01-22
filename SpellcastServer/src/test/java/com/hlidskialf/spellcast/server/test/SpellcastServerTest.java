package com.hlidskialf.spellcast.server.test;


import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.test.helpers.TestSpellcastChannel;
import com.hlidskialf.spellcast.server.test.helpers.TestSpellcastServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by wiggins on 1/22/15.
 */
public class SpellcastServerTest {

    private TestSpellcastServer server;
    private TestSpellcastChannel firstChannel;
    private TestSpellcastChannel secondChannel;
    private SpellcastClient first;
    private SpellcastClient second;

    @Before
    public void initialize() {
        server = new TestSpellcastServer();
        firstChannel = new TestSpellcastChannel();
        secondChannel = new TestSpellcastChannel();

        first = server.addChannel(firstChannel);
        second = server.addChannel(secondChannel);
    }


    private void authenticate() {
        server.processChannelMessage(firstChannel, "NAME first");
        server.processChannelMessage(secondChannel, "NAME second");
    }

    @Test
    public void shouldInitializeTwoClients() {

        assertEquals(server.getAllClients().size(), 2);

    }


    @Test
    public void shouldIdentifyClient() {

        server.processChannelMessage(firstChannel, "NAME first");

        SpellcastClient client = server.getClientByNickname("first");
        assertNotNull(client);
        assertEquals(client.getNickname(), "first");
        assertEquals(client.getState(), SpellcastClient.ClientState.Watching);
    }

    @Test
    public void shouldSendQuitMessage() {
        authenticate();

        server.closeClient(first);

        assertEquals("303 first :Quits", secondChannel.lastMessage());

    }
}
