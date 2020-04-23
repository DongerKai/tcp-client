package com.c503.tcp.client.model;

import io.netty.channel.ChannelFuture;
import lombok.Data;

/**
 * 请求参数
 *
 * @author DongerKai
 * @since 2020/4/23 12:33 ，1.0
 **/
@Data
public class ClientConnectVo {
    private String ip;
    private Integer port;
    private Integer totalThreads;//总线程
    private ChannelFuture future;
    private Integer id;
    private String msg;
    private Integer clientPer;//每次使用的线程数
    private Integer requestPer;//每个线程的请求数
}
