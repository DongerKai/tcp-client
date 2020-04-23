package com.c503.tcp.client.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 字符 和 字符串 转换 util
 *
 * @author DongerKai
 * @since 2020/4/23 13:07 ，1.0
 **/
@Slf4j
public class HexBytesStringUtils {
    /**
     * 十六进制字符串转十六进制字节数组
     *
     * @param hex 十六进制字符串
     * @return 十六进制字节数组
     */
    public static byte[] hexStringToBytes(String hex) {
        if (hex.length() < 1) {
            log.error("十六进制字符串必须为偶数！，当前hex值为：{}", hex);
            throw new IllegalArgumentException();
        } else {
            byte[] result = new byte[hex.length() / 2];
            int j = 0;
            for (int i = 0; i < hex.length(); i += 2) {
                result[j++] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            }
            return result;
        }
    }
}
