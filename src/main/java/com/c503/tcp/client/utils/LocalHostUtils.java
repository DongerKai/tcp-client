package com.c503.tcp.client.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 获取本机ip信息
 *
 * @author DongerKai
 * @since 2020/4/23 12:41 ，1.0
 **/

public class LocalHostUtils {
    private LocalHostUtils(){}


    //获取本机ip
    public static String getLocalHost() throws Exception{
        return getLocalHostAddress().getHostAddress();
    }


    public static InetAddress getLocalHostAddress() throws SocketException, UnknownHostException {
        InetAddress candidateAddress = null;
        // 遍历所有的网络接口
        for (Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces(); faces.hasMoreElements();) {
            NetworkInterface face = faces.nextElement();
            // 在所有的接口下再遍历IP
            for (Enumeration<InetAddress> addresses = face.getInetAddresses(); addresses.hasMoreElements();) {
                InetAddress address = addresses.nextElement();
                if(address.isLoopbackAddress())// 排除loopback类型地址
                    continue;
                if (address.isSiteLocalAddress()) {
                    // 如果是site-local地址，就是它了
                    return address;
                } else if (candidateAddress == null) {
                    // site-local类型的地址未被发现，先记录候选地址
                    candidateAddress = address;
                }
            }
        }
        if (candidateAddress != null) {
            return candidateAddress;
        }
        // 如果没有发现 non-loopback地址.只能用最次选的方案
        return InetAddress.getLocalHost();
    }
}
