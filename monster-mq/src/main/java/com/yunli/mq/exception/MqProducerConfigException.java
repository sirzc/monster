package com.yunli.mq.exception;

/**
 * 配置文件异常
 * @author zhouchao
 * @date 2019-01-16 17:09
 */
public class MqProducerConfigException extends RuntimeException {
    public MqProducerConfigException() {
        super();
    }

    public MqProducerConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqProducerConfigException(String message) {
        super(message);
    }

    public MqProducerConfigException(Throwable cause) {
        super(cause);
    }

    public MqProducerConfigException(Object message) {
        super(message.toString());
    }
}