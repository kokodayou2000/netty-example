package io.netty.factorialme;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

import javax.net.ssl.SSLContext;
import java.util.Map;

public class FactorialClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SSLContext sslCtx;
    public FactorialClientInitializer(SSLContext sslCtx){
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null){

        }
        // 对流数据进行压缩
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // 添加解码器
        pipeline.addLast(new BigIntegerDecoder());
        // 添加编码器
        pipeline.addLast(new NumberEncoder());

        pipeline.addLast(new FactorialClientHandler());
        Map<String, ChannelHandler> map = pipeline.toMap();
    }
}
