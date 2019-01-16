package com.yunli.mq.producer.config;

import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;


/**
 * 标准消息选择器
 *
 * @author zc151
 * @date 2019-01-16 21:52
 */

public class StandardMessageQueueSelector implements MessageQueueSelector {

    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
        Integer id = arg.hashCode();
        int index = id % mqs.size();
        return mqs.get(index);
    }

}