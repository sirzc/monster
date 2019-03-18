package com.yunli.mq;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 消息监听
 *
 * @author zhouchao
 * @create 2018-12-12 14:55
 */
public class MyMqListener implements MessageListenerConcurrently {

    private static final Logger logger = LoggerFactory.getLogger(MyMqListener.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt me : msgs) {
            try {
                logger.info("{}-收到新的消息：{}", Thread.currentThread().getName(), new String(me.getBody(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

}
