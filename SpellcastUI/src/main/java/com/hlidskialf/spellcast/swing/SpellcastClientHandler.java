package com.hlidskialf.spellcast.swing;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


/**
 * Created by wiggins on 1/13/15.
 */
public class SpellcastClientHandler extends ChannelInboundHandlerAdapter {

	private final SpellcastForm parentForm;


	private static class NettyChannel extends SpellcastChannel {

		private Channel channel;

		public NettyChannel() {
		}

		@Override
		public void writeMessage(String message) {
			channel.writeAndFlush(message+"\r\n");
		}

		public void setChannel(Channel channel) {
			this.channel = channel;
		}
	}
	private final NettyChannel nettyChannel = new NettyChannel();

	public SpellcastClientHandler(final SpellcastForm parentForm) {
		this.parentForm = parentForm;
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		try {
			String message = ((String)msg).trim();
			if (!message.isEmpty()) {
				parentForm.appendToLog(message);

				nettyChannel.setChannel(ctx.channel());
				SpellcastMessage.dispatchMessage(parentForm, nettyChannel, message);
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}
}

