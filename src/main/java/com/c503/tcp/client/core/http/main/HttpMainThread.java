package com.c503.tcp.client.core.http.main;


import com.c503.tcp.client.context.SpringContextHolder;
import com.c503.tcp.client.service.IServerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * http 线程
 *
 * @author DongerKai
 * @since 2020/4/23 13:22 ，1.0
 **/
@Data
@AllArgsConstructor
@Component
public class HttpMainThread implements Runnable {

    private Object vo;

    public HttpMainThread() {

    }

    @Override
    /**
     * 重写run方法,该方法同样是该线程的线程执行体
     */
    public void run() {
        System.out.println(Thread.currentThread().getName());
        IServerService serverService = SpringContextHolder.getBean("ClientServer", IServerService.class);
        serverService.startServer(vo);
    }
}
