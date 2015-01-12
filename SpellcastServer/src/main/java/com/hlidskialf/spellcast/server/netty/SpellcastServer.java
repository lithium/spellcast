package com.hlidskialf.spellcast.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastServer {


    private int port;
    private NettySpellcastGameState gameState;
    private String serverName;
    private String versionString;

    public SpellcastServer(String serverName, int port, String versionString) {
        this.serverName = serverName;
        this.port = port;
        this.versionString = versionString;
        this.gameState = new NettySpellcastGameState(serverName, versionString);
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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel socketChannel) throws Exception {
                                          socketChannel.pipeline().addLast(new LineBasedFrameDecoder(512))
                                                                  .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                                                  .addLast(new StringEncoder(CharsetUtil.UTF_8))

                                                                  .addLast(new SpellcastClientHandler(gameState));
                                      }
                                  });

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

}

