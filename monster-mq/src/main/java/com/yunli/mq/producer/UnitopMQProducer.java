package com.yunli.mq.producer;

import com.yunli.mq.common.MQConstantPool;
import com.yunli.mq.exception.MqProducerConfigException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            throw new MqProducerConfigException("unitop wrong. producer config properties error.");
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
            logger.info("unitop-mq running. a producer started. use properties:" + prop);
        } catch (MQClientException e) {
            String warn = "unitop-mq wrong. producer init failed. prop:" + prop;
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
     * 检查有效性
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

//    private void checkConfig(EasyMQMessageConfig config) throws MqBussinessException {
//        if (Objects.isNull(config)) {
//            throw new MqBussinessException("EasyMQMessageConfig config can not be null.");
//        }
//        if (Objects.isNull(config.getTransferMode())) {
//            throw new MqBussinessException("EasyMQMessageConfig transferMode can not be null.");
//        }
//        if (StringUtils.isEmpty(config.getTopic())) {
//            throw new MqBussinessException("EasyMQMessageConfig topic can not be empty.");
//        }
//        if (StringUtils.isEmpty(config.getKeys())) {
//            throw new MqBussinessException("EasyMQMessageConfig keys can not be empty.");
//        }
//        if (StringUtils.isEmpty(config.getMessage())) {
//            throw new MqBussinessException("EasyMQMessageConfig message can not be empty.");
//        }
//        if (StringUtils.isEmpty(config.getCharSet())) {
//            throw new MqBussinessException("EasyMQMessageConfig charset can not be empty.");
//        }
//        if (StringUtils.isEmpty(config.getTags())) {
//            throw new MqBussinessException("EasyMQMessageConfig tags can not be empty.");
//        }
//    }

    /**
     * 发送方式同步，编码UTF-8。
     *
     * @param topic
     * @param keys
     * @param msg
     * @throws MqWapperException
     * @throws MqBussinessException
     */
//    public void sendMsg(String topic, String keys, String msg) throws MqWapperException, MqBussinessException {
//        EasyMQMessageConfig config = new EasyMQMessageConfig(topic, keys, msg);
//        config.getTransferMode().sendMsg(producer, config);
//    }

    /**
     * 发送方式异步，编码为UTF-8。
     *
     * @param topic
     * @param keys
     * @param msg
     * @param callback
     * @throws MqWapperException
     * @throws MqBussinessException
     */
//    public void sendMsg(String topic, String keys, String msg, SendCallback callback)
//            throws MqWapperException, MqBussinessException {
//        EasyMQMessageConfig config = new EasyMQMessageConfig(topic, keys, msg);
//        config.setTransferMode(ProducerTransferMode.ASYNC);
//        config.setCallback(callback);
//        config.getTransferMode().sendMsg(producer, config);
//    }

    /**
     * 定制化发送
     *
     * @param config
     * @throws MqWapperException
     * @throws MqBussinessException
     */
//    public void sendMsg(EasyMQMessageConfig config) throws MqWapperException, MqBussinessException {
//        checkConfig(config);
//        if (Objects.nonNull(config.getCallback())) {
//            config.setTransferMode(ProducerTransferMode.ASYNC);
//        }
//        ProducerTransferMode transferMode = config.getTransferMode();
//        transferMode.sendMsg(producer, config);
//    }

}
