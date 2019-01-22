package com.yunli.mq.exception;

/**
 * RocketMq使用异常，自定义异常
 *
 * @author zc151
 * @date 2019-01-16 22:37
 */
public class MqBusinessException extends Exception {

    public MqBusinessException() {
        super();
    }

    public MqBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqBusinessException(String message) {
        super(message);
    }

    public MqBusinessException(Throwable cause) {
        super(cause);
    }

    public MqBusinessException(Object message) {
        super(message.toString());
    }
}
