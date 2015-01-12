package com.hlidskialf.spellcast.server.netty;

/**
 * Created by wiggins on 1/11/15.
 */
public class Main {


    private final static String versionString = "NettySpellcastServer_1.0-SNAPSHOT";

    public static void main(String[] args) {
        String serverName = "NettySpellcast";
        int port = 41075;

        if (args.length > 2) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            serverName = args[1];
        }

        try {
            NettySpellcastServer server = new NettySpellcastServer(serverName, port, versionString);
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
