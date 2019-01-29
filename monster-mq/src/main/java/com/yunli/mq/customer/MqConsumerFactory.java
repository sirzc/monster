package com.yunli.mq.customer;

import com.google.common.collect.Lists;
import com.yunli.mq.common.MqConstant;
import com.yunli.mq.common.PropertiesUtil;
import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.customer.handler.IMqHandler;
import com.yunli.mq.exception.MqConsumerConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者工厂类
 *
 * @author zhouchao
 * @date 2019-01-18 16:24
 */
public class MqConsumerFactory {

    private static final Logger                            logger     =
            LoggerFactory.getLogger(MqConsumerFactory.class);
    /**
     * 消费者实例对象集合
     */
    private static       Map<String, FastMqPushConsumer> consumers  = new ConcurrentHashMap<>();
    private static       Properties                        properties = PropertiesUtil.getConsumerProperties();
    private static       MqConsumerFactory                 factory;

    private MqConsumerFactory() {
        super();
    }

    /**
     * 获取消费者工厂类
     *
     * @return
     */
    public static MqConsumerFactory getFactory() {
        if (factory == null) {
            synchronized (MqConsumerFactory.class) {
                if (factory == null) {
                    factory = new MqConsumerFactory();
                }
            }
        }
        return factory;
    }

    /**
     * 获取需要配置的
     *
     * @return
     */
    public List<String> listPrefix() {
        String start = properties.getProperty(MqConstant.MQ_START_CONSUMER, MqConstant.DEFAULT_MQ_PREFIX);
        String[] configs = start.split(MqConstant.DEFAULT_MQ_SEPARATOR);
        List<String> prefixList = Lists.newArrayList();
        for (String prefix : configs) {
            if (!prefixList.contains(prefix)) {
                prefixList.add(prefix);
            }
        }
        return prefixList;
    }

    /**
     * 构建消费者
     *
     * @param prefix
     */
    public void buildConsumer(String prefix) {
        if (consumers.containsKey(prefix)) {
            return;
        }
        if (!checkValid(prefix)) {
            throw new MqConsumerConfigException(
                    "mq build consumer error,properties nameServer or topic or listener is null.");
        }
        consumers.put(prefix, new FastMqPushConsumer(getBuildConfig(prefix)));
    }

    /**
     * 校验配置有效性
     *
     * @param prefix 获取的配置
     * @return
     */
    private boolean checkValid(String prefix) {
        // 校验服务是否配置
        if (StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_NAMESERVER))) {
            return false;
        }
        // 校验是否配置订阅
        if (StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_SUBSCRIBE))) {
            return false;
        }
        // 校验是否配置监听
        if (StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_LISTENER_HANDLER))) {
            return false;
        }
        return true;
    }

    /**
     * 组装配置
     *
     * @param prefix
     * @return
     */
    private ConsumerBuildConfig getBuildConfig(String prefix) {
        ConsumerBuildConfig config = new ConsumerBuildConfig();
        config.setNameServer(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_NAMESERVER));
        config.setGroupName(prefix);
        String[] subscribe = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_SUBSCRIBE).split(MqConstant.DEFAULT_MQ_SEPARATOR);
        config.setTopic(subscribe[0]);
        if (subscribe.length > 1) {
            config.setSubExpression(subscribe[1]);
        }
        String instanceName = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_INSTANCENAME);
        if (!StringUtils.isEmpty(instanceName)) {
            config.setInstanceName(instanceName);
        }
        String timeout = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_TIMEOUT);
        if (!StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_TIMEOUT))) {
            config.setConsumeTimeout(Long.parseLong(timeout));
        }
        String messageSize = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_MESSAGE_SIZE);
        if (!StringUtils.isEmpty(messageSize)) {
            config.setConsumeMessageBatchMaxSize(Integer.parseInt(messageSize));
        }
        String threadMin = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_THREAD_MIN);
        if (!StringUtils.isEmpty(threadMin)) {
            config.setThreadCountMin(Integer.parseInt(threadMin));
        }
        String threadMax = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_THREAD_MAX);
        if (!StringUtils.isEmpty(threadMax)) {
            config.setThreadCountMax(Integer.parseInt(threadMax));
        }
        String isBroadcast =properties.getProperty(prefix + MqConstant.MQ_CONSUMER_BROADCAST);
        if (!StringUtils.isEmpty(isBroadcast)) {
            config.setBroadcast(Boolean.parseBoolean(isBroadcast));
        }
        String isOrderly = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_ORDERLY);
        if (!StringUtils.isEmpty(isOrderly)) {
            config.setOrderly(Boolean.parseBoolean(isOrderly));
        }
        // 注册主题对应的监听器
        String iHandler = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_LISTENER_HANDLER);
        config.setTopicHandler(registerTopicHandler(config.getTopic(), iHandler));
        return config;
    }

    /**
     * 注册topic处理器
     *
     * @param topic
     * @param iHandler
     * @return
     */
    private Map<String, List<IMqHandler>> registerTopicHandler(String topic, String iHandler) {
        Map<String, List<IMqHandler>> topicHandler = new HashMap<>(5);
        List<IMqHandler> iMqHandlers = new ArrayList<>();
        String[] callbackAddress = iHandler.split(MqConstant.DEFAULT_MQ_SEPARATOR);
        for (String callbackName : callbackAddress) {
            try {
                // callbackName完整的类名称，即待回调的类
                Class<?> cc = Class.forName(callbackName);
                // 默认的接口名称
                Class<?> interfaceClass = Class.forName(MqConstant.CONSUMER_INTERFACE_CLASSNAME);
                // 检查当前类是否继承了 默认的接口
                boolean rightStatus = Arrays.asList(cc.getInterfaces()).contains(interfaceClass);
                if (!rightStatus) {
                    logger.error("mq consumer register error. class:{} not implement {}", callbackName,
                            MqConstant.CONSUMER_INTERFACE_CLASSNAME);
                    continue;
                }
                // newInstance:相当于先加载到类即（classs.forName）,再实例化
                IMqHandler mqHandler = (IMqHandler) cc.newInstance();
                iMqHandlers.add(mqHandler);
            } catch (Exception e) {
                logger.error("mq consumer register error. analysis class:{}", callbackName, e);
            }
        }
        topicHandler.put(topic,iMqHandlers);
        return topicHandler;
    }

    /**
     * 获取消费者
     * @param prefix
     * @return
     */
    public FastMqPushConsumer getConsumer(String prefix) {
        return consumers.get(prefix);
    }
}
