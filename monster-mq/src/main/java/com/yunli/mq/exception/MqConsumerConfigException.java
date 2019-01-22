package com.yunli.mq.exception;

/**
 * 消费者配置文件异常
 *
 * @author zhouchao
 * @date 2019-01-16 17:09
 */
public class MqConsumerConfigException extends RuntimeException {

    public MqConsumerConfigException() {
        super();
    }

    public MqConsumerConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqConsumerConfigException(String message) {
        super(message);
    }

    public MqConsumerConfigException(Throwable cause) {
        super(cause);
    }

    public MqConsumerConfigException(Object message) {
        super(message.toString());
    }
}