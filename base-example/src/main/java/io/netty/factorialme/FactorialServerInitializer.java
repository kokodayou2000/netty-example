package io.netty.factorialme;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;

import java.util.Map;

// 服务端
public class FactorialServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    public FactorialServerInitializer(SslContext sslContext){
        this.sslCtx = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null){
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // 先解码
        pipeline.addLast(new BigIntegerDecoder());
        // 在编码
        pipeline.addLast(new NumberEncoder());

        pipeline.addLast(new FactorialServerHandler());
        Map<String, ChannelHandler> map = pipeline.toMap();

    }
}
