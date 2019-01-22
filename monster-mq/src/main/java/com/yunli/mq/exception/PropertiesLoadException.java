package com.yunli.mq.exception;

/**
 * 配置文件加载异常
 *
 * @author zhouchao
 * @date 2019-01-18 11:35
 */
public class PropertiesLoadException extends RuntimeException {
    public PropertiesLoadException() {
        super();
    }

    public PropertiesLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesLoadException(String message) {
        super(message);
    }

    public PropertiesLoadException(Throwable cause) {
        super(cause);
    }

    public PropertiesLoadException(Object message) {
        super(message.toString());
    }
}
