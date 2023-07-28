package io.netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;

public class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {

    private ByteBuf content;

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 服务支持发送空，如果发送了任何数据，就会丢弃它
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    long counter;

    private void generateTraffic(){
        // 将 outbound buffer 刷新到socket
        // 刷新之后，再次生成同样的 traffic
        // 根据监听到的信息做决策
        ctx.writeAndFlush(content.retainedDuplicate()).addListener(trafficGenerator);
    }
    private final ChannelFutureListener trafficGenerator = future -> {
        if (future.isSuccess()){
            generateTraffic();
        }else{
            future.cause().printStackTrace();
            future.channel().close();
        }
    };
}
