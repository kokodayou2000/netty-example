package io.netty.factorialme;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

/**
 * 长整形解码器
 * 规则 {'F',0,0,0,1,42}
 * F 是magic number
 * 0001 表示1位
 * 127 -> 0111 1111 这是 1 byte 8bit
 * 128 -> 1000 0000 这是 2 byte 16bit 1111 1111
 * 128 -> 原码 1000 0000 反码 1111 1111  补码 1111 1111 + 1 = 1000 0000 0000 0000
 * 129 -> 源码 1000 0001 反码 1111 1110 补码 1111 1111
 * [-128,0]
 * 42 最后的结果
 */
public class BigIntegerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 由于前缀是 f0001 等待前缀完成
        if (in.readableBytes() < 5){
            return;
        }
        // 标记索引
        in.markReaderIndex();

        // 检测标志 readUnsignedByte获取第一位，然后索引++
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F'){
            // 重设标记位
            in.resetReaderIndex();
            throw new CorruptedFrameException("magic number 解析失败" + magicNumber);
        }

        // 比对传过来的字节数
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength){
            // 继续等待
            in.resetReaderIndex();
            return;
        }
        // 创建decode 用来接收数据
        byte[] decode = new byte[dataLength];
        in.readBytes(decode);
        // 向out添加数据
        out.add(new BigInteger(decode));
    }

}
