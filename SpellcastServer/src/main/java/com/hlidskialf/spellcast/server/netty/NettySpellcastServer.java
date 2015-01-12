package com.hlidskialf.spellcast.server.netty;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastServer;
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
public class NettySpellcastServer extends SpellcastServer<Channel> {


    private int port;

    @Override
    public void sendToClient(SpellcastClient client, String message) {
        Channel channel = (Channel) client.getChannel();
        channel.writeAndFlush(message+"\r\n");
    }

    @Override
    public void closeClient(SpellcastClient client) {
        Channel channel = (Channel) client.getChannel();
        channel.close();
    }

    public NettySpellcastServer(String serverName, String versionString, int port) {
        super(serverName, versionString);
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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel socketChannel) throws Exception {
                                          socketChannel.pipeline().addLast(new LineBasedFrameDecoder(512))
                                                                  .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                                                  .addLast(new StringEncoder(CharsetUtil.UTF_8))

                                                                  .addLast(new NettySpellcastClientHandler(NettySpellcastServer.this));
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

