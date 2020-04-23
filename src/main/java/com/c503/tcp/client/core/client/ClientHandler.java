package com.c503.tcp.client.core.client;

/**
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 13:01 ，1.0
 **/

import com.c503.tcp.client.context.SpringContextHolder;
import com.c503.tcp.client.service.IServerService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * HJ212ServerHandler
 *
 * @author DongerKai
 * @since 2020/2/12 17:10 ，1.0
 **/
@Slf4j
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        IServerService serverService = SpringContextHolder.getBean("ClientServer", IServerService.class);
        serverService.computeTime(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常 {}", ctx.channel().toString(), cause);
        super.exceptionCaught(ctx, cause);
    }


}