package io.netty.factorialme;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;

public class NumberEncoder extends MessageToByteEncoder<Number> {

    // 编码
    @Override
    protected void encode(ChannelHandlerContext ctx, Number msg, ByteBuf out) throws Exception {
        // convert
        BigInteger val;
        if (msg instanceof BigInteger){
            val = (BigInteger) msg;
        }else{
            val = new BigInteger(String.valueOf(msg));
        }
        // 将数字转换成byte数组
        byte[] data = val.toByteArray();
        int dataLength = data.length;

        // 写数据
        out.writeByte((byte)'F');
        out.writeInt(dataLength);
        out.writeBytes(data);
    }
}
