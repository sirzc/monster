package com.unitop.mq.producer;

import com.unitop.mq.common.MQConstantPool;
import com.unitop.mq.exception.MqProducerConfigException;
import com.unitop.mq.common.MessageData;
import com.unitop.mq.exception.MqBusinessException;
import com.unitop.mq.exception.MqWrapperException;
import com.unitop.mq.producer.config.CustomMessageConfig;
import com.unitop.mq.producer.enums.ProducerSendModeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

/**
 * 自定义消费者
 *
 * @author zhouchao
 * @date 2019-01-16 9:17
 */
public class UnitopMQProducer {
    private static final Logger logger = LoggerFactory.getLogger(UnitopMQProducer.class);

    private DefaultMQProducer producer;

    public UnitopMQProducer(Properties prop) {
        init(prop);
    }

    /**
     * 初始化生产者，并启动
     *
     * @param prop
     */
    private void init(Properties prop) {
        if (!checkVaild(prop)) {
            throw new MqProducerConfigException("unitop-mq配置文件加载异常");
        }
        // 创建Rocket MQ client 实例
        producer = new DefaultMQProducer(prop.getProperty(MQConstantPool.UNITOP_PRODUCER_GROUPNAME));
        producer.setNamesrvAddr(prop.getProperty(MQConstantPool.UNITOP_PRODUCER_NAMESERVER));
        // 设置默认队列数
        String topicQueueNums = prop.getProperty(MQConstantPool.UNITOP_PRODUCER_TOPICQUEUENUMS);
        if (!StringUtils.isEmpty(topicQueueNums)) {
            producer.setDefaultTopicQueueNums(Integer.parseInt(topicQueueNums));
        }
        // 设置超时时间
        String timeout = prop.getProperty(MQConstantPool.UNITOP_PRODUCER_TIMEOUT);
        if (!StringUtils.isEmpty(timeout)) {
            producer.setSendMsgTimeout(Integer.parseInt(timeout));
        }
        // 客户端实例名称
        String instanceName = prop.getProperty(MQConstantPool.UNITOP_PRODUCER_INSTANCENAME);
        if (!StringUtils.isEmpty(instanceName)) {
            producer.setInstanceName(instanceName);
        }
        try {
            producer.start();
            logger.info("unitop-mq 正常启动，生产者启动，当前配置为:" + prop);
        } catch (MQClientException e) {
            String warn = "unitop-mq 启动异常，生产者加载异常，当前配置为:" + prop;
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
     * @param props
     * @return
     */
    private boolean checkVaild(Properties props) {
        String nameserver = props.getProperty(MQConstantPool.UNITOP_PRODUCER_NAMESERVER);
        if (StringUtils.isEmpty(nameserver)) {
            return false;
        }
        String groupname = props.getProperty(MQConstantPool.UNITOP_PRODUCER_GROUPNAME);
        if (StringUtils.isEmpty(groupname)) {
            return false;
        }
        String topicQueueNums = props.getProperty(MQConstantPool.UNITOP_PRODUCER_TOPICQUEUENUMS);
        if (!StringUtils.isEmpty(topicQueueNums) && !topicQueueNums.matches("[0-9]+")) {
            return false;
        }
        String timeout = props.getProperty(MQConstantPool.UNITOP_PRODUCER_TIMEOUT);
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
    private void checkConfig(CustomMessageConfig config) throws MqBusinessException {
        if (Objects.isNull(config)) {
            throw new MqBusinessException("消息配置不能为空.");
        }
        if (Objects.isNull(config.getSendMode())) {
            throw new MqBusinessException("消息配置: 发送模式不能为空.");
        }
        if (StringUtils.isEmpty(config.getTopic())) {
            throw new MqBusinessException("消息配置: 消息主题不能为空.");
        }
        if (Objects.isNull(config.getMessage())) {
            throw new MqBusinessException("消息配置: 消息内容不能为空.");
        }
    }

    /**
     * 定制化发送
     *
     * @param config
     * @throws MqBusinessException
     * @throws MqWrapperException
     */
    public void sendMsg(CustomMessageConfig config) throws MqBusinessException, MqWrapperException {
        // 校验消息合法
        checkConfig(config);
        // 判断消息模式
        if (Objects.nonNull(config.getCallback())) {
            config.setSendMode(ProducerSendModeEnum.ASYNC);
        }
        ProducerSendModeEnum sendMode = config.getSendMode();
        sendMode.sendMsg(producer, config);
    }

    /**
     * ****************************** 【同步发送】 *********************************
     */

    public void send(String topic, MessageData msg) throws MqBusinessException, MqWrapperException {
        send(topic, "", "", msg, null);
    }

    public void send(String topic, String tags, MessageData msg) throws MqBusinessException, MqWrapperException {
        send(topic, tags, "", msg, null);
    }

    public void send(String topic, String tags, String keys, MessageData msg)
            throws MqBusinessException, MqWrapperException {
        send(topic, tags, keys, msg, null);
    }

    /**
     * ****************************** 【异步发送】 **********************************
     */

    public void send(String topic, MessageData msg, SendCallback callback)
            throws MqBusinessException, MqWrapperException {
        send(topic, "", "", msg, callback);
    }

    public void send(String topic, String tags, MessageData msg, SendCallback callback)
            throws MqBusinessException, MqWrapperException {
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
     * @throws MqWrapperException
     */
    public void send(String topic, String tags, String keys, MessageData msg, SendCallback callback)
            throws MqBusinessException, MqWrapperException {
        CustomMessageConfig config = new CustomMessageConfig(topic, tags, keys, msg);
        // 回调函数不为空，则发送模式设置为异步
        if (Objects.nonNull(callback)) {
            config.setSendMode(ProducerSendModeEnum.ASYNC);
        }
        // 校验消息合法
        checkConfig(config);
        // 发送消息
        config.getSendMode().sendMsg(producer, config);
    }
}
