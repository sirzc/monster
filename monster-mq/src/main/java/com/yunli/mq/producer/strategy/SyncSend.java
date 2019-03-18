package com.yunli.mq.producer.strategy;

import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.producer.config.ProducerBuildConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步发送：指消息发送方发出数据后，会在收到接收方发回响应之后才发下一个数据包的通讯方式。</br>
 * <p>
 * 应用场景：此种方式应用场景非常广泛，例如重要通知邮件、报名短信通知、营销短信系统等。
 *
 * @author zhouchao
 * @date 2019-01-29 10:10
 */
public class SyncSend extends AbstractSend {
    private static final Logger logger = LoggerFactory.getLogger(SyncSend.class);

    @Override
    protected SendResult sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException {
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
        return sendResult;
    }

    @Override
    protected SendResult sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException {
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message, new SelectMessageQueueByHash(), config.getSort());
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
        return sendResult;
    }
}
