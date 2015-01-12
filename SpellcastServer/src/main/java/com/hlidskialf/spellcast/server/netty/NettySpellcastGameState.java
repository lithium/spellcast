package com.hlidskialf.spellcast.server.netty;

import com.hlidskialf.spellcast.server.base.SpellcastClient;
import com.hlidskialf.spellcast.server.base.SpellcastGameState;
import io.netty.channel.Channel;

/**
 * Created by wiggins on 1/11/15.
 */
public class NettySpellcastGameState extends SpellcastGameState<Channel> {

    public NettySpellcastGameState(String serverName, String serverVersion) {
        super(serverName, serverVersion);
    }

    @Override
    public SpellcastClient newClientForChannel(Channel channel) {
        return new NettySpellcastClient(channel);
    }
}
