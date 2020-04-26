package com.c503.tcp.client.core.http.main;

import com.c503.tcp.client.context.SpringContextHolder;
import com.c503.tcp.client.core.http.HttpConfig;
import com.c503.tcp.client.core.server.ServerContext;
import com.c503.tcp.client.properties.TcpClientProperties;
import com.c503.tcp.client.utils.LocalHostUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

/**
 * http main server
 *
 * @author DongerKai
 * @since 2020/4/23 10:58 ，1.0
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class HttpMainServer {
    @NonNull private ServerContext serverContext;
    @NonNull private TcpClientProperties tcpClientProperties;
    public void mainStart() {
        try {
            ServerContext.getServerLock().lock();
            //获取本机ip地址
            String ip = LocalHostUtils.getLocalHost();
            log.info(" 本地ip:{}",ip);
            //启动http主服务
            start(HttpConfig.getMain(tcpClientProperties.getPort(), 1));
            //启动激活中的协议子服务
            log.info("服务启动成功！*★,°*:.☆(￣▽￣)/$:*.°★* 。");
        }catch (Exception e){
            log.error("服务启动失败！╥﹏╥...",e);
            //退出程序
            System.exit(SpringApplication.exit(SpringContextHolder.getApplicationContext()));
        } finally {
            ServerContext.getServerLock().unlock();
        }
    }

    private void start(HttpConfig t) throws InterruptedException {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(t.getBossThreads());//创建接收线程
        final EventLoopGroup workerGroup = new NioEventLoopGroup(t.getWorkThreads());//创建工作线程
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(getChannelInitializer(t));
        setOption(b,t);//设置socket option
        ChannelFuture cf = b.bind("0.0.0.0",t.getPort()).sync();//使用同步的方式绑定服务监听端口
        cf.channel().closeFuture().addListener((ChannelFutureListener) future -> {//设置链路关闭监听事件
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        });

        //设置协议服务上下文
        t.setFuture(cf);
        serverContext.addServer(t);
    }

    private void setOption(ServerBootstrap b, HttpConfig config) {
        b.option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
    }

    private ChannelInitializer<SocketChannel> getChannelInitializer(final HttpConfig config) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel sc) {
                ChannelPipeline cp = sc.pipeline();
                cp.addLast(new HttpRequestDecoder());// 用于解析http报文的handler
                cp.addLast(new HttpObjectAggregator(config.getMaxContentLength()));
                cp.addLast(new HttpResponseEncoder());// 用于将response编码成http response报文发送
                cp.addLast(new ReadTimeoutHandler(config.getReadTimeoutSeconds()));//设置读取超时时间
                cp.addLast(new WriteTimeoutHandler(config.getWriteTimeoutSeconds()));//设置写出数据超时时间
                cp.addLast(new HttpMainServerHandler());
            }
        };
    }




}
