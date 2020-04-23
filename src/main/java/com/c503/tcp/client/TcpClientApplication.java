package com.c503.tcp.client;

import com.c503.tcp.client.context.SpringContextHolder;
import com.c503.tcp.client.core.http.main.HttpMainServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.c503.**"})
public class TcpClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TcpClientApplication.class, args);
		SpringContextHolder.getBean(HttpMainServer.class).mainStart();
	}
}