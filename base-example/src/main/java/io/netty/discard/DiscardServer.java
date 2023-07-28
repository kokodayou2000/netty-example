package io.netty.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

public class DiscardServer {
    static final int PORT = 4100;
    public void run() throws Exception {
        System.out.println("in run threadId"+ Thread.currentThread().getId());
        // 多线程IO操作事件处理器
        // EventLoopGroup 可以有不同的协议类型实现
        // boss负责接收订单，work负责处理订单
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            // 服务启动辅助类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    // 创建通道
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 初始化管道
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 管道中有流水线，把我们自定义的句柄放到流水线的末尾
                            socketChannel.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    //
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            // 异步绑定端口
            ChannelFuture future = b.bind(PORT).sync();

            future.channel().closeFuture().sync();

        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new DiscardServer().run();
        System.out.println("in main threadId"+ Thread.currentThread().getId());
    }
}
