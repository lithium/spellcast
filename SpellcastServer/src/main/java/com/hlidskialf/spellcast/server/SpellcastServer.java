package com.hlidskialf.spellcast.server;


import java.util.ArrayList;
import java.util.HashMap;
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
    private final static String validGestureCharacters = "FPSWDCK_";

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
                    client.setReady(true);
                    broadcast(client.get301(), null);
                    checkForMatchStart();
                }
            } else if (command.equals("YIELD")) {
                if (client.getState() == SpellcastClient.ClientState.Identified) {
                    client.setReady(false);
                    broadcast(client.get301(), null);
                }
                else if (client.getState() == SpellcastClient.ClientState.Playing) {
                    // TODO: client loses
                }
            } else if (currentRoundState == RoundState.WaitingForGestures && command.equals("GESTURE") && parts.length > 2) {
                if (isGestureValid(parts[1]) && isGestureValid(parts[2])) {
                    client.readyGestures(parts[1], parts[2]);
                    broadcast("321 "+currentMatchId+"."+currentRoundNumber+" "+client.getNickname()+" :Gestures ready");
                    if (isAllClientsReady()) {
                        startRoundQuestions();
                    }
                } else {
                    error_invalid_gesture(client);
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
            if ((client == null || !client.equals(c)) && (c.getNickname() != null && c.getNickname().equals(nickname))) {
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

    private boolean isGestureValid(String gesture) {
        if (gesture.length() != 1) {
            return false;
        }
        char c = gesture.toUpperCase().charAt(0);
        return validGestureCharacters.indexOf(c) != -1;
    }


    private boolean isAllClientsReady() {
        if (clients.size() < 2) {
            return false;
        }
        for (SpellcastClient c : clients.values()) {
            if (!c.isReady()) {
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
        for (SpellcastClient c : clients.values()) {
            c.setHitpoints(15);
            c.setState(SpellcastClient.ClientState.Playing);
        }
        for (SpellcastClient c : clients.values()) {
            wizards(c);
        }
        startNewRound();
    }

    private void startNewRound() {
        currentRoundNumber += 1;
        currentRoundState = RoundState.WaitingForGestures;
        broadcast("251 "+currentMatchId+"."+currentRoundNumber+" :Round start");
        for (SpellcastClient client : clients.values()) {
            client.setReady(false);
            if (client.canGestureThisRound(currentRoundNumber)) {
                broadcast("320 " + currentMatchId + "." + currentRoundNumber + " " + client.getNickname() + " :What are your gestures");
            }
        }
    }
    private void startRoundQuestions() {

        broadcast("330 Gestures:");
        for (SpellcastClient client : clients.values()) {
            broadcast("331 "+currentMatchId+"."+currentRoundNumber+" "+client.getNickname()+" "+client.getLeftGesture()+" "+client.getRightGesture());
        }
        broadcast("332 End of Gestures");

        boolean anyQuestions = false;

        // ask each client their questions
        for (SpellcastClient client : clients.values()) {
            client.setReady(false);
            client.performGestures();

            // there are some spells so there will be questions
            int nLeft = client.getLeftSpellQuestions().size();
            int nRight = client.getRightSpellQuestions().size();

            boolean hasQuestions = (nLeft > 1 ||  nRight > 1 ||
                                   (nLeft == 1 && !client.getLeftSpellQuestions().get(0).hasTarget()) ||
                                   (nRight == 1 && !client.getRightSpellQuestions().get(0).hasTarget()));
            if (hasQuestions) {
                anyQuestions = true;
                sendToClient(client, "340 "+currentMatchId+"."+currentRoundNumber+" :Questions");

                questions(client, client.getLeftSpellQuestions(), "left");
                questions(client, client.getRightSpellQuestions(), "right");

                sendToClient(client, "349 :End of Questions");
            }
        }

        if (anyQuestions) {
            currentRoundState = RoundState.WaitingForAnswers;
        } else {
            // no questions so there is nothing to resolve...
            startNewRound();
        }
    }


    public void questions(SpellcastClient client, ArrayList<SpellQuestion> spellQuestions, String hand) {
        int left = spellQuestions.size();
        if (left > 1) {
            sendToClient(client, "341 "+hand+" :Cast which spell with "+hand+" hand");
            for (SpellQuestion q : spellQuestions) {
                sendToClient(client, "342 " + q.getSpell().getSlug() + " :" + q.getSpell().getName());
            }
        } else if (left > 0) { // one spell to resolve, needs a target
            SpellQuestion q = spellQuestions.get(0);
            if (!q.hasTarget()) {
                sendToClient(client, "345 "+hand+" "+ q.getSpell().getSlug() + " :Which target for "+hand+" hand");
            }
        }

        //TODO: ask for targets of monsters
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
        sendToClient(client, "302 End of Wizards");
    }

    public void monsters(SpellcastClient client) {
        sendToClient(client, "310 Monsters:");
//        for (SpellcastClient c : clients.values()) {
//        }
        sendToClient(client, "302 End of Monsters");

    }


    public void error_nickname_used(SpellcastClient client) {
        sendToClient(client, "400 Nickname in use");
    }
    public void error_invalid_nickname(SpellcastClient client) {
        sendToClient(client, "401 Invalid nickname");
    }
    public void error_invalid_gesture(SpellcastClient client) {
        sendToClient(client, "402 Invalid gesture");
    }
}
