package com.hlidskialf.spellcast.server.netty;

import com.hlidskialf.spellcast.server.SpellcastServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by wiggins on 1/11/15.
 */
public class NettySpellcastClientHandler extends ChannelInboundHandlerAdapter {

    private SpellcastServer server;

    public NettySpellcastClientHandler(SpellcastServer server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String message = ((String)msg).trim();
            if (!message.isEmpty()) {
                server.processChannelMessage(ctx.channel(), message);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        server.addChannel(ctx.channel());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.removeChannel(ctx.channel());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
