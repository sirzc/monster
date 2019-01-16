package com.yunli.mq.producer.enums;

import com.alibaba.fastjson.JSON;
import com.yunli.mq.exception.MqBussinesException;
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
public enum ProducerSendMode {

    SYNC {
        @Override
        protected void sendMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                SendResult sendResult = producer.send(message);
                SendStatus sendStatus = sendResult.getSendStatus();
                if (!Objects.equals(sendStatus, SendStatus.SEND_OK)) {
                    throw new MqBussinesException("send message return not ok. sendStatus:" + sendStatus.name());
                }
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }

        @Override
        protected void sendMsgOrderly(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                SendResult sendResult = producer.send(message, new StandardMessageQueueSelector(),
                        config.getSort());
                SendStatus sendStatus = sendResult.getSendStatus();
                if (!Objects.equals(sendStatus, SendStatus.SEND_OK)) {
                    throw new MqBussinesException("send message return not ok. sendStatus:" + sendStatus.name());
                }
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }
    },
    ASYNC {
        @Override
        public void sendMsg(DefaultMQProducer producer, CustomMessageConfig config)
                throws MqWrapperException, MqBussinesException {
            if (Objects.isNull(config.getCallback())) {
                throw new MqBussinesException("ASYNC mode need callback. plz set up CustomMessageConfig.callback");
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
        protected void sendMsgOrderly(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                producer.send(message, new StandardMessageQueueSelector(), config.getSort(), config.getCallback());
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }
    },

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
        protected void sendMsgOrderly(DefaultMQProducer producer, Message message, CustomMessageConfig config)
                throws MqWrapperException {
            try {
                producer.sendOneway(message, new StandardMessageQueueSelector(), config.getSort());
            } catch (Exception e) {
                throw new MqWrapperException(e);
            }
        }
    };

    private ProducerSendMode() {

    }

    private static Message genMessage(CustomMessageConfig config) throws MqWrapperException {
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

    public void sendMsg(DefaultMQProducer producer, CustomMessageConfig config)
            throws MqWrapperException, MqBussinesException {
        Message message = ProducerSendMode.genMessage(config);
        if (!Objects.isNull(message)) {
            if (Objects.isNull(config.getSort())) {
                this.sendMsg(producer, message, config);
                return;
            }
            this.sendMsgOrderly(producer, message, config);
        }
    }

    protected abstract void sendMsg(DefaultMQProducer producer, Message message, CustomMessageConfig config)
            throws MqWrapperException;

    protected abstract void sendMsgOrderly(DefaultMQProducer producer, Message message, CustomMessageConfig config)
            throws MqWrapperException;
}
