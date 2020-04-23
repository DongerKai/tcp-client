package com.c503.tcp.client.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 12:56 ，1.0
 **/
@Slf4j
@NoArgsConstructor
public class FunctionUtils {
    /**
     * 执行但是不关心成功没有，忽略异常
     *
     * 使用方法：
     * <code>FunctionUtil.applyIgnoreException(() -> System.out.print("hi"))</code>
     */
    public static <T> T applyIgnoreException(TAction<T> action) {
        try {
            return action.apply();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 执行但是不关心成功没有，忽略异常
     */
    public static void applyIgnoreException(VoidAction action) {
        try {
            action.apply();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }

    /**
     * 执行关心是否成功，但是抑制抛出的异常 ，修改为系统异常
     */
    public static <T> T applyThrowException(TAction<T> action) {
        try {
            return action.apply();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行关心是否成功，但是抑制抛出的异常 ，修改为系统异常
     */
    public static void applyThrowException(VoidAction action) {
        try {
            action.apply();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行关心是否成功，但是抑制抛出的异常 ，修改为自定义异常
     */
    public static <T> T applyThrowException(TAction<T> action,RuntimeException exception) {
        try {
            return action.apply();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw exception;
        }
    }

    /**
     * 执行关心是否成功，但是抑制抛出的异常 ，修改为自定义异常
     */
    public static void applyThrowException(VoidAction action,RuntimeException exception) {
        try {
            action.apply();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw exception;
        }
    }

    public interface TAction<T> {
        T apply() throws Exception;
    }

    public interface VoidAction {
        void apply() throws Exception;
    }
}
