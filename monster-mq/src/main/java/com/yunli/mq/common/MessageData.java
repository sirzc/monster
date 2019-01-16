package com.yunli.mq.common;

import java.io.Serializable;

/**
 * 消息传输类
 *
 * @author zhouchao
 * @date 2019-01-16 9:17
 */
public class MessageData<T> implements Serializable {

    private static final long   serialVersionUID = 4031285006395940859L;
    /**
     * 时间戳
     */
    private              Long   timestamp;
    /**
     * 幂等控制 备用字段
     */
    private              String uuid;
    /**
     * 具体消息内容
     */
    private              T      obj;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "MessageData{" + "timestamp=" + timestamp + ", uuid='" + uuid + '\'' + ", obj=" + obj + '}';
    }
}
