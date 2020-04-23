package com.c503.tcp.client.service.impl;

import com.c503.tcp.client.core.client.ClientServer;
import com.c503.tcp.client.core.server.ServerContext;
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
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 13:02 ，1.0
 **/
@Slf4j
@Service("ClientStartService")
@RequiredArgsConstructor
public class ClientStartServiceImpl implements IMainService<Object, ClientConnectVo> {
    @NonNull private ClientServer clientServer;


    @Override
    @SuppressWarnings("unchecked")
    public ApiResult<Object> doWork(ClientConnectVo clientConnect) {
        log.info(clientConnect.toString());
        clientServer.startServer(clientConnect);
        return ApiResult.create(SUCCESS);
    }

    @Override
    public ClientConnectVo formatData(String str) {
        return JsonUtils.readValueIgnoreException(str, ClientConnectVo.class);
    }
}
