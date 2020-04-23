package com.c503.tcp.client.model;

import com.c503.tcp.client.api.ApiState;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 12:49 ，1.0
 **/
@Data
public class ApiResult<T> {
    private int code;
    private boolean status;
    private String message;
    private T data ;
    private ZonedDateTime timestamp;

    private static <T> ApiResult<T> create(int code, boolean status, String message, T data) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setMessage(message);
        apiResult.setCode(code);
        apiResult.setStatus(status);
        apiResult.setData(data);
        apiResult.setTimestamp(ZonedDateTime.now());
        return apiResult;
    }

    public static <T> ApiResult<T> create(ApiState state){
        return create(state.getCode(), state.isStatus(), state.getMessage(), null);
    }

    public static <T> ApiResult<T> create(ApiState state, T data){
        return create(state.getCode(), state.isStatus(), state.getMessage(), data);
    }

    public static <T> ApiResult<T> create(ApiState state, String message, T data){
        return create(state.getCode(), state.isStatus(), message, data);
    }
}
