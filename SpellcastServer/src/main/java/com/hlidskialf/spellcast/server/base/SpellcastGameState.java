package com.hlidskialf.spellcast.server.base;


import java.util.HashMap;

/**
 * Created by wiggins on 1/11/15.
 */
public abstract class SpellcastGameState<ChannelType> {
    private HashMap<ChannelType, SpellcastClient> clients;
    private String serverName;
    private String serverVersion;


    public SpellcastGameState(String serverName, String serverVersion) {
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        clients = new HashMap<ChannelType, SpellcastClient>();
    }

    abstract public SpellcastClient newClientForChannel(ChannelType channel);

    public void addChannel(ChannelType channel) {
        SpellcastClient newClient = newClientForChannel(channel);
        clients.put(channel, newClient);
        hello(newClient);
    }
    public void removeChannel(ChannelType channel) {
        clients.remove(clients.get(channel));
    }


    public void broadcast(String message) {
        for (SpellcastClient client : clients.values()) {
            client.send(message);
        }
    }

    public void processChannelMessage(ChannelType channel, String message) {

        SpellcastClient client = clients.get(channel);
        if (client.getState() == SpellcastClient.ClientState.WaitingForName) {
            String[] parts = message.split(" ");
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


    public void pubmsg(SpellcastClient sender, String message) {
        for (SpellcastClient client : clients.values()) {
            if (client.getState() != SpellcastClient.ClientState.WaitingForName) {
                client.send("201 " + sender.getNickname() + " :" + message);
            }
        }
    }

    public void hello(SpellcastClient client) {
        client.send("222 SPELLCAST " + serverName + " " + serverVersion);
    }

    public void welcome(SpellcastClient client) {
        client.send("200 Hello "+client.getNickname());

    }

}
