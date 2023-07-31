package io.netty.factorialme;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.sql.Time;

public class FactorialClient {
    static final String HOST =  "127.0.0.1";
    static final int PORT = 4302;
    static final int COUNT = 100;

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
//            new Thread(()->{
                EventLoopGroup group = new NioEventLoopGroup();
                try{
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new FactorialClientInitializer(null));

                    ChannelFuture future = b.connect(HOST, PORT).sync();
                    // 获取句柄
                    FactorialClientHandler handler = (FactorialClientHandler)future.channel().pipeline().last();

                    System.err.format("Factorial of %,d is: %,d", COUNT, handler.getFactorial());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    group.shutdownGracefully();
                }
//            }).start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
