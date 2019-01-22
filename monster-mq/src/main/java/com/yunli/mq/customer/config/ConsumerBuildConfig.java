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
    /**
     * 分组 名称
     */
    private String                        groupName;
    /**
     * 分组，消息按组进行消费
     */
    private String                        group;
    /**
     * 消费子表达式
     */
    private String                        subExpression;
    /**
     * 是否顺序
     */
    private boolean                       isOrderly;
    /**
     * 是否广播
     */
    private boolean                       isBroadcast;
    /**
     * 最小消费者线程数
     */
    private int                           consumerThreadCountMin;
    /**
     * 最大消费者线程数
     */
    private int                           consumerThreadCountMax;
    /**
     * 批量消息最大大小
     */
    private int                           consumeMessageBatchMaxSize;
    /**
     * 引用配置文件前缀（切换配置）
     */
    private String                        prefix;

    public Map<String, List<IMqHandler>> getTopicHandler() {
        return topicHandler;
    }

    public void setTopicHandler(Map<String, List<IMqHandler>> topicHandler) {
        this.topicHandler = topicHandler;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }

    public boolean isOrderly() {
        return isOrderly;
    }

    public void setOrderly(boolean orderly) {
        isOrderly = orderly;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }

    public int getConsumerThreadCountMin() {
        return consumerThreadCountMin;
    }

    public void setConsumerThreadCountMin(int consumerThreadCountMin) {
        this.consumerThreadCountMin = consumerThreadCountMin;
    }

    public int getConsumerThreadCountMax() {
        return consumerThreadCountMax;
    }

    public void setConsumerThreadCountMax(int consumerThreadCountMax) {
        this.consumerThreadCountMax = consumerThreadCountMax;
    }

    public int getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }

    public void setConsumeMessageBatchMaxSize(int consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "ConsumerBuildConfig{" + "topicHandler=" + topicHandler + ", groupName='" + groupName + '\''
                + ", group='" + group + '\'' + ", subExpression='" + subExpression + '\'' + ", isOrderly=" + isOrderly
                + ", isBroadcast=" + isBroadcast + ", consumerThreadCountMin=" + consumerThreadCountMin
                + ", consumerThreadCountMax=" + consumerThreadCountMax + ", consumeMessageBatchMaxSize="
                + consumeMessageBatchMaxSize + ", prefix='" + prefix + '\'' + '}';
    }
}
