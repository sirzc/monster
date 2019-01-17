package com.unitop.mq.exception;

/**
 * RocketMq使用异常，自定义异常
 *
 * @author zc151
 * @date 2019-01-16 22:37
 */
public class MqWrapperException extends Exception {

    public MqWrapperException() {
        super();
    }

    public MqWrapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqWrapperException(String message) {
        super(message);
    }

    public MqWrapperException(Throwable cause) {
        super(cause);
    }

    public MqWrapperException(Object message) {
        super(message.toString());
    }
}
