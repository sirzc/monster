package com.unitop.mq.exception;

/**
 * MQ业务异常:RokectMq异常全在这个异常
 *
 * @author zc151
 * @date 2019-01-16 21:52
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
