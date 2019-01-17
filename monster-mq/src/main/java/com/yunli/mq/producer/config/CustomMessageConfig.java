package com.yunli.mq.producer.config;


import com.yunli.mq.common.MessageData;
import com.yunli.mq.producer.enums.MessageDelayLevelEnum;
import com.yunli.mq.producer.enums.ProducerSendModeEnum;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 自定义mq消息配置
 *
 * @author zc151
 * @date 2019-01-16 21:52
 */
public class CustomMessageConfig {

    /**
     * 发送模式 同步、异步、one-way
     */
    private ProducerSendModeEnum  sendMode;
    /**
     * topic 主题
     */
    private String                topic;
    /**
     * tags 主题下的标签，暂统一为default
     */
    private String                tags;
    /**
     * keys 消息对应的key，建议每个消息有唯一的key
     */
    private String                keys;
    /**
     * message 消息
     */
    private MessageData           message;
    /**
     * charSet 编码
     */
    private String                charSet;
    /**
     * callback 异步调用时，使用的回调函数
     */
    private SendCallback          callback;
    /**
     * sort 顺序消息需要的参数，该参数相同值下的消息保证有序
     */
    private Object                sort;
    /**
     * delayLevel 延迟发送级别
     */
    private MessageDelayLevelEnum delayLevel;

    public CustomMessageConfig(String topic, MessageData message) {
        this(topic, "", "", message);
    }

    public CustomMessageConfig(String topic, String tags, MessageData message) {
        this(topic, tags, "", message);
    }

    public CustomMessageConfig(String topic, String tags, String keys, MessageData message) {
        this.topic = topic;
        this.tags = tags;
        this.keys = keys;
        this.message = message;
        this.sendMode = ProducerSendModeEnum.SYNC;
        this.charSet = RemotingHelper.DEFAULT_CHARSET;
        this.callback = null;
        this.sort = null;
        this.delayLevel = null;
    }

    public ProducerSendModeEnum getSendMode() {
        return sendMode;
    }

    public void setSendMode(ProducerSendModeEnum sendMode) {
        this.sendMode = sendMode;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public MessageData getMessage() {
        return message;
    }

    public void setMessage(MessageData message) {
        this.message = message;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public SendCallback getCallback() {
        return callback;
    }

    public void setCallback(SendCallback callback) {
        this.callback = callback;
    }

    public Object getSort() {
        return sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    public MessageDelayLevelEnum getDelayLevel() {
        return delayLevel;
    }

    public void setDelayLevel(MessageDelayLevelEnum delayLevel) {
        this.delayLevel = delayLevel;
    }

    @Override
    public String toString() {
        return "CustomMessageConfig{" + "sendMode=" + sendMode + ", topic='" + topic + '\'' + ", tags='" + tags + '\''
                + ", keys='" + keys + '\'' + ", message=" + message + ", charSet='" + charSet + '\'' + ", callback="
                + callback + ", sort=" + sort + ", delayLevel=" + delayLevel + '}';
    }
}
