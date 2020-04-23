package com.c503.tcp.client.core.server;

import com.c503.tcp.client.model.ClientConnectVo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 11:32 ，1.0
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class ServerContext {

    @Getter
    private static final ReentrantLock serverLock = new ReentrantLock(true);//服务启停锁
    private ClientConnectVo mainHttpServer;//http 主服务
    @Getter
    private Map<Integer,ClientConnectVo> servers;//协议子服务
    /**
     * 添加协议服务
     */
    public void addServer(ClientConnectVo server){
        if(mainHttpServer == null){//添加主服务
            log.info("添加主服务:{}",server.getFuture().channel().toString());
            mainHttpServer = server;
            servers = new ConcurrentHashMap<Integer, ClientConnectVo>();
        }else{//添加协议子服务
            log.info("添加客户端:{},{}",server.getId(),server.getFuture().channel().toString());
            servers.put(server.getId(),server);
        }
    }

    /**
     * 销毁协议子服务
     */
    public boolean destroyServer(Integer id){
        ClientConnectVo server = servers.get(id);
        if(server == null) {//销毁协议子服务不存在,直接返回
            log.info("销毁协议子服务:{},不存在！",id);
            return false;
        }
        log.info("销毁协议子服务:{},{}",id,server.getFuture().channel().toString());
        server.getFuture().channel().close();
        servers.remove(id);
        //修改数据库状态
        return true;
    }

    /**
     * 销毁所有服务,程序关闭调用
     */
    @PreDestroy
    public void destroyServerAll(){
        log.info("服务关闭,开始销毁所有服务！");
        if(mainHttpServer != null) {//判断Http主服务是否存在
            log.info("销毁主HTTP服务:{}",mainHttpServer.getFuture().channel().toString());
            mainHttpServer.getFuture().channel().close();
        }
        if(!CollectionUtils.isEmpty(servers)){//判断是否有协议子服务可以销毁
            servers.forEach((k,v)->{
                log.info("销毁协议子服务:{},{}",k,v.getFuture().channel().toString());
                v.getFuture().channel().close();
            });
            servers.clear();
        }
        log.info("销毁所有服务完成！");
    }
}
