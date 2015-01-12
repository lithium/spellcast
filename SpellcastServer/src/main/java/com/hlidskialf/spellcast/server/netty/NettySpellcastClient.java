package com.hlidskialf.spellcast.server.netty;

import com.hlidskialf.spellcast.server.base.SpellcastClient;
import io.netty.channel.Channel;

/**
 * Created by wiggins on 1/11/15.
 */
public class NettySpellcastClient extends SpellcastClient {
    private Channel channel;

    public NettySpellcastClient(Channel channel) {
        super();
        this.channel = channel;
    }

    @Override
    public void send(String message) {
        channel.writeAndFlush(message+"\r\n");
    }

    public Channel channel() {
        return channel;
    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
