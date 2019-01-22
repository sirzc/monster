package com.yunli.mq.customer;

import com.yunli.mq.common.MqConstant;
import com.yunli.mq.common.PropertiesUtil;
import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.customer.enums.CustomerPushTransferEnum;
import com.yunli.mq.exception.MqConsumerConfigException;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * unitop-mq push消费者
 *
 * @author zhouchao
 * @date 2019-01-18 15:09
 */
public class FastMqPushConsumer {

    private DefaultMQPushConsumer consumer;

    /**
     * 获取配置文件操作类
     */
    private static Properties props = PropertiesUtil.getConsumerProperties();

    public FastMqPushConsumer(String prefix) {
        init(prefix);
    }

    /**
     * 初始化加载静态配置
     *
     * @param prefix 引用配置文件前缀（切换配置）
     */
    private void init(String prefix) {
        // 检验配置的有效性
        if (!checkVaild(prefix)) {
            throw new MqConsumerConfigException("unitop mq consumer config properties error.");
        }

        // 创建消费者实例
        consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(props.getProperty(prefix + MqConstant.MQ_CONSUMER_NAMESERVER));

        // 客户端创建的多个Producer、 Consumer实际是共用一个内部实例（这个实例包含网络连接、线程资源等）
        if (!StringUtils.isEmpty(props.getProperty(prefix + MqConstant.MQ_CONSUMER_INSTANCENAME))) {
            consumer.setInstanceName(props.getProperty(prefix + MqConstant.MQ_CONSUMER_INSTANCENAME));
        }

        // 消息可能阻止使用线程的最长时间（以分钟为单位,默认：15）。
        String timeout = props.getProperty(prefix + MqConstant.MQ_CONSUMER_TIMEOUT);
        if (!StringUtils.isEmpty(timeout)) {
            consumer.setConsumeTimeout(Long.parseLong(timeout));
        }
    }

    /**
     * 校验静态资源配置
     *
     * @param prefix 引用配置文件前缀（切换配置）
     * @return
     */
    private boolean checkVaild(String prefix) {
        String nameserver = props.getProperty(prefix + MqConstant.MQ_CONSUMER_NAMESERVER);
        if (StringUtils.isEmpty(nameserver)) {
            return false;
        }

        String timeout = props.getProperty(prefix + MqConstant.MQ_CONSUMER_TIMEOUT);
        // 不为数字且不为空
        if (!StringUtils.isEmpty(timeout) && !timeout.matches("[0-9]+")) {
            return false;
        }
        return true;
    }

    /**
     * 加载动态配置
     *
     * @param config 从客户端注解中获取的相关配置
     * @throws MQClientException
     */
    public void loadBuildConfig(ConsumerBuildConfig config) throws MQClientException {
        consumer.setConsumerGroup(config.getGroupName());
        consumer.setConsumeThreadMin(config.getConsumerThreadCountMin());
        consumer.setConsumeThreadMax(config.getConsumerThreadCountMax());
        consumer.setConsumeMessageBatchMaxSize(config.getConsumeMessageBatchMaxSize());
        // 订阅主题
        for (String topic : config.getTopicHandler().keySet()) {
            consumer.subscribe(topic, config.getSubExpression());
        }
        // 是否为广播模式
        if (config.isBroadcast()) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }
        // 是否为顺序模式，注册处理程序
        if (config.isOrderly()) {
            CustomerPushTransferEnum.PUSH_ORDERLY.registerHandlers(consumer, config.getTopicHandler());
        } else {
            CustomerPushTransferEnum.PUSH_DISORDER.registerHandlers(consumer, config.getTopicHandler());
        }

    }

    /**
     * 启动消费者
     *
     * @throws MQClientException
     */
    public void start() throws MQClientException {
        consumer.start();
    }

    /**
     * 关闭消费者
     */
    public void shutdown() {
        consumer.shutdown();
    }
}
