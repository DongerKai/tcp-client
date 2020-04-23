package com.c503.tcp.client.core.http.main;


import com.c503.tcp.client.context.SpringContextHolder;
import com.c503.tcp.client.service.IMainService;
import com.c503.tcp.client.service.IServerService;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * http 线程
 *
 * @author DongerKai
 * @since 2020/4/23 13:22 ，1.0
 **/
@Data
@AllArgsConstructor
public class HttpMainThread implements Runnable {

    private IMainService mainService;
    private Object vo;

    @Override
    /**
     * 重写run方法,该方法同样是该线程的线程执行体
     */
    public void run() {
        mainService.doWork(vo);
    }
}
