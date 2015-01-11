package com.hlidskialf.spellcast.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastClientHandler extends ChannelInboundHandlerAdapter {

    static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String message = (String)msg;

            allChannels.writeAndFlush("["+ctx.channel().remoteAddress()+"] "+message+"\r\n");
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        allChannels.add(ctx.channel());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        allChannels.remove(ctx.channel());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
