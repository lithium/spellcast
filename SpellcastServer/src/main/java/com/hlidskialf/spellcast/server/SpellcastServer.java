package com.hlidskialf.spellcast.server;


import com.hlidskialf.spellcast.server.SpellcastClient;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by wiggins on 1/11/15.
 */
public abstract class SpellcastServer<ChannelType> {
    private HashMap<ChannelType, SpellcastClient> clients;
    private String serverName;
    private String serverVersion;

    private int matchIdSeed;
    private String currentMatchId;
    private int currentRoundNumber;
    private MatchState currentMatchState;
    private RoundState currentRoundState;


    private enum MatchState {
        WaitingForPlayers,
        Playing
    }
    private enum RoundState {
        WaitingForGestures,
        WaitingForAnswers,
        NotPlaying
    }


    private final static Pattern validNicknamePattern = Pattern.compile("^\\w+$");

    /* abstract methods */
    abstract public void sendToClient(SpellcastClient client, String message);
    abstract public void closeClient(SpellcastClient client);

    public SpellcastServer() {}

    public SpellcastServer(String serverName, String serverVersion) {
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        clients = new HashMap<ChannelType, SpellcastClient>();
        matchIdSeed = 1000;
        currentMatchState = MatchState.WaitingForPlayers;
        currentRoundState = RoundState.NotPlaying;
    }


    public void addChannel(ChannelType channel) {
        SpellcastClient newClient = new SpellcastClient(channel);
        clients.put(channel, newClient);
        hello(newClient);
    }
    public void removeChannel(ChannelType channel) {
        SpellcastClient client = clients.get(channel);
        if (client != null)
            disconnectClient(client, false);
    }



    public void processChannelMessage(ChannelType channel, String message) {
        SpellcastClient client = clients.get(channel);
        String[] parts = message.split(" ");
        String command = parts[0].toUpperCase();

        if (command.equals("QUIT")) {
            disconnectClient(client, true);
        }
        else
        if (client.getState() == SpellcastClient.ClientState.WaitingForName) {
            if (command.equals("NAME") && parts.length > 1) {
                if (isNicknameTaken(parts[1], client)) {
                    error_nickname_used(client);
                } else if (!isNicknameValid(parts[1])) {
                    error_invalid_nickname(client);
                } else {

                    client.setNickname(parts[1]);
                    if (parts.length > 2)
                        client.setGender(parts[2]);

                    int idx = message.lastIndexOf(':');
                    if (idx != -1) {
                        client.setVisibleName(message.substring(idx + 1));
                    } else if (parts.length > 3) {
                        client.setVisibleName(parts[3]);
                    }
                    client.setState(SpellcastClient.ClientState.Identified);

                    welcome(client);
                    broadcast(client.get301(), client);
                }
            }
        }
        else {
            if (command.equals("NAME") && parts.length > 2) {
                client.setGender(parts[1]);
                int idx = message.lastIndexOf(':');
                if (idx != -1) {
                    client.setVisibleName(message.substring(idx + 1));
                } else {
                    client.setVisibleName(parts[2]);
                }
                broadcast(client.get301(), null);
            } else if (command.equals("SAY")) {
                String txt = message.substring(4);
                broadcast("201 " + client.getNickname() + " :" + txt, null);
            } else if (command.equals("READY")) {
                if (client.getState() == SpellcastClient.ClientState.Identified) {
                    client.setReadyToPlay(true);
                    broadcast(client.get301(), null);
                    checkForMatchStart();
                }
            } else if (command.equals("YIELD")) {
                if (client.getState() == SpellcastClient.ClientState.Identified) {
                    client.setReadyToPlay(false);
                    broadcast(client.get301(), null);
                }
                else if (client.getState() == SpellcastClient.ClientState.Playing) {
                    // TODO: client loses
                }
            } else if (command.equals("WHO")) {
                wizards(client);
                monsters(client);
            }
        }
    }

    private void disconnectClient(SpellcastClient client, boolean close) {
        broadcast("303 "+client.getNickname()+" :Quits", client);
        clients.remove(client.getChannel());
        if (close) {
            closeClient(client);
        }
    }
    private boolean isNicknameTaken(String nickname, SpellcastClient client) {
        for (SpellcastClient c : clients.values()) {
            if ((client == null || !client.equals(c)) && c.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }
    private boolean isNicknameValid(String nickname) {
        return (nickname.length() > 0 &&
                nickname.length() <= 32 &&
                validNicknamePattern.matcher(nickname).matches());
    }


    private boolean isAllClientsReady() {
        if (clients.size() < 2) {
            return false;
        }
        for (SpellcastClient c : clients.values()) {
            if (!c.isReadyToPlay()) {
                return false;
            }
        }
        return true;
    }
    private void checkForMatchStart() {
        if (currentMatchState == MatchState.WaitingForPlayers && isAllClientsReady()) {
            startNewMatch();
        }
    }

    private String generateMatchId() {
        matchIdSeed += 1;
        return "m"+(matchIdSeed);
    }

    private void startNewMatch() {
        if (currentMatchState == MatchState.Playing) {
            return;
        }

        currentMatchState = MatchState.Playing;
        currentMatchId = generateMatchId();
        currentRoundNumber = 0;
        broadcast("250 "+currentMatchId+ " :Match start");
        startNewRound();
    }

    private void startNewRound() {
        currentRoundNumber += 1;
        currentRoundState = RoundState.WaitingForGestures;
        broadcast("251 "+currentMatchId+"."+currentRoundNumber+" :Round start");
        for (SpellcastClient client : clients.values()) {
            if (client.canGestureThisRound(currentRoundNumber)) {
                broadcast("320 " + currentMatchId + "." + currentRoundNumber + " " + client.getNickname() + " :What are your gestures");
            }
        }
    }


    public void broadcast(String message) {
        broadcast(message, null);
    }
    public void broadcast(String message, SpellcastClient except) {
        for (SpellcastClient client : clients.values()) {
            if (except == null || !except.equals(client)) {
                sendToClient(client, message);
            }
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
        wizards(client);
        monsters(client);
    }

    public void wizards(SpellcastClient client) {
        sendToClient(client, "300 Wizards:");
        for (SpellcastClient c : clients.values()) {
            sendToClient(client, c.get301());
        }
        sendToClient(client, "302 End of Wizards.");
    }

    public void monsters(SpellcastClient client) {
        sendToClient(client, "310 Monsters:");
//        for (SpellcastClient c : clients.values()) {
//        }
        sendToClient(client, "302 End of Monsters.");

    }


    public void error_nickname_used(SpellcastClient client) {
        sendToClient(client, "400 Nickname in use");
    }
    public void error_invalid_nickname(SpellcastClient client) {
        sendToClient(client, "401 Invalid nickname");
    }
}
