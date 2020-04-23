package com.c503.tcp.client.service.impl;

import com.c503.tcp.client.core.client.ClientServer;
import com.c503.tcp.client.model.ApiResult;
import com.c503.tcp.client.model.ClientConnectVo;
import com.c503.tcp.client.service.IMainService;
import com.c503.tcp.client.utils.JsonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.c503.tcp.client.constant.Constants.State.SUCCESS;

/**
 * 连接关闭
 *
 * @author DongerKai
 * @since 2020/4/23 13:10 ，1.0
 **/
@Slf4j
@Service("ClientDestroyService")
@RequiredArgsConstructor
public class ClientDestroyServiceImpl implements IMainService<Object, ClientConnectVo> {

    @NonNull
    private ClientServer clientServer;

    @Override
    @SuppressWarnings("unchecked")
    public ApiResult<Object> doWork(ClientConnectVo clientConnect) {
        clientServer.stopServer();
        return ApiResult.create(SUCCESS);
    }

    @Override
    public ClientConnectVo formatData(String str) {
        return JsonUtils.readValueIgnoreException(str, ClientConnectVo.class);
    }

}
