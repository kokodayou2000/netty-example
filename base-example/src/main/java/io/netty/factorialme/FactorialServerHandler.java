package io.netty.factorialme;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;

public class FactorialServerHandler extends SimpleChannelInboundHandler<BigInteger> {
    private BigInteger lastMultiplier = new BigInteger("1");

    private BigInteger factorial = new BigInteger("1");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        System.out.println(msg);
        // 实际进行计算的地方
        lastMultiplier = msg;
        factorial = factorial.multiply(msg);
        Thread.sleep(100);
        // 将结果写到控制台中
        ctx.writeAndFlush(factorial);
//        System.out.println("channel "+ctx.channel().id());
        System.out.println(ctx.channel().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("Factorial of %d, is: %,d%n",lastMultiplier,factorial);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
