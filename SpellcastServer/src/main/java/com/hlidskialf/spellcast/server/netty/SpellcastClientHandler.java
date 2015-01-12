package com.hlidskialf.spellcast.server.netty;

import com.hlidskialf.spellcast.server.base.SpellcastClient;
import com.hlidskialf.spellcast.server.base.SpellcastGameState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastClientHandler extends ChannelInboundHandlerAdapter {

    private SpellcastGameState gameState;

    public SpellcastClientHandler(SpellcastGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String message = ((String)msg).trim();
            if (!message.isEmpty()) {
                gameState.processChannelMessage(ctx.channel(), message);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        gameState.addChannel(ctx.channel());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        gameState.removeChannel(ctx.channel());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
