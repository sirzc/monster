package com.yunli.mq.producer.enums;

import com.alibaba.fastjson.JSON;
import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.producer.config.ProducerBuildConfig;
import com.yunli.mq.producer.config.StandardMessageQueueSelector;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 消息发送模式
 *
 * @author zc151
 * @date 2019-01-16 21:52
 */
public enum ProducerTransferEnum {

    /**
     * 同步模式
     */
    SYNC {
        @Override
        protected void sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
                throws MqBusinessException {
            final Logger logger = LoggerFactory.getLogger(ProducerTransferEnum.class);
            try {
                SendResult sendResult = producer.send(message);
                SendStatus sendStatus = sendResult.getSendStatus();
                if (!Objects.equals(sendStatus, SendStatus.SEND_OK)) {
                    logger.info("sendResult advice：{}", sendResult);
                } else {
                    logger.debug("sendResult advice：{}", sendResult);
                }
            } catch (Exception e) {
                throw new MqBusinessException(e);
            }
        }

        @Override
        protected void sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
                throws MqBusinessException {
            final Logger logger = LoggerFactory.getLogger(ProducerTransferEnum.class);
            try {
                SendResult sendResult = producer.send(message, new StandardMessageQueueSelector(), config.getSort());
                SendStatus sendStatus = sendResult.getSendStatus();
                if (!Objects.equals(sendStatus, SendStatus.SEND_OK)) {
                    logger.info("sendResult advice：{}", sendResult);
                } else {
                    logger.debug("sendResult advice：{}", sendResult);
                }
            } catch (Exception e) {
                throw new MqBusinessException(e);
            }
        }
    },

    /**
     * 异步模式
     */
    ASYNC {
        @Override
        public void sendMsg(DefaultMQProducer producer, ProducerBuildConfig config) throws MqBusinessException {
            if (Objects.isNull(config.getCallback())) {
                throw new MqBusinessException(
                        "async mode must set callback. Please set CustomMessageConfig.SendCallback");
            }
            super.sendMsg(producer, config);
        }

        @Override
        protected void sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
                throws MqBusinessException {
            try {
                producer.send(message, config.getCallback());
            } catch (Exception e) {
                throw new MqBusinessException(e);
            }
        }

        @Override
        protected void sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
                throws MqBusinessException {
            try {
                producer.send(message, new StandardMessageQueueSelector(), config.getSort(), config.getCallback());
            } catch (Exception e) {
                throw new MqBusinessException(e);
            }
        }
    },

    /**
     * ONE—WAY 模式
     */
    ONEWAY {
        @Override
        protected void sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
                throws MqBusinessException {
            try {
                producer.sendOneway(message);
            } catch (Exception e) {
                throw new MqBusinessException(e);
            }
        }

        @Override
        protected void sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
                throws MqBusinessException {
            try {
                producer.sendOneway(message, new StandardMessageQueueSelector(), config.getSort());
            } catch (Exception e) {
                throw new MqBusinessException(e);
            }
        }
    };

    ProducerTransferEnum() {

    }

    /**
     * 普通消息
     *
     * @param producer
     * @param message
     * @param config
     * @throws MqBusinessException
     */
    protected abstract void sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException;

    /**
     * 顺序消息
     *
     * @param producer
     * @param message
     * @param config
     * @throws MqBusinessException
     */
    protected abstract void sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
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
     * @throws MqBusinessException
     */
    public void sendMsg(DefaultMQProducer producer, ProducerBuildConfig config) throws MqBusinessException {
        Message message = ProducerTransferEnum.formatMessage(config);
        if (!Objects.isNull(message)) {
            // 判断消息类型：无序、有序
            if (Objects.isNull(config.getSort())) {
                this.sendMsg(producer, message, config);
                return;
            }
            // 发送有序消息
            this.sendOrderlyMsg(producer, message, config);
        }
    }
}
