package com.yunli.mq.customer.pull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yunli.mq.common.MessageData;
import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.customer.handler.IMqHandler;
import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.exception.MqConsumerConfigException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * pull 消息
 *
 * @author zhouchao
 * @date 2019-02-20 10:01
 */
public class FastMqPullConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FastMqPullConsumer.class);

    private ConsumerBuildConfig config;

    private DefaultMQPullConsumer consumer;

    public FastMqPullConsumer(ConsumerBuildConfig config) {
        this.config = config;
        init();
    }

    /**
     * 初创建pull消费者
     */
    private void init() {
        if (null == config) {
            throw new MqConsumerConfigException("com.yunli.mq.mq init error,build config is null");
        }
        try {
            // 创建消费者实体
            consumer = new DefaultMQPullConsumer();
            consumer.setConsumerGroup(config.getGroupName());
            consumer.setNamesrvAddr(config.getNameServer());
            if (!StringUtils.isEmpty(config.getInstanceName())) {
                consumer.setInstanceName(config.getInstanceName());
            }
            consumer.start();
            logger.info("com.yunli.mq.mq start pull consumer use config:{}", config.getGroupName());
        } catch (MQClientException e) {
            logger.error("com.yunli.mq.mq pull consumer start error.", e);
            e.printStackTrace();
        }
    }

    /**
     * 消息队列选择
     *
     * @param mqs 消息队列
     * @param arg 队列因子
     * @return
     */
    private MessageQueue select(List<MessageQueue> mqs, Object arg) {
        int value = arg.hashCode();
        if (value < 0) {
            value = Math.abs(value);
        }
        value = value % mqs.size();
        return mqs.get(value);
    }

    /**
     * 回调函数
     *
     * @param handler     执行方法
     * @param messageExts 获取到的消息
     */
    private void callback(IMqHandler handler, List<MessageExt> messageExts) throws Exception {
        // 解析消费
        List<MessageData> messages = new ArrayList();
        for (MessageExt messageExt : messageExts) {
            String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            MessageData messageData = JSON.parseObject(message, new TypeReference<MessageData>() {
            });
            messages.add(messageData);
        }
        handler.handle(messages);
    }

    /**
     * 从队列中获取消息
     *
     * @param queues
     * @param subExpression
     * @param offset        消费位点
     * @param maxNum
     * @return
     * @throws MqBusinessException
     */
    private List<MessageExt> pullMessage(List<MessageQueue> queues, String subExpression, Long offset, int maxNum)
            throws MqBusinessException {
        List<MessageExt> results = new ArrayList<>();
        int pullNum = maxNum;
        boolean updateStore = offset == null || offset < 0;
        try {
            for (MessageQueue mq : queues) {
                // 从broker中获取消费进度，且重新定义更新消费进度
                if (updateStore) {
                    offset = consumer.fetchConsumeOffset(mq, true);
                }
                // 拉取消息
                PullResult pullResult = consumer.pullBlockIfNotFound(mq, subExpression, offset, pullNum);
                // 检查消息状态
                switch (pullResult.getPullStatus()) {
                    case FOUND:
                        if (updateStore) {
                            consumer.getOffsetStore().updateOffset(mq, pullResult.getNextBeginOffset(), false);
                            consumer.getOffsetStore().persist(mq);
                        }
                        results.addAll(pullResult.getMsgFoundList());
                        break;
                    default:
                        break;
                }
                // 检查消息数量
                if (results.size() == maxNum) {
                    break;
                }
                pullNum = maxNum - results.size();
            }
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
        return results;
    }

    /**
     * 拉取消息，外部调用的入口
     *
     * @param handler 消息处理器
     * @param arg     与消息队列选择器一起使用的参数
     */
    public synchronized void pull(IMqHandler handler, int maxNum, Object arg) throws MqBusinessException {
        List<MessageExt> exts = new ArrayList<>();
        String topic = config.getTopic();
        String subExpression = config.getSubExpression();
        maxNum = maxNum > 0 ? maxNum : config.getConsumeMessageBatchMaxSize();
        try {
            Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues(topic);
            List<MessageQueue> queues = new ArrayList<>(messageQueues);
            Collections.sort(queues);
            if (Objects.isNull(arg)) {
                exts.addAll(pullMessage(queues, subExpression, null, maxNum));
            } else {
                MessageQueue mq = select(queues, arg);
                exts.addAll(pullMessage(Arrays.asList(mq), subExpression, null, maxNum));
            }
            callback(handler, exts);
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
    }

    /**
     * 指定消费位点消费类，默认每次从0开始获取
     *
     * @param handler 回调
     * @param offset  消息的起始位置
     * @param maxNum  消费的最大消息数
     */
    public void pull(IMqHandler handler, Long offset, int maxNum) throws MqBusinessException {
        List<MessageExt> exts = new ArrayList<>();
        String topic = config.getTopic();
        String subExpression = config.getSubExpression();
        maxNum = maxNum > 0 ? maxNum : config.getConsumeMessageBatchMaxSize();
        try {
            Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues(topic);
            List<MessageQueue> queues = new ArrayList<>(messageQueues);
            Collections.sort(queues);
            exts.addAll(pullMessage(queues, subExpression, offset, maxNum));
            callback(handler, exts);
        } catch (Exception e) {
            throw new MqBusinessException(e);
        }
    }
}
