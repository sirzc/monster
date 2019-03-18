package com.yunli.mq.producer.strategy;

import com.alibaba.fastjson.JSON;
import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.producer.config.ProducerBuildConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.Objects;

/**
 * 消息发送 抽象类
 *
 * @author zhouchao
 * @date 2019-01-29 11:19
 */
public abstract class AbstractSend {

    /**
     * 普通消息
     *
     * @param producer
     * @param message
     * @param config
     * @return
     * @throws MqBusinessException
     */
    protected abstract SendResult sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config) throws
            MqBusinessException;

    /**
     * 顺序消息
     *
     * @param producer
     * @param message
     * @param config
     * @return
     * @throws MqBusinessException
     */
    protected abstract SendResult sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException;

    /**
     * 格式化消息为RocketMQ消息样式
     *
     * @param config
     * @return
     * @throws MqBusinessException
     */
    private static Message formatMessage(ProducerBuildConfig config) throws MqBusinessException {
        try {
            byte[] body = JSON.toJSONString(config.getMessage()).getBytes(config.getCharSet());
            Message message = new Message(config.getTopic(), config.getTags(), config.getKeys(), body);
            if (Objects.nonNull(config.getDelayLevel())) {
                message.setDelayTimeLevel(config.getDelayLevel().getLevel());
            }
            return message;
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }

    }

    /**
     * 发送消息
     *
     * @param producer 生产者实例
     * @param config   消息包装
     * @return
     * @throws MqBusinessException
     */
    public SendResult sendMsg(DefaultMQProducer producer, ProducerBuildConfig config) throws MqBusinessException {
        Message message = formatMessage(config);
        if (Objects.isNull(message)) {
           throw new MqBusinessException("com.yunli.mq.mq message is null...");
        }
        // 判断消息类型：无序、有序
        if (Objects.isNull(config.getSort())) {
            return this.sendMsg(producer, message, config);
        }
        // 发送有序消息
        return this.sendOrderlyMsg(producer, message, config);
    }
}
