package fitz.learn.quant

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

class ClientHandler extends ChannelInboundHandlerAdapter {
  override def channelRegistered(ctx: ChannelHandlerContext): Unit = super.channelRegistered(ctx)

  override def channelUnregistered(ctx: ChannelHandlerContext): Unit = super.channelUnregistered(ctx)

  override def channelActive(ctx: ChannelHandlerContext): Unit = super.channelActive(ctx)

  override def channelInactive(ctx: ChannelHandlerContext): Unit = super.channelInactive(ctx)

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = super.channelRead(ctx, msg)

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = super.channelReadComplete(ctx)

  override def channelWritabilityChanged(ctx: ChannelHandlerContext): Unit = super.channelWritabilityChanged(ctx)

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = super.exceptionCaught(ctx, cause)
}
