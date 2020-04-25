package com.c503.tcp.client.core.client;

import com.c503.tcp.client.core.server.ServerContext;
import com.c503.tcp.client.model.ClientConnectVo;
import com.c503.tcp.client.service.IServerService;
import com.c503.tcp.client.utils.HexBytesStringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * hj212Server
 *
 * @author DongerKai
 * @since 2020/2/20 13:29 ，1.0
 **/
@RequiredArgsConstructor
@Service("ClientServer")
@Slf4j
public class ClientServer implements IServerService<ClientConnectVo> {
    @NonNull
    private ServerContext serverContext;

    @Getter
    @Setter
    private long sendBegin;

    @Getter
    @Setter
    private long size;

    @Getter
    @Setter
    private Map<Channel, Integer> map;

    @Override
    public void startServer(ClientConnectVo clientConnect) {
        try{
            int id = serverContext.getServers().size();
            clientConnect.setId(id);
            long connectStart = System.currentTimeMillis();
            log.info("============connect start :{} ======", connectStart);
            connect(clientConnect);
            long connectEnd = System.currentTimeMillis();
            log.info("============connect end :{} ======", connectEnd);
            log.info("===========连接建立时间：{}======", connectEnd-connectStart);
            size = clientConnect.getThreads()*clientConnect.getCycleTimes();
            map = new HashMap<>();
            send(clientConnect);
        }catch (Exception e){
            log.error("连接有误",e);
        }
    }

    private void connect(ClientConnectVo clientConnect){
        for (int i = 0; i<clientConnect.getConnections(); i++){
            try {
                ClientConnectVo client = new ClientConnectVo();
                client.setId(i);
                client.setIp(clientConnect.getIp());
                client.setPort(clientConnect.getPort());
                start(client);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        log.info("============connect finish ======");
    }

    private void start(ClientConnectVo t) throws InterruptedException {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(4);//创建线程
        Bootstrap b = new Bootstrap();
        b.group(bossGroup).channel(NioSocketChannel.class).handler(getChannelInitializer());
        this.setOption(b);//设置socket option
        ChannelFuture cf = b.connect(t.getIp(),t.getPort()).sync();//使用同步的方式绑定服务监听端口
        cf.channel().closeFuture().addListener((ChannelFutureListener) future -> {//设置链路关闭监听事件
            bossGroup.shutdownGracefully();
        });
        //设置协议服务上下文
        t.setFuture(cf);
        serverContext.addServer(t);
    }

    private void send(ClientConnectVo clientConnect){
        byte[] bytes = HexBytesStringUtils.hexStringToBytes(clientConnect.getMsg());
        List<ChannelFuture> channelFutureList = serverContext.getServers().values().stream().map(ClientConnectVo::getFuture).collect(Collectors.toList());
        int interval = channelFutureList.size()/clientConnect.getThreads();
        int tail = channelFutureList.size()%clientConnect.getThreads();
        long sendStart = System.currentTimeMillis();
        sendBegin = sendStart;
        log.info("============send start :{} ======", sendStart);
        for (int i = 0; i < clientConnect.getThreads(); i++){
            int begin = tail > i? i*interval+i: i*interval+tail;
            int end = tail > i? (i+1)*interval+i : (i+1)*interval-1+tail;
            new Thread(new SendThread(channelFutureList.subList(begin, end+1), clientConnect.getCycleTimes(), bytes), "sendThread-"+i).start();
        }
    }

    @Override
    public void count(Channel channel) {
        map.put(channel, map.get(channel) == null? 1: map.get(channel)+1);
    }

    @Override
    public void logEndTime() {
        long receiveEnd = System.currentTimeMillis();
        log.info("=============发送结束时间:{}============", receiveEnd);
        log.info("============QPS:{}===========", size*1000/(receiveEnd-sendBegin));
    }

    @Override
    public void stopServer(){
        if(!CollectionUtils.isEmpty(serverContext.getServers())){//判断是否有协议子服务可以销毁
            serverContext.getServers().forEach((k,v)->v.getFuture().channel().close());
            log.info("断开客户端连接");
            serverContext.getServers().clear();
        }
    }

    @Override
    public void computeTime(ChannelHandlerContext ctx) {
        long receiveEnd = System.currentTimeMillis();
        log.info("==============最后一个回包接收时间：{},channel:{}, QPS:{}/s=============", receiveEnd ,ctx.channel(), size*1000/(receiveEnd-sendBegin));
//        Integer value = map.get(ctx.channel());
//        if (value == null)
//            return;
//        map.put(ctx.channel(), value-1);
//        if (value == 0 || value < 0){
//            long receiveEnd = System.currentTimeMillis();
//            log.info("==============最后一个回包接收时间：{}, map中value值:{} ,channel:{}, QPS:{}/s=============", receiveEnd, value, ctx.channel(), size*1000/(receiveEnd-sendBegin));
//        }

    }


    private ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel sc) {
                ChannelPipeline cp = sc.pipeline();
                cp.addLast(new ClientHandler());
            }
        };
    }

    private void setOption(Bootstrap b) {
        b.option(ChannelOption.TCP_NODELAY, true);
    }
}

