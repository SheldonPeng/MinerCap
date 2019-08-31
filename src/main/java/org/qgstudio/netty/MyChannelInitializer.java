package org.qgstudio.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.qgstudio.netty.handler.MyServerHandler;

/**
 * @Description: $
 * @Param: $
 * @return: $
 * @author: SheledonPeng
 * @Date: $
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    public void initChannel(SocketChannel channel) throws Exception {

        channel.pipeline().addLast("http-codec", new HttpServerCodec());
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        channel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());

        channel.pipeline().addLast(new MyServerHandler());
    }
}
