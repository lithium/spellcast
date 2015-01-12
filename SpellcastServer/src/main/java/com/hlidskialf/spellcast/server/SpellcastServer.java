package com.hlidskialf.spellcast.server;


import com.hlidskialf.spellcast.server.SpellcastClient;

import java.util.HashMap;

/**
 * Created by wiggins on 1/11/15.
 */
public abstract class SpellcastServer<ChannelType> {
    private HashMap<ChannelType, SpellcastClient> clients;
    private String serverName;
    private String serverVersion;

    /* abstract methods */
    abstract public void sendToClient(SpellcastClient client, String message);

    public SpellcastServer() {}

    public SpellcastServer(String serverName, String serverVersion) {
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        clients = new HashMap<ChannelType, SpellcastClient>();
    }


    public void addChannel(ChannelType channel) {
        SpellcastClient newClient = new SpellcastClient(channel);
        clients.put(channel, newClient);
        hello(newClient);
    }
    public void removeChannel(ChannelType channel) {
        clients.remove(clients.get(channel));
    }



    public void processChannelMessage(ChannelType channel, String message) {
        SpellcastClient client = clients.get(channel);
        String[] parts = message.split(" ");

        if (client.getState() == SpellcastClient.ClientState.WaitingForName) {
            if (parts[0].equals("NAME") && parts.length > 2) {
                client.setNickname(parts[1]);
                client.setGender(parts[2]);

                int idx = message.lastIndexOf(':');
                if (idx != -1) {
                    client.setVisibleName(message.substring(idx+1));
                }
                client.setState(SpellcastClient.ClientState.Identified);

                welcome(client);
            }

        }
        else {

        }
    }


    public void broadcast(String message) {
        for (SpellcastClient client : clients.values()) {
            sendToClient(client, message);
        }
    }

    public void pubmsg(SpellcastClient sender, String message) {
        for (SpellcastClient client : clients.values()) {
            if (client.getState() != SpellcastClient.ClientState.WaitingForName) {
                sendToClient(client, "201 " + sender.getNickname() + " :" + message);
            }
        }
    }

    public void hello(SpellcastClient client) {
        sendToClient(client, "222 SPELLCAST " + serverName + " " + serverVersion);
    }

    public void welcome(SpellcastClient client) {
        sendToClient(client, "200 Hello " + client.getNickname());

    }

}
