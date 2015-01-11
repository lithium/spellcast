package com.hlidskialf.spellcast.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastServer {


    private int port;

    public SpellcastServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .option(ChannelOption.SO_BACKLOG, 128)
                     .childOption(ChannelOption.SO_KEEPALIVE, true)
                     .childHandler(new SpellcastServerInitializer());

            //bind to the port and accept connections
            ChannelFuture channelFuture = bootstrap.bind(port).sync();

            Channel serverChannel = channelFuture.sync().channel();

            //wait until the socket is closed
            serverChannel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 41075;
        }

        try {
            SpellcastServer server = new SpellcastServer(port);
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
