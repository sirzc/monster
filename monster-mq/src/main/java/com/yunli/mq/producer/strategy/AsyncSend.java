package com.yunli.mq.producer.strategy;

import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.producer.config.ProducerBuildConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;

import java.util.Objects;

/**
 * 异步发送策略：异步发送是指发送方发出数据后，不等接收方发回响应，接着发送下个数据包的通讯方式。
 * 消息队列 RocketMQ 的异步发送，需要用户实现异步发送回调接口（SendCallback）。
 * 消息发送方在发送了一条消息后，不需要等待服务器响应即可返回，进行第二条消息发送。
 * 发送方通过回调接口接收服务器响应，并对响应结果进行处理。</br>
 * <p>
 * 应用场景：异步发送一般用于链路耗时较长，对 RT 响应时间较为敏感的业务场景，
 * 例如用户视频上传后通知启动转码服务，转码完成后通知推送转码结果等。
 *
 * @author zhouchao
 * @date 2019-01-29 10:10
 */
public class AsyncSend extends AbstractSend {

    @Override
    public SendResult sendMsg(DefaultMQProducer producer, ProducerBuildConfig config) throws MqBusinessException {
        if (Objects.isNull(config.getCallback())) {
            throw new MqBusinessException("async mode must set callback. Please set CustomMessageConfig.SendCallback");
        }
        super.sendMsg(producer, config);
        return null;
    }

    @Override
    protected SendResult sendMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException {
        try {
            producer.send(message, config.getCallback());
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
        return null;
    }

    @Override
    protected SendResult sendOrderlyMsg(DefaultMQProducer producer, Message message, ProducerBuildConfig config)
            throws MqBusinessException {
        try {
            producer.send(message, new SelectMessageQueueByHash(), config.getSort(), config.getCallback());
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
        return null;
    }

}
