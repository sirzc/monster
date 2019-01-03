package com.yunli.monster.core.mq.core;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单号生成
 *
 * @author zhouchao
 * @create 2019-01-02 13:44
 */
public class OrderProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);

    private DefaultMQProducer producer = new DefaultMQProducer("OrderProducer-1");

    /**
     * 初始化
     */
    public void init() {
        producer.setNamesrvAddr("10.1.55.180:9876");
        producer.setRetryTimesWhenSendFailed(3);
        try {
            producer.start();
        } catch (MQClientException e) {
            logger.error("生产者创建异常");
            e.printStackTrace();
        }
    }

    /**
     * 销毁
     */
    public void close() {
        producer.shutdown();
    }

    /**
     * 消息发送
     * @param topic 主题
     * @param tags  标签
     * @param msg   内容
     * @return 发送结果
     */
    public SendResult send(String topic, String tags, String msg) {
        SendResult result = null;
        try {
            Message message = new Message(topic, tags, (msg).getBytes("utf-8"));
            result = producer.send(message, (mqs, msg1, arg) -> {
                Integer id = arg.hashCode();
                int index = id % mqs.size();
                return mqs.get(Math.abs(index));
            }, tags);
        } catch (Exception e) {
            logger.error("消息发送异常");
            e.printStackTrace();
        }
        return result;
    }

}
