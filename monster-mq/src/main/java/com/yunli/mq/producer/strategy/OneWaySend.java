package com.yunli.mq.producer.strategy;

import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.producer.config.ProducerBuildConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;

/**
 * one-way策略：单向（Oneway）发送特点为发送方只负责发送消息，不等待服务器回应且没有回调函数触发，
 * 即只发送请求不等待应答。 此方式发送消息的过程耗时非常短，一般在微秒级别。<br/>
 *
 * 应用场景：适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集。
 *
 * @author zhouchao
 * @date 2019-01-29 13:52
 */
public class OneWaySend extends AbstractSend {

    @Override
    protected SendResult sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException {
        try {
            producer.sendOneway(message);
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
        return null;
    }

    @Override
    protected SendResult sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException {
        try {
            producer.sendOneway(message, new SelectMessageQueueByHash(), config.getSort());
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
        return null;
    }
}
