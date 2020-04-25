package com.c503.tcp.client.core.client;

import com.c503.tcp.client.context.SpringContextHolder;
import com.c503.tcp.client.service.IServerService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 发送报文
 *
 * @author DongerKai
 * @since 2020/4/23 13:57 ，1.0
 **/
@Data
@AllArgsConstructor
@Slf4j
public class SendThread implements Runnable {
    private List<ChannelFuture> connects;
    private int cycleTimes;
    private byte[] bytes;

    @Override
    public void run() {
        for (int i = 0; i < cycleTimes; i++) {
            int place = i%connects.size();
            connects.get(place).channel().writeAndFlush(Unpooled.wrappedBuffer(bytes));
//            IServerService serverService = SpringContextHolder.getBean("ClientServer", IServerService.class);
//            serverService.count(connects.get(place).channel());
        }
//        IServerService serverService = SpringContextHolder.getBean("ClientServer", IServerService.class);
//        serverService.logEndTime();
    }

}
