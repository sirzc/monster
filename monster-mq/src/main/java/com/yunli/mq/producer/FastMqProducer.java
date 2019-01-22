package com.yunli.mq.producer;

import com.yunli.mq.common.MessageData;
import com.yunli.mq.common.MqConstant;
import com.yunli.mq.common.PropertiesUtil;
import com.yunli.mq.exception.MqBusinessException;
import com.yunli.mq.exception.MqProducerConfigException;
import com.yunli.mq.producer.config.ProducerBuildConfig;
import com.yunli.mq.producer.enums.ProducerTransferEnum;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Properties;

/**
 * 自定义消费者
 *
 * @author zhouchao
 * @date 2019-01-16 9:17
 */
public class FastMqProducer {

    private static final Logger logger = LoggerFactory.getLogger(FastMqProducer.class);

    /**
     * 获取配置文件操作类
     */
    private static Properties prop = PropertiesUtil.getProducerProperties();

    private DefaultMQProducer producer;

    public FastMqProducer(String prefix) {
        init(prefix);
    }

    /**
     * 初始化生产者，并启动
     *
     * @param prefix 配置属性的前缀
     */
    private void init(String prefix) {
        if (!checkVaild(prefix)) {
            throw new MqProducerConfigException(">>>>>>>>>>>>> mq load " + prefix + " properties error.<<<<<<<<<<<<<");
        }
        // 创建Rocket MQ client 实例
        producer = new DefaultMQProducer(prop.getProperty(prefix + MqConstant.MQ_PRODUCER_GROUPNAME));
        producer.setNamesrvAddr(prop.getProperty(prefix + MqConstant.MQ_PRODUCER_NAMESERVER));
        // 设置默认队列数
        String topicQueueNums = prop.getProperty(prefix + MqConstant.MQ_PRODUCER_TOPICQUEUENUMS);
        if (!StringUtils.isEmpty(topicQueueNums)) {
            producer.setDefaultTopicQueueNums(Integer.parseInt(topicQueueNums));
        }
        // 设置超时时间
        String timeout = prop.getProperty(prefix + MqConstant.MQ_PRODUCER_SENDTIMEOUTMILLIS);
        if (!StringUtils.isEmpty(timeout)) {
            producer.setSendMsgTimeout(Integer.parseInt(timeout));
        }
        // 客户端实例名称
        String instanceName = prop.getProperty(prefix + MqConstant.MQ_PRODUCER_INSTANCENAME);
        if (!StringUtils.isEmpty(instanceName)) {
            producer.setInstanceName(instanceName);
        }
        try {
            producer.start();
            logger.info(">>>>>>>>>>>>> mq producer start {} properties", prefix);
        } catch (MQClientException e) {
            String warn = ">>>>>>>>>>>>> mq producer start " + prefix + " properties error.";
            logger.warn(warn, e);
            throw new MqProducerConfigException(warn, e);
        }
    }

    /**
     * 关闭生产者
     */
    public void shutdown() {
        producer.shutdown();
    }

    /**
     * 检查生产者配置文件有效性
     *
     * @param prefix
     * @return
     */
    private boolean checkVaild(String prefix) {
        String nameserver = prop.getProperty(prefix + MqConstant.MQ_PRODUCER_NAMESERVER);
        if (StringUtils.isEmpty(nameserver)) {
            return false;
        }

        String groupName = prop.getProperty(prefix + MqConstant.MQ_PRODUCER_GROUPNAME);
        if (StringUtils.isEmpty(groupName)) {
            return false;
        }

        String topicQueueNums = prop.getProperty(prefix + MqConstant.MQ_PRODUCER_TOPICQUEUENUMS);
        if (!StringUtils.isEmpty(topicQueueNums) && !topicQueueNums.matches("[0-9]+")) {
            return false;
        }
        String timeout = prop.getProperty(prefix + MqConstant.MQ_PRODUCER_SENDTIMEOUTMILLIS);
        if (!StringUtils.isEmpty(timeout) && !timeout.matches("[0-9]+")) {
            return false;
        }
        return true;

    }

    /**
     * 检查消息配置有效性
     *
     * @param config
     * @throws MqBusinessException
     */
    private void checkConfig(ProducerBuildConfig config) throws MqBusinessException {
        if (Objects.isNull(config)) {
            throw new MqBusinessException("message config not null");
        }
        if (Objects.isNull(config.getSendMode())) {
            throw new MqBusinessException("message send mode not null");
        }
        if (StringUtils.isEmpty(config.getTopic())) {
            throw new MqBusinessException("message topic not null");
        }
        if (Objects.isNull(config.getMessage())) {
            throw new MqBusinessException("message content not null");
        }
    }

    /**
     * 定制化发送
     *
     * @param config
     * @throws MqBusinessException
     */
    public void sendMsg(ProducerBuildConfig config) throws MqBusinessException {
        // 校验消息合法
        checkConfig(config);
        // 判断消息模式
        if (Objects.nonNull(config.getCallback())) {
            config.setSendMode(ProducerTransferEnum.ASYNC);
        }
        ProducerTransferEnum sendMode = config.getSendMode();
        sendMode.sendMsg(producer, config);
    }

    /**
     * ****************************** 【同步发送】 *********************************
     */

    public void send(String topic, MessageData msg) throws MqBusinessException {
        send(topic, "", "", msg, null);
    }

    public void send(String topic, String tags, MessageData msg) throws MqBusinessException {
        send(topic, tags, "", msg, null);
    }

    public void send(String topic, String tags, String keys, MessageData msg) throws MqBusinessException {
        send(topic, tags, keys, msg, null);
    }

    /**
     * ****************************** 【异步发送】 **********************************
     */

    public void send(String topic, MessageData msg, SendCallback callback) throws MqBusinessException {
        send(topic, "", "", msg, callback);
    }

    public void send(String topic, String tags, MessageData msg, SendCallback callback) throws MqBusinessException {
        send(topic, tags, "", msg, callback);
    }

    /**
     * 发送消息
     *
     * @param topic    主题
     * @param tags     标签
     * @param keys
     * @param msg      消息内容
     * @param callback 回调函数（异步：须填写）
     * @throws MqBusinessException
     */
    public void send(String topic, String tags, String keys, MessageData msg, SendCallback callback)
            throws MqBusinessException {
        ProducerBuildConfig config = new ProducerBuildConfig(topic, tags, keys, msg);
        // 回调函数不为空，则发送模式设置为异步
        if (Objects.nonNull(callback)) {
            config.setSendMode(ProducerTransferEnum.ASYNC);
        }
        // 校验消息合法
        checkConfig(config);
        // 发送消息
        config.getSendMode().sendMsg(producer, config);
    }
}
