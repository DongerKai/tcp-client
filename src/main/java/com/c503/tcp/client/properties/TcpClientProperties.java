package com.c503.tcp.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * tcp-client 配置
 *
 * @author DongerKai
 * @since 2020/4/26 15:10 ，1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "tcp-client")
public class TcpClientProperties {
    private String name;
    private Integer port;
}
