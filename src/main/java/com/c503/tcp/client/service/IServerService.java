package com.c503.tcp.client.service;

import io.netty.channel.ChannelHandlerContext;

public interface IServerService<T> {
    void startServer(T t);

    void stopServer();

    void computeTime(ChannelHandlerContext ctx);
}
