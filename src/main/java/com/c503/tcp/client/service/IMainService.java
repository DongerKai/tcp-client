package com.c503.tcp.client.service;

import com.c503.tcp.client.model.ApiResult;

public interface IMainService<T,M> {
    /**
     * 处理主方法
     */
    ApiResult<T> doWork(M m);

    /**
     * 生成对应请求参数,并且校验参数
     */
    M formatData(String str);
}
