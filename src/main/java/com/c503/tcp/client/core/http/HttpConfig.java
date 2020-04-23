package com.c503.tcp.client.core.http;

import com.c503.tcp.client.model.ClientConnectVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * http config
 *
 * @author DongerKai
 * @since 2020/4/23 10:57 ，1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class HttpConfig extends ClientConnectVo {

    private int maxContentLength = 1024 * 1024;//the maximum length of the aggregated content in bytes.
    private int readTimeoutSeconds = 10;// 读取超时时间  单位秒
    private int writeTimeoutSeconds = 10;//写出超时时间  单位秒
    private Integer bossThreads = 1;
    private Integer workThreads = 1;

    //调试 临时配置
    public static HttpConfig getMain(Integer port, Integer id){
        HttpConfig config = new HttpConfig();
        config.setPort(port);
        config.setId(id);
        return config;
    }
}
