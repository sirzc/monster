package com.yunli.mq.customer;

import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.exception.MqConsumerConfigException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * pull 消息
 *
 * @author zhouchao
 * @date 2019-02-20 10:01
 */
public class FastMQPullConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FastMQPullConsumer.class);

    private ConsumerBuildConfig config;

    private DefaultMQPullConsumer consumer;

    public FastMQPullConsumer(ConsumerBuildConfig config) {
        this.config = config;
    }

    /**
     * 初创建pull消费者
     */
    private void init() throws MQClientException {
        if (null == config) {
            throw new MqConsumerConfigException("mq init error,build config is null");
        }
        // 创建消费者实体
        consumer = new DefaultMQPullConsumer();
        consumer.setConsumerGroup(config.getGroupName());
        consumer.setNamesrvAddr(config.getNameServer());
        if (!StringUtils.isEmpty(config.getInstanceName())) {
            consumer.setInstanceName(config.getInstanceName());
        }
        logger.info("mq init {},Rocket MQ:UnitopMQPullConsumer use config:{}", config.getGroupName(), config);
        consumer.start();
    }

    /**
     * @param topic         topic to subscribe.
     * @param subExpression subscription expression.it only support or operation such as "tag1 || tag2 || tag3" <br>
     * @param maxNums       最大获取量
     * @throws Exception
     */
    public void getMessgae(String topic, String subExpression, int maxNums) throws Exception {
        List<MessageQueue> linkedList = new ArrayList<>(consumer.fetchSubscribeMessageQueues(topic));
        Collections.sort(linkedList);
        List<MessageExt> results = new ArrayList<>();
        for (MessageQueue mq : linkedList) {
            // 对于从存储端获取消费进度
            long offset = consumer.fetchConsumeOffset(mq, true);
            PullResult pullResult = consumer.pullBlockIfNotFound(mq, subExpression, offset, maxNums);
            consumer.getOffsetStore().updateOffset(mq, pullResult.getNextBeginOffset(), false);
            consumer.getOffsetStore().persist(mq);
            results.addAll(pullResult.getMsgFoundList());
            if (results.size() == maxNums) {
                break;
            } else {
                maxNums = maxNums - results.size();
            }
        }
        // System.err.println(new String(m.getBody(), StandardCharsets.UTF_8));
    }

}
