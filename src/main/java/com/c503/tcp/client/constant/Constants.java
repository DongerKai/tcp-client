package com.c503.tcp.client.constant;

import com.c503.tcp.client.api.ApiState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 12:51 ，1.0
 **/
@NoArgsConstructor
public class Constants {
    public static final String APPLICATION_JSON = "application/json";


    @Getter
    @AllArgsConstructor
    public enum State implements ApiState {
        SUCCESS(0, true, "success"),
        SYSTEM_ERROR(9999, false, "System error"),
        PERMISSION_DENIED(9998,false,"Permission denied"),
        THIRD_PARTY_API_REQUEST_ERROR(9997, false,"Third party api request error"),
        SYSTEM_BUSY(9996,false,"系统繁忙，请稍后再试！"),
        INVALID_PARAM(1001, false, "参数校验异常！"),
        INVALID_PATH(1002, false, "Invalid path"),
        INVALID_SIGN(1003,false,"Invalid sign"),
        INVALID_TOKEN(1004,false,"Invalid token"),
        ERROR_UPDATE(1008,false,"修改失败！"),
        ERROR_INSERT(1009,false,"新增失败！"),
        INVALID_ACCESS_TOKEN(1010, false, "Invalid access token！"),
        ERROR_USERNAME_OR_PASSWORD(2001, false, "用户名或密码错误！"),
        ERROR_PARK_LOAD(2100, false, "园区加载失败！"),
        ERROR_PARK_ROUTE_LOAD(2101, false, "园区路由加载失败！"),
        ERROR_VIDEO_ISC_LOAD(2102, false, "isc加载失败！"),
        VIDEO_API_ERROR(2201, false, "video api error！"),
        VIDEO_API_FAIL(2202, false, "video api fail！"),
        ERROR_REQUEST_METHOD(3001,false,"错误的请求方式！"),
        NO_MATCHING_METHOD(3002,false,"404，无匹配请求！");


        private int code;
        private boolean status;
        private String message;
    }

}
