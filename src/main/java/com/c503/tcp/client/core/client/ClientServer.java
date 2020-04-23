package com.c503.tcp.client.core.client;

import com.c503.tcp.client.core.server.ServerContext;
import com.c503.tcp.client.model.ClientConnectVo;
import com.c503.tcp.client.service.IServerService;
import com.c503.tcp.client.utils.CopyUtils;
import com.c503.tcp.client.utils.HexBytesStringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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
    private Integer size;

    @Getter
    @Setter
    private long sendBegin;

    @Getter
    @Setter
    private ChannelFuture channelFuture;

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
            size = clientConnect.getClientPer()*clientConnect.getRequestPer()-1;
            long sendStart = System.currentTimeMillis();
            sendBegin = sendStart;
            log.info("============send start :{} ======", sendStart);
            send(clientConnect);
            long sendEnd = System.currentTimeMillis();
            log.info("============send end :{} ======", sendEnd);
            log.info("===========执行请求时间：{}======", sendEnd-sendStart);
            log.info("===========发送QPS：{}/s======", (clientConnect.getClientPer()*clientConnect.getRequestPer())*1000/(sendEnd-sendStart));

        }catch (Exception e){
            log.error("连接有误",e);
        }
    }

    private void connect(ClientConnectVo clientConnect){
        int id = clientConnect.getId();
        for (int i = 0; i<clientConnect.getTotalThreads(); i++){
            try {
                ClientConnectVo client = CopyUtils.copy(clientConnect, ClientConnectVo.class);
                client.setId(id+i);
                start(client);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    private void start(ClientConnectVo t) throws InterruptedException {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();//创建接收线程
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
        int count = 0;
        for (int i = 0; i< clientConnect.getRequestPer(); i++){
            for (int j = 0; j< clientConnect.getClientPer(); j++){
                int place = (i*clientConnect.getClientPer()+j)%clientConnect.getTotalThreads();
                serverContext.getServers().get(place).getFuture().channel().writeAndFlush(Unpooled.wrappedBuffer(bytes));
                count++;
                if (count == size)
                    channelFuture = serverContext.getServers().get(place).getFuture();
            }
        }
        log.info("总次数：{}", count);
    }

    @Override
    public void stopServer(){
        if(!CollectionUtils.isEmpty(serverContext.getServers())){//判断是否有协议子服务可以销毁
            serverContext.getServers().forEach((k,v)->{
                log.info("断开客户端连接:{},{}",k,v.getFuture().channel().toString());
                v.getFuture().channel().close();
            });
            serverContext.getServers().clear();
        }
    }

    @Override
    public void computeTime(ChannelHandlerContext ctx) {
//        log.info("最后一个回包接收时间：{}", System.currentTimeMillis());
//        if (ctx.channel() == channelFuture.channel()){
        long receiveEnd = System.currentTimeMillis();
        log.info("最后一个回包接收时间：{}", receiveEnd);
        log.info("=============收到QPS:{}===========", size*1000/(receiveEnd-sendBegin));
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

