package org.qgstudio.netty.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;
import org.qgstudio.model.Feedback;
import org.qgstudio.netty.channel.ChannelSupervise;
import org.qgstudio.service.MessageService;
import org.qgstudio.service.impl.MessageServiceImpl;


/**
 * @Description: $
 * @Param: $
 * @return: $
 * @author: SheledonPeng
 * @Date: $
 */
@Log4j2
public class MyServerHandler extends ChannelInboundHandlerAdapter {


    private WebSocketServerHandshaker handshaker;

    // 无帽子在线的字符串常量
    private final String ZeroCapConnection = "当前没有嵌入式设备在线!";

    private MessageService messageService = new MessageServiceImpl();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("websocket连接报告成功");
        log.info("用户连接信息: IP: {},port: {}", channel.remoteAddress().getHostString(),channel.remoteAddress().getPort());
        ChannelSupervise.addChannel(ctx.channel());
        /*
        log.info("{}",ctx.writeAndFlush(new TextWebSocketFrame("连接成功")));
        */
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        log.info("客户端断开链接{}", ctx.channel().remoteAddress());
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //http
        if (msg instanceof FullHttpRequest) {

            FullHttpRequest httpRequest = (FullHttpRequest) msg;

            if (!httpRequest.decoderResult().isSuccess()) {

                DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);

                // 返回应答给客户端
                if (httpResponse.status().code() != 200) {
                    ByteBuf buf = Unpooled.copiedBuffer(httpResponse.status().toString(), CharsetUtil.UTF_8);
                    httpResponse.content().writeBytes(buf);
                    buf.release();
                }


                // 如果是非Keep-Alive，关闭连接
                ChannelFuture f = ctx.channel().writeAndFlush(httpResponse);
                if (httpResponse.status().code() != 200) {
                    f.addListener(ChannelFutureListener.CLOSE);
                }
                return;
            }

            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws:/" + ctx.channel().localAddress() + "/websocket", null, false);
            handshaker = wsFactory.newHandshaker(httpRequest);
            if (null == handshaker) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), httpRequest);
            }

            ctx.writeAndFlush(new TextWebSocketFrame("连接成功"));

            return;
        }

        //ws
        if (msg instanceof WebSocketFrame) {

            WebSocketFrame webSocketFrame = (WebSocketFrame) msg;

            //关闭请求
            if (webSocketFrame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
                return;
            }

            //ping请求
            if (webSocketFrame instanceof PingWebSocketFrame) {
                ctx.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
                return;
            }

            //只支持文本格式，不支持二进制消息
            if (!(webSocketFrame instanceof TextWebSocketFrame)) {
                throw new Exception("仅支持文本格式");
            }

            String request = ((TextWebSocketFrame) webSocketFrame).text();
            System.out.println("服务端收到：" + request);
            log.info("{}",ctx.channel().writeAndFlush(new TextWebSocketFrame(request)));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.info("异常信息：\r\n" + cause.getMessage());
    }

    private void sentMsgToSocket(String msg){

        ObjectMapper objectMapper = new ObjectMapper();

        String newMsg = null;
        try {
            Feedback feedback = (Feedback) objectMapper.readValue(msg,Feedback.class);
            newMsg = "";

            if (feedback != null){

                // 请求定位的内容
                if ( feedback.isLocate()){

                    newMsg = "locate@" + feedback.getAddress() + "%";

                    // 请求反馈的内容
                } else if ( feedback.isRescue()){

                    newMsg = "rescue@" + feedback.getAddress() + "%";
                }
            }
        } catch (Exception e) {

            newMsg = "error message";
        }
        log.info("向嵌入式端发送了" + newMsg);
        messageService.sendMsgToSocket(newMsg);
    }
}
