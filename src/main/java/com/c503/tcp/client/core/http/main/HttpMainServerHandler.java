package com.c503.tcp.client.core.http.main;

import com.c503.tcp.client.context.SpringContextHolder;
import com.c503.tcp.client.core.server.ServerContext;
import com.c503.tcp.client.model.ApiResult;
import com.c503.tcp.client.service.IMainService;
import com.c503.tcp.client.utils.JsonUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.c503.tcp.client.constant.Constants.APPLICATION_JSON;
import static com.c503.tcp.client.constant.Constants.State.*;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * http 数据处理
 *
 * @author DongerKai
 * @since 2020/4/23 10:58 ，1.0
 **/
@Slf4j
@ChannelHandler.Sharable
public class HttpMainServerHandler extends ChannelInboundHandlerAdapter {
    @SuppressWarnings("unchecked")
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String req = null;
        String method = null;
        try {
            FullHttpRequest request = (FullHttpRequest) msg;
            method = request.uri().substring(1);
            if ("favicon.ico".equals(method))
                return;
            //判断是否在spring上下文中存在 判断类型是否对应
            if(!SpringContextHolder.containsBean(method) || !SpringContextHolder.isTypeMatch(method,IMainService.class)){
                sendResponse(ctx,method, ApiResult.create(NO_MATCHING_METHOD));
                return;
            }
            //只接受Post 请求  并且需要APPLICATION_JSON
            if(!HttpMethod.POST.equals(request.method()) || !checkHeaders(request.headers())){
                sendResponse(ctx,method,ApiResult.create(ERROR_REQUEST_METHOD));
                return;
            }
            //从spring上下文中获取service
            IMainService<?,Object> service = SpringContextHolder.getBean(method,IMainService.class);
            //从请求中获取参数 只接受 json
            req = request.content().toString(StandardCharsets.UTF_8);
            Object vo = service.formatData(req);
            if(vo == null){//校验参数
                sendResponse(ctx,method,req,ApiResult.create(INVALID_PARAM));
                return;
            }
            //尝试获取锁，可以等待2秒
            if(!ServerContext.getServerLock().tryLock(2, TimeUnit.SECONDS)){
                sendResponse(ctx,method,req,ApiResult.create(SYSTEM_BUSY));
                return;
            }
            HttpMainThread httpMainThread = new HttpMainThread();
            httpMainThread.setVo(vo);
            new Thread(httpMainThread).start();
            sendResponse(ctx,method,req,ApiResult.create(SUCCESS));
        }catch (Exception e) {
            sendResponse(ctx,method,req,ApiResult.create(SYSTEM_ERROR),e);
        }finally {
            //如果锁是当前线程获取的  那我们需要释放锁
            if(ServerContext.getServerLock().isHeldByCurrentThread())
                ServerContext.getServerLock().unlock();
        }
    }

    /**
     * 校验请求头
     */
    private boolean checkHeaders(HttpHeaders headers){
        String typeStr = headers.get(CONTENT_TYPE);
        return typeStr != null && typeStr.contains(APPLICATION_JSON);
    }

    /**
     * 返回结果
     */
    private void sendResponse(ChannelHandlerContext ctx,String method,ApiResult<?> result){
        sendResponse(ctx,method,null,result);
    }

    /**
     * 返回结果
     */
    private void sendResponse(ChannelHandlerContext ctx,String method,String req,ApiResult<?> result){
        sendResponse(ctx,method,req,result,null);
    }

    /**
     * 返回结果
     */
    private void sendResponse(ChannelHandlerContext ctx,String method,String req,ApiResult<?> result,Exception e) {
        String resp = JsonUtils.writeValueAsString(result);
        log.info("channel method:{},req:{},resp:{}", method, req, resp, e);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resp.getBytes()));
        response.headers().set(CONTENT_TYPE, APPLICATION_JSON);
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // flush掉所有写回的数据
                .addListener(ChannelFutureListener.CLOSE);// 当flush完成后关闭channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("channel exceptionCaught", cause);
        ctx.close();
    }

}
