package com.yunli.mq.exception;

/**
 * MQ业务异常
 *
 * @author zc151
 * @date 2019-01-16 21:52
 */
public class MqBussinesException extends Exception {

    public MqBussinesException() {
        super();
    }

    public MqBussinesException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqBussinesException(String message) {
        super(message);
    }

    public MqBussinesException(Throwable cause) {
        super(cause);
    }

    public MqBussinesException(Object message) {
        super(message.toString());
    }
}
