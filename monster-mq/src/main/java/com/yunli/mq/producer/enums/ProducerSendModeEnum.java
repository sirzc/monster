package com.yunli.mq.producer.enums;

import com.alibaba.fastjson.JSON;
import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.exception.MqWrapperException;
import com.yunli.mq.producer.config.CustomMessageConfig;
import com.yunli.mq.producer.config.StandardMessageQueueSelector;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.Objects;

/**
 * 消息发送模式
 *
 * @author zc151
 * @date 2019-01-16 21:52
 */
public enum ProducerSendModeEnum {
    /**
     * 同步模式
     */
    SYNC {
        @Override
        protected void sendMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                SendResult sendResult = producer.send(message);
                SendStatus sendStatus = sendResult.getSendStatus();
                if (!Objects.equals(sendStatus, SendStatus.SEND_OK)) {
                    throw new MqBusinessException("消息发送异常. 返回：sendStatus:" + sendStatus.name());
                }
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }

        @Override
        protected void sendOrderlyMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                SendResult sendResult = producer.send(message, new StandardMessageQueueSelector(), config.getSort());
                SendStatus sendStatus = sendResult.getSendStatus();
                if (!Objects.equals(sendStatus, SendStatus.SEND_OK)) {
                    throw new MqBusinessException("消息发送异常. 返回：sendStatus:" + sendStatus.name());
                }
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }
    },

    /**
     * 异步模式
     */
    ASYNC {
        @Override
        public void sendMsg(DefaultMQProducer producer, CustomMessageConfig config)
                throws MqWrapperException, MqBusinessException {
            if (Objects.isNull(config.getCallback())) {
                throw new MqBusinessException("异步模式必须添加回调函数. 请设置CustomMessageConfig.SendCallback属性");
            }
            super.sendMsg(producer, config);
        }

        @Override
        protected void sendMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                producer.send(message, config.getCallback());
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }

        @Override
        protected void sendOrderlyMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                producer.send(message, new StandardMessageQueueSelector(), config.getSort(), config.getCallback());
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }
    },

    /**
     * ONE—WAY 模式
     */
    ONEWAY {
        @Override
        protected void sendMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                producer.sendOneway(message);
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }

        @Override
        protected void sendOrderlyMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                producer.sendOneway(message, new StandardMessageQueueSelector(), config.getSort());
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }
    };

    ProducerSendModeEnum() {

    }

    /**
     * 普通消息
     *
     * @param producer
     * @param message
     * @param config
     * @throws MqWrapperException
     */
    protected abstract void sendMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
            throws MqWrapperException;

    /**
     * 顺序消息
     *
     * @param producer
     * @param message
     * @param config
     * @throws MqWrapperException
     */
    protected abstract void sendOrderlyMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
            throws MqWrapperException;

    /**
     * 格式化消息为RocketMQ消息样式
     *
     * @param config
     * @return
     * @throws MqWrapperException
     */
    private static Message formatMessage(CustomMessageConfig config) throws MqWrapperException {
        try {
            byte[] body = JSON.toJSONString(config.getMessage()).getBytes(config.getCharSet());
            Message message = new Message(config.getTopic(), config.getTags(), config.getKeys(), body);
            if (Objects.nonNull(config.getDelayLevel())) {
                message.setDelayTimeLevel(config.getDelayLevel().getLevel());
            }
            return message;
        } catch (Exception e) {
            throw new MqWrapperException(e);
        }

    }

    /**
     * 发送消息
     *
     * @param producer 生产者实例
     * @param config   消息包装
     * @throws MqWrapperException
     * @throws MqBusinessException
     */
    public void sendMsg(DefaultMQProducer producer, CustomMessageConfig config)
            throws MqWrapperException, MqBusinessException {
        Message message = ProducerSendModeEnum.formatMessage(config);
        if (!Objects.isNull(message)) {
            // 判断消息发送方式
            if (Objects.isNull(config.getSort())) {
                this.sendMsg(producer, message, config);
                return;
            }
            // 发送有序消息
            this.sendOrderlyMsg(producer, message, config);
        }
    }
}
