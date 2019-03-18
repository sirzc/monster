package com.yunli.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;

/**
 * 消费者1
 *
 * @author zhouchao
 * @date 2019-02-20 14:44
 */
public class Consumer1 {
    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ONLY_ONE_1");
        consumer.setNamesrvAddr("127.0.0.1:9999");
//        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.setInstanceName("8888");
        consumer.subscribe("TOPIC8", "TAG2");
        consumer.registerMessageListener(new MyMqListener());
        consumer.start();
    }
}
