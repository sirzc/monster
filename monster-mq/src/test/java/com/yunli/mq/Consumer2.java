package com.yunli.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 消费者2
 *
 * @author zhouchao
 * @date 2019-02-20 14:44
 */
public class Consumer2 {
    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ONLY_ONE");
        consumer.setNamesrvAddr("127.0.0.1:9999");
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.setInstanceName("8080");
        consumer.subscribe("TOPIC2", "*");
        consumer.registerMessageListener(new MyMqListener());
        consumer.start();
    }
}
