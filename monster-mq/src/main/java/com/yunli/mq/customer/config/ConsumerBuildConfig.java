package com.yunli.mq.customer.config;

import com.yunli.mq.customer.handler.IMqHandler;

import java.util.List;
import java.util.Map;

/**
 * 消费者动态配置
 *
 * @author zhouchao
 * @date 2019-01-18 16:02
 */
public class ConsumerBuildConfig {

    /**
     * 同一个group中的 topic - List<>topic class</>
     */
    private Map<String, List<IMqHandler>> topicHandler;
    private String                        nameServer;
    private String                        groupName;
    private String                        topic;
    private String                        subExpression              = "*";
    private String                        instanceName;
    private long                          consumeTimeout             = 15;
    private int                           consumeMessageBatchMaxSize = 1;
    private int                           ThreadCountMin             = 20;
    private int                           ThreadCountMax             = 64;
    private boolean                       isBroadcast                = false;
    private boolean                       isOrderly                  = false;

    public Map<String, List<IMqHandler>> getTopicHandler() {
        return topicHandler;
    }

    public void setTopicHandler(Map<String, List<IMqHandler>> topicHandler) {
        this.topicHandler = topicHandler;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public long getConsumeTimeout() {
        return consumeTimeout;
    }

    public void setConsumeTimeout(long consumeTimeout) {
        this.consumeTimeout = consumeTimeout;
    }

    public int getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }

    public void setConsumeMessageBatchMaxSize(int consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }

    public int getThreadCountMin() {
        return ThreadCountMin;
    }

    public void setThreadCountMin(int threadCountMin) {
        ThreadCountMin = threadCountMin;
    }

    public int getThreadCountMax() {
        return ThreadCountMax;
    }

    public void setThreadCountMax(int threadCountMax) {
        ThreadCountMax = threadCountMax;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }

    public boolean isOrderly() {
        return isOrderly;
    }

    public void setOrderly(boolean orderly) {
        isOrderly = orderly;
    }

    @Override
    public String toString() {
        return "ConsumerBuildConfig{" + "topicHandler='" + topicHandler + '\'' + ", nameServer='" + nameServer + '\''
                + ", groupName='" + groupName + '\'' + ", topic='" + topic + '\'' + ", subExpression='" + subExpression
                + '\'' + ", instanceName='" + instanceName + '\'' + ", consumeTimeout=" + consumeTimeout
                + ", consumeMessageBatchMaxSize=" + consumeMessageBatchMaxSize + ", ThreadCountMin=" + ThreadCountMin
                + ", ThreadCountMax=" + ThreadCountMax + ", isBroadcast=" + isBroadcast + ", isOrderly=" + isOrderly
                + '}';
    }
}
