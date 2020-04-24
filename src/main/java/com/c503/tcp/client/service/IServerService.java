package com.c503.tcp.client.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface IServerService<T> {
    void startServer(T t);

    void stopServer();

    void count(Channel channel);

    void computeTime(ChannelHandlerContext ctx);

    void logEndTime();
}
